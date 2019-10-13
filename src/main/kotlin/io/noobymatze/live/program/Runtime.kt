package io.noobymatze.live.program

import com.fasterxml.jackson.core.JsonProcessingException
import io.noobymatze.live.html.Attribute
import io.noobymatze.live.html.BrowserEvent
import io.noobymatze.live.html.Html
import io.noobymatze.live.page.LiveProgramBehavior
import io.noobymatze.live.page.internal.JSON
import org.apache.wicket.Application
import org.apache.wicket.protocol.ws.api.registry.IKey
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry
import java.io.Serializable
import java.util.*


/**
 * The Runtime manages the current state of the given program and
 * runs
 */
class Runtime<Flags, Model, Msg>(
    initialModel: Pair<Model, Cmd<Msg>>?,
    private val flags: Flags,
    private val program: Program<Flags, Model, Msg>
): Serializable {


    /**
     * The [State] holds the current state of the [Program].
     */
    sealed class State<Model, Msg>: Serializable {

        /**
         * Means, that a Client has never been connected. This is
         * in contrast to [NotConnected].
         */
        class NeverConnected<Model, Msg>: State<Model, Msg>()

        /**
         * Means, that a Client has been connected, but is currently
         * disconnected.
         *
         * @param model the last known model
         */
        data class NotConnected<Model, Msg>(
            val model: Model
        ): State<Model, Msg>()

        /**
         * Means, that a Client is currently connected.
         *
         * @param model the current model
         * @param session a session
         * @param handlers
         */
        data class Connected<Model, Msg>(
            val model: Model,
            val session: Session,
            val handlers: Map<Int, Attribute.Value.Listener<Msg>>
        ): State<Model, Msg>()

    }


    /**
     * A [Session] contains all necessary information to identify
     * a specific connection.
     *
     * @param applicationKey a key for the current WicketApplication
     * @param key a key
     * @param sessionId the actual sessionId
     */
    data class Session(
        val applicationKey: String,
        val key: IKey,
        val sessionId: String
    ): Serializable


    /**
     * Defines all the messages, which can be sent to the
     *
     */
    sealed class Message<out Msg> {
        data class Diff<out Msg>(val html: Html<Msg>): Message<Msg>()
    }


    private var state: State<Model, Msg> =
        initialModel?.first
            ?.let { State.NotConnected<Model, Msg>(it) }
            ?: State.NeverConnected()


    /**
     *
     *
     */
    fun connected(session: Session): Unit = state.let {
        when (it) {
            is State.NeverConnected -> {
                val (model, cmd) = program.init(flags)
                val html = program.view(model)
                val handlers = html.replaceHandlers()
                send(session, Message.Diff(html))
                this.state = State.Connected(model, session, handlers)
            }

            is State.NotConnected -> {
                val html = program.view(it.model)
                val handlers = html.replaceHandlers()
                send(session, Message.Diff(html))
                this.state = State.Connected(it.model, session, handlers)
            }

            is State.Connected ->
                this.state = it.copy(session = session)
        }
    }

    /**
     *
     */
    fun handle(event: BrowserEvent): Unit = state.let { state ->
        when (state) {
            is State.Connected -> {
                val handler = state.handlers[event.handlerId]
                    ?: return

                val inputMsg = handler(event.payload)
                val (newModel, cmd) = program.update(inputMsg, state.model)

                if (newModel != state.model) {
                    val html = program.view(newModel)
                    val handlers = html.replaceHandlers()
                    this.state = state.copy(model = newModel, handlers = handlers)
                    send(state.session, Message.Diff(html))
                }

                val messages = runCmds(cmd)
                val (nextModel, model) = applyMessages(messages, state.model)
            }
        }
    }

    /**
     *
     */
    fun disconnected(): Unit = when (state) {
        is State.Connected -> {
            this.state = State.NotConnected((state as State.Connected).model)
        }

        else ->
            Unit
    }


    /**
     *
     * @param messages
     */
    private fun applyMessages(messages: List<Msg>, model: Model): Pair<Model, Cmd<Msg>> {
        if (messages.isEmpty()) {
            return model to Cmd.none
        }

        val nextCommands = mutableListOf<Cmd<Msg>>()
        var currentModel = model
        messages.forEach {
            val (nextModel, cmd) = program.update(it, model)
            currentModel = nextModel
            nextCommands.add(cmd)
        }

        return currentModel to Cmd.batch(nextCommands)
    }

    /**
     *
     *
     * @param cmd
     * @return a list of the messages, which need to be applied to
     */
    private fun <Msg> runCmds(cmd: Cmd<Msg>): List<Msg> {
        val stack = Stack<Cmd<Msg>>().apply { push(cmd) }
        val messages = mutableListOf<Msg>()

        while (stack.isNotEmpty()) {
            when (val curCmd = stack.pop()) {
                is Cmd.Batch ->
                    curCmd.commands.reversed().forEach { stack.push(it) }

                is Cmd.Effect<*, *> -> {
                    val result = try {
                        Result.Success<Throwable, Any?>(curCmd.run())
                    } catch (t: Throwable) {
                        Result.Failure<Throwable, Any?>(t)
                    }

                    curCmd.toMsg(result)
                }
            }
        }

        return messages
    }

    /**
     * Send the given [message] to the [session].
     *
     * @param session a live session
     * @param message
     */
    private fun send(session: Session, message: Message<Msg>) = try {
        SimpleWebSocketConnectionRegistry()
            .getConnection(Application.get(session.applicationKey), session.sessionId, session.key)
            ?.takeIf { it.isOpen }
            ?.sendMessage(JSON.unsafeStringify(message))
    } catch (ex: JsonProcessingException) {
        // logger.error(ex) { "Error while trying to send current html" }
    }

}
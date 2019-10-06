package io.noobymatze.live.page

import com.fasterxml.jackson.core.JsonProcessingException
import io.noobymatze.live.page.html.Attribute
import io.noobymatze.live.page.html.BrowserEvent
import io.noobymatze.live.page.html.Html
import io.noobymatze.live.page.internal.JSON
import mu.KotlinLogging
import org.apache.wicket.Application
import org.apache.wicket.Component
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.protocol.ws.api.WebSocketBehavior
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage
import org.apache.wicket.protocol.ws.api.message.TextMessage
import org.apache.wicket.protocol.ws.api.registry.IKey
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry
import org.apache.wicket.request.resource.PackageResourceReference
import java.io.Serializable


internal class LiveBehavior<Model: Serializable, Msg: Serializable>(
    private val program: Program<Model, Msg>
) : WebSocketBehavior() {


    /**
     * Track the current state.
     */
    private var state: State<Model, Msg> = State.NeverConnected()


    override fun onConnect(message: ConnectedMessage) {
        super.onConnect(message)

        logger.info { "Client connected ${message.key}" }

        this.state = update(state, Session(
            key = message.key,
            sessionId = message.sessionId,
            applicationKey = message.application.applicationKey
        ))
    }


    override fun onMessage(websocketHandler: WebSocketRequestHandler, message: TextMessage) {
        super.onMessage(websocketHandler, message)

        try {
            logger.debug { "Received message ${message.text}" }

            val event = JSON.unsafeParse(message.text, BrowserEvent::class)

            this.state = update(state, event)
        } catch (ex: JsonProcessingException) {
            logger.error(ex) { "Error while reading the message: ${message.text}"}
        }
    }


    /**
     * Update the given [state], when a client connects.
     *
     * @param state the current state
     * @param session a
     * @return a new state based on the current state
     */
    private fun update(
        state: State<Model, Msg>,
        session: Session
    ): State<Model, Msg> = when (state) {
        is State.NeverConnected -> {
            val model = program.init()
            val html = program.view(model)
            val handlers = html.replaceHandlers()
            send(session, html)
            State.Connected(model, session, handlers)
        }

        is State.Disconnected -> {
            val html = program.view(state.model)
            val handlers = html.replaceHandlers()
            send(session, html)
            State.Connected(state.model, session, handlers)
        }

        is State.Connected ->
            state.copy(session = session)
    }

    /**
     * Update the given [State] when a client sends a message.
     *
     * @param state the current state
     * @param message a BrowserEvent
     * @return a new [State], depending on the
     * current state
     */
    private fun update(
        state: State<Model, Msg>,
        message: BrowserEvent
    ): State<Model, Msg> = when (state) {
        is State.NeverConnected ->
            state

        is State.Disconnected ->
            state

        is State.Connected -> when (val handler = state.handlers[message.handlerId]) {
            null ->
                state

            else -> {
                val newModel = program.update(handler(message.payload), state.model)
                val html = program.view(newModel)
                val handlers = html.replaceHandlers()
                send(state.session, html)
                state.copy(model = newModel, handlers = handlers)
            }
        }
    }

    override fun renderHead(component: Component, response: IHeaderResponse) {
        super.renderHead(component, response)

        response.render(JavaScriptReferenceHeaderItem.forReference(
            PackageResourceReference(LiveBehavior::class.java, "morphdom-umd.min.js")
        ))

        response.render(JavaScriptReferenceHeaderItem.forReference(
            PackageResourceReference(LiveBehavior::class.java, "LiveBehavior.js")
        ))
    }

    /**
     * Send the given [message] to the [session].
     *
     * @param session a live session
     * @param message
     */
    private fun send(session: Session, message: Html<Msg>) = try {
        SimpleWebSocketConnectionRegistry()
            .getConnection(Application.get(session.applicationKey), session.sessionId, session.key)
            ?.takeIf { it.isOpen }
            ?.sendMessage(JSON.unsafeStringify(message))
    } catch (ex: JsonProcessingException) {
        logger.error(ex) { "Error while trying to send current html" }
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
     * The [State] tracks the current state of the connection with
     * the client.
     */
    sealed class State<Model, Msg>: Serializable {

        class NeverConnected<Model, Msg>: State<Model, Msg>()

        data class Disconnected<Model, Msg>(
            val model: Model
        ): State<Model, Msg>()

        data class Connected<Model, Msg>(
            val model: Model,
            val session: Session,
            val handlers: Map<Int, Attribute.Handler.Fn<Msg>>
        ): State<Model, Msg>()

    }

    companion object {

        private val logger = KotlinLogging.logger { }

    }

}
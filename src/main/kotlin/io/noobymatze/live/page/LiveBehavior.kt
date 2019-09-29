package io.noobymatze.live.page

import com.fasterxml.jackson.core.JsonProcessingException
import io.noobymatze.live.page.html.Attribute.Event.Handler
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
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry
import org.apache.wicket.request.resource.PackageResourceReference


internal class LiveBehavior<Model, Msg>(
    val program: Program<Model, Msg>
) : WebSocketBehavior() {

    private var model: Model? = null // TODO: AtomicReference?
    private var liveSession: LiveSession? = null
    private var handlers = mutableMapOf<Int, Handler.Fn<Msg>>()

    override fun onConnect(message: ConnectedMessage) {
        super.onConnect(message)

        logger.info { "Client connected ${message.key}" }

        this.liveSession = LiveSession(
            key = message.key,
            sessionId = message.sessionId,
            applicationKey = message.application.applicationKey
        )

        if (model == null) {
            this.model = program.init()
        }

        model?.let {
            val html = program.view(it)
            liveSession?.let { session ->
                send(session, html)
            }
        }
    }

    override fun onMessage(websocketHandler: WebSocketRequestHandler, message: TextMessage) {
        super.onMessage(websocketHandler, message)

        try {
            logger.info { "Received message ${message.text}" }

            val event = JSON.unsafeParse(message.text, BrowserEvent::class)
            val handler = handlers[event.handlerId]

            if (handler == null) {
                logger.error { "Unkown message, ignoring ${message.text}" }
                return
            }

            model?.let {
                val newModel = program.update(handler(event.payload), it)
                val html = program.view(newModel)
                this.model = newModel
                websocketHandler.push(JSON.unsafeStringify(html))
            }
        } catch (ex: JsonProcessingException) {
            logger.error(ex) { "Error while reading the message: ${message.text}"}
        }
    }

    private fun send(session: LiveSession, message: Html<Msg>) = try {
        SimpleWebSocketConnectionRegistry()
            .getConnection(Application.get(session.applicationKey), session.sessionId, session.key)
            ?.takeIf { it.isOpen }
            ?.sendMessage(JSON.unsafeStringify(message))
    } catch (ex: JsonProcessingException) {
        logger.error(ex) { "Error while trying to send current html" }
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

    companion object {
        private val logger = KotlinLogging.logger { }
    }

}
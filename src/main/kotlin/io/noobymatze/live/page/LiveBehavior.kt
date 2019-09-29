package io.noobymatze.live.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.noobymatze.live.page.internal.BrowserMessage
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
    val program: Program<Model, Msg>,
    val msgClass: Class<Msg>
) : WebSocketBehavior() {

    private var model: Model? = null // TODO: AtomicReference?
    private var liveSession: LiveSession? = null

    override fun onConnect(message: ConnectedMessage) {
        super.onConnect(message)
        println("Client connected ${message.key}")

        this.liveSession = LiveSession(
            key = message.key,
            sessionId = message.sessionId,
            applicationKey = message.application.applicationKey
        )

        if (model == null) {
            val model = program.init()
            val result = program.view(model)
            val mapper = ObjectMapper().registerKotlinModule()
            send(liveSession!!, mapper.writeValueAsString(result))
            this.model = model
        }
    }

    private fun send(session: LiveSession, message: String) {
        println(message)
        SimpleWebSocketConnectionRegistry()
            .getConnection(Application.get(session.applicationKey), session.sessionId, session.key)
            ?.takeIf { it.isOpen }
            ?.sendMessage(message)
    }

    override fun onMessage(handler: WebSocketRequestHandler, message: TextMessage) {
        super.onMessage(handler, message)
        logger.info { "Received message ${message.text}" }

        val mapper = ObjectMapper().registerKotlinModule()
        val msg = mapper.readValue(message.text, msgClass)

        model?.let {
            val newModel = program.update(msg, it)
            val html = program.view(newModel)
            this.model = newModel
            handler.push(mapper.writeValueAsString(html))
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

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
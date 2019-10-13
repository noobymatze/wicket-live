package io.noobymatze.live.page

import com.fasterxml.jackson.core.JsonProcessingException
import io.noobymatze.live.html.BrowserEvent
import io.noobymatze.live.page.internal.JSON
import io.noobymatze.live.program.Runtime
import mu.KotlinLogging
import org.apache.wicket.Component
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.protocol.ws.api.WebSocketBehavior
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler
import org.apache.wicket.protocol.ws.api.message.ClosedMessage
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage
import org.apache.wicket.protocol.ws.api.message.TextMessage
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.request.resource.PackageResourceReference
import java.io.Serializable


/**
 *
 * @param
 */
class LiveProgramBehavior<Model: Serializable, Msg: Serializable>(
    private val runtime: Runtime<PageParameters, Model, Msg>
) : WebSocketBehavior() {

    override fun onConnect(message: ConnectedMessage) {
        super.onConnect(message)

        logger.info { "Client connected ${message.key}" }

        runtime.connected(Runtime.Session(
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

            runtime.handle(event)
        } catch (ex: JsonProcessingException) {
            logger.error(ex) { "Error while reading the message: ${message.text}"}
        }
    }


    override fun onClose(message: ClosedMessage) {
        super.onClose(message)

        runtime.disconnected()
    }


    override fun renderHead(component: Component, response: IHeaderResponse) {
        super.renderHead(component, response)

        response.render(JavaScriptReferenceHeaderItem.forReference(
            PackageResourceReference(LiveProgramBehavior::class.java, "morphdom-umd.min.js")
        ))

        response.render(JavaScriptReferenceHeaderItem.forReference(
            PackageResourceReference(LiveProgramBehavior::class.java, "internal/LiveDom.js")
        ))

        response.render(JavaScriptReferenceHeaderItem.forReference(
            PackageResourceReference(LiveProgramBehavior::class.java, "LiveBehavior.js")
        ))
    }


    companion object {

        private val logger = KotlinLogging.logger { }

    }

}
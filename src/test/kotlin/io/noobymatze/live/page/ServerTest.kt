package io.noobymatze.live.page

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.noobymatze.live.page.internal.WicketInstanceHandle
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.DefaultByteBufferPool
import io.undertow.servlet.Servlets
import io.undertow.servlet.websockets.WebSocketServlet
import io.undertow.websockets.jsr.ServerWebSocketContainer
import io.undertow.websockets.jsr.WebSocketDeploymentInfo
import org.apache.wicket.protocol.http.WicketFilter
import org.apache.wicket.protocol.http.WicketServlet
import org.apache.wicket.protocol.ws.javax.JavaxWebSocketFilter
import org.apache.wicket.protocol.ws.javax.WicketEndpoint
import org.apache.wicket.protocol.ws.javax.WicketServerEndpointConfig
import org.junit.Test
import javax.websocket.*
import javax.websocket.server.ServerEndpoint

class Test {

    @Test
    fun x() {
        val mapper = ObjectMapper().registerKotlinModule()
        println(mapper.writeValueAsString(Msg.Inc))
    }

}

object ServerTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val nordref = Servlets.deployment()
            .setClassLoader(ServerTest::class.java.classLoader)
            .setContextPath("/")
            .setDeploymentName("nordref")
            .addServletContextAttribute(
                WebSocketDeploymentInfo.ATTRIBUTE_NAME,
                WebSocketDeploymentInfo()
                    .setBuffers(DefaultByteBufferPool(true, 100))
                    .addEndpoint(WicketServerEndpointConfig())
            )
            .addServlet(Servlets.servlet("nordref", WicketServlet::class.java, WicketInstanceHandle {
                LivePageTestApplication()
            }).addInitParam(JavaxWebSocketFilter.FILTER_MAPPING_PARAM, "/*").addMapping("/*"))

        val manager = Servlets.defaultContainer()
            .addDeployment(nordref).apply {
                deploy()
            }



        // START UNDERTOW


        val handler = Handlers.path()
            .addPrefixPath("/", manager.start())

        val server = Undertow.builder()
            .addHttpListener(8080, "localhost")
            .setHandler(handler)
            .build()

        server.start()
    }
}
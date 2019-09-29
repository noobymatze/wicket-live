package io.noobymatze.live.page.internal

import io.undertow.servlet.api.InstanceFactory
import io.undertow.servlet.api.InstanceHandle
import org.apache.wicket.protocol.http.IWebApplicationFactory
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.protocol.http.WicketFilter
import org.apache.wicket.protocol.http.WicketServlet
import org.apache.wicket.protocol.ws.javax.JavaxWebSocketFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class WicketInstanceHandle(
    val create: () -> WebApplication
): InstanceFactory<WicketServlet> {

    override fun createInstance(): InstanceHandle<WicketServlet> = object: InstanceHandle<WicketServlet> {
        override fun release() = Unit

        override fun getInstance(): WicketServlet =
            WicketFunctionServlet(create)
    }


    private class WicketFunctionServlet(private val create: () -> WebApplication) : WicketServlet() {

        override fun newWicketFilter(): WicketFilter = object: JavaxWebSocketFilter() {
            override fun getApplicationFactory(): IWebApplicationFactory = object: IWebApplicationFactory {
                override fun destroy(p0: WicketFilter?) = Unit
                override fun createApplication(p0: WicketFilter?): WebApplication =
                    create()
            }
        }

    }

}

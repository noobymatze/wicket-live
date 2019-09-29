package io.noobymatze.live.page

import org.apache.wicket.Page
import org.apache.wicket.protocol.http.WebApplication


class LivePageTestApplication: WebApplication() {

    override fun getHomePage(): Class<out Page> =
        ExamplePage::class.java

}
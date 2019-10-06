package io.noobymatze.live.page

import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.IMarkupResourceStreamProvider
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.util.resource.IResourceStream
import org.apache.wicket.util.resource.StringResourceStream
import java.io.Serializable


abstract class LivePage<Model: Serializable, Msg: Serializable>(
    params: PageParameters
): WebPage(params), Program<Model, Msg>, IMarkupResourceStreamProvider {

    private val model = init()

    override fun onInitialize() {
        super.onInitialize()

        add(LiveBehavior(this, model))
    }

    override fun getMarkupResourceStream(
        p0: MarkupContainer?,
        p1: Class<*>?
    ): IResourceStream = StringResourceStream("""
        |<!DOCTYPE html>
        |
        |<html>
        |  <head>
        |    <title>Test</title>
        |  </head>
        |  <body>
        |    <div id="app">${view(model).render()}</div>
        |  </body>
        |</html>
    """.trimMargin())

}
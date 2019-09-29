package io.noobymatze.live.page

import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.IMarkupResourceStreamProvider
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.util.resource.IResourceStream
import org.apache.wicket.util.resource.StringResourceStream


abstract class LivePage<Model, Msg>(
    params: PageParameters,
    val msgClass: Class<Msg>
): WebPage(params), Program<Model, Msg>, IMarkupResourceStreamProvider {

    override fun onInitialize() {
        super.onInitialize()

        add(LiveBehavior<Model, Msg>(this, msgClass))
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
        |    <div id="app"></div>
        |  </body>
        |</html>
    """.trimMargin())

}
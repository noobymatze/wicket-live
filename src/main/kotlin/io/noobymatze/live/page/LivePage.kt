package io.noobymatze.live.page

import io.noobymatze.live.program.Cmd
import io.noobymatze.live.program.Program
import io.noobymatze.live.program.Runtime
import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.IMarkupResourceStreamProvider
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.model.LoadableDetachableModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.util.resource.IResourceStream
import org.apache.wicket.util.resource.StringResourceStream
import java.io.Serializable


abstract class LivePage<Model: Serializable, Msg: Serializable>(
    params: PageParameters
): WebPage(params), Program<PageParameters, Model, Msg>, IMarkupResourceStreamProvider {

    private val model = object: LoadableDetachableModel<Pair<Model, Cmd<Msg>>>() {
        override fun load(): Pair<Model, Cmd<Msg>> = init(params)
    }

    override fun onInitialize() {
        super.onInitialize()

        add(LiveProgramBehavior(Runtime(model.`object`, pageParameters, this)))
    }

    override fun getMarkupResourceStream(
        p0: MarkupContainer,
        p1: Class<*>
    ): IResourceStream = StringResourceStream("""
        |<!DOCTYPE html>
        |
        |<html>
        |  <head>
        |    <title>Test</title>
        |  </head>
        |  <body>
        |    <div id="app">${view(model.`object`?.first!!).render()}</div>
        |  </body>
        |</html>
    """.trimMargin())

}
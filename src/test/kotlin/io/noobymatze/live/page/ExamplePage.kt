package io.noobymatze.live.page

import io.noobymatze.live.page.ExamplePage.Msg
import io.noobymatze.live.page.html.Attributes.attribute
import io.noobymatze.live.page.html.Events.onClick
import io.noobymatze.live.page.html.Html
import io.noobymatze.live.page.html.Html.Companion.button
import io.noobymatze.live.page.html.Html.Companion.div
import io.noobymatze.live.page.html.Html.Companion.text
import org.apache.wicket.request.mapper.parameter.PageParameters
import java.io.Serializable


class ExamplePage(params: PageParameters): LivePage<Int, Msg>(params) {

    sealed class Msg: Serializable {
        object Inc: Msg()
        object Dec: Msg()
    }

    override fun init(): Int = 0

    override fun update(msg: Msg, model: Int): Int = when (msg) {
        is Msg.Inc ->
            model + 1

        is Msg.Dec ->
            model - 1
    }

    override fun view(model: Int): Html<Msg> = div(
        div(text(model.toString())),
        div(button(
            listOf(
                onClick(Msg.Inc),
                attribute("data-test", "Test")
            ),
            text("Increment")
        )),
        div(button(
            listOf(onClick(Msg.Dec)),
            text("Decrement")
        ))
    )
}
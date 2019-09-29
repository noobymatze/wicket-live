package io.noobymatze.live.page

import com.fasterxml.jackson.annotation.JsonSubTypes

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.noobymatze.live.page.ExamplePage.*
import io.noobymatze.live.page.html.Events.onClick
import io.noobymatze.live.page.html.Html
import io.noobymatze.live.page.html.Html.Companion.button
import io.noobymatze.live.page.html.Html.Companion.div
import io.noobymatze.live.page.html.Html.Companion.text
import org.apache.wicket.request.mapper.parameter.PageParameters
import java.io.Serializable


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Msg.Inc::class, name = "Inc"),
    JsonSubTypes.Type(value = Msg.Dec::class, name = "Dec")
)
sealed class Msg(val type: String): Serializable {
    object Inc: Msg("Inc")
    object Dec: Msg("Dec")
}

class ExamplePage(params: PageParameters): LivePage<Int, Msg>(params, Msg::class.java) {


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
            listOf(onClick(Msg.Inc)),
            text("Increment")
        )),
        div(button(
            listOf(onClick(Msg.Dec)),
            text("Decrement")
        ))
    )
}
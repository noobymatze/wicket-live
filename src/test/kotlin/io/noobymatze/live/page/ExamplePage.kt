package io.noobymatze.live.page

import io.noobymatze.live.page.ExamplePage.Model
import io.noobymatze.live.page.ExamplePage.Msg
import io.noobymatze.live.page.html.Attributes.attribute
import io.noobymatze.live.page.html.Attributes.classList
import io.noobymatze.live.page.html.Attributes.name
import io.noobymatze.live.page.html.Attributes.type
import io.noobymatze.live.page.html.Attributes.value
import io.noobymatze.live.page.html.Events
import io.noobymatze.live.page.html.Events.onClick
import io.noobymatze.live.page.html.Html
import io.noobymatze.live.page.html.Html.Companion.button
import io.noobymatze.live.page.html.Html.Companion.div
import io.noobymatze.live.page.html.Html.Companion.form
import io.noobymatze.live.page.html.Html.Companion.input
import io.noobymatze.live.page.html.Html.Companion.text
import org.apache.wicket.request.mapper.parameter.PageParameters
import java.io.Serializable


class ExamplePage(params: PageParameters): LivePage<Model, Msg>(params) {


    // MODEL


    data class Model(
        val count: Int,
        val name: String?
    ): Serializable


    override fun init(): Model = Model(0, null)



    // VIEW


    override fun view(model: Model): Html<Msg> = div(
        div(text(model.count.toString())),
        div(listOf(
            classList("bla" to (model.count < 5))
        ),
            button(
                listOf(
                    onClick(Msg.Inc),
                    attribute("data-test", "Test")
                ),
                text("Increment"))
        ),
        div(button(
            listOf(onClick(Msg.Dec)),
            text("Decrement")
        )),
        div(text("Hello ${model.name ?: " World"}!")),
        viewForm(model.name)
    )

    private fun viewForm(name: String?): Html<Msg> =
        form(
            listOf(
                Events.onSubmit { Msg.Submit(it["name"] as String) }
            ),
            input(type("text"), name("name"), value(name ?: "")),
            input(type("number"), name("age")),
            button(text("Save"))
        )



    // UPDATE


    sealed class Msg : Serializable {
        object Inc : Msg()
        object Dec : Msg()
        data class Submit(val name: String): Msg()
    }

    override fun update(msg: Msg, model: Model): Model = when (msg) {
        is Msg.Inc ->
            model.copy(count = model.count + 1)

        is Msg.Dec ->
            model.copy(count = model.count - 1)

        is Msg.Submit ->
            model.copy(name = msg.name)

    }

}
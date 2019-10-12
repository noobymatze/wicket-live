package io.noobymatze.live.page

import io.noobymatze.live.page.ExamplePage.Model
import io.noobymatze.live.page.ExamplePage.Msg
import io.noobymatze.live.page.ExamplePage.Msg.*
import io.noobymatze.live.page.html.Attributes
import io.noobymatze.live.page.html.Attributes.attribute
import io.noobymatze.live.page.html.Attributes.classList
import io.noobymatze.live.page.html.Attributes.name
import io.noobymatze.live.page.html.Attributes.type
import io.noobymatze.live.page.html.Attributes.value
import io.noobymatze.live.page.html.Events
import io.noobymatze.live.page.html.Events.onClick
import io.noobymatze.live.page.html.Events.onSubmit
import io.noobymatze.live.page.html.FormData
import io.noobymatze.live.page.html.Html
import io.noobymatze.live.page.html.Html.Companion.button
import io.noobymatze.live.page.html.Html.Companion.div
import io.noobymatze.live.page.html.Html.Companion.form
import io.noobymatze.live.page.html.Html.Companion.input
import io.noobymatze.live.page.html.Html.Companion.text
import org.apache.wicket.request.mapper.parameter.PageParameters
import java.io.Serializable
import kotlin.reflect.KProperty


typealias Errors = Map<KProperty<*>, List<String>>

class ExamplePage(params: PageParameters): LivePage<Model, Msg>(params) {


    // MODEL

    data class Person(
        val name: String,
        val age: Int
    ): Serializable

    data class Model(
        val count: Int,
        val person: Person,
        val errors: Errors = mapOf()
    ): Serializable


    override fun init(): Model = Model(
        count = 0,
        person = Person("Matthias", 28)
    )



    // VIEW


    override fun view(model: Model): Html<Msg> = div(
        div(text(model.count.toString())),
        div(listOf(
            classList("bla" to (model.count < 5))
        ),
            button(
                listOf(
                    onClick(Inc),
                    attribute("data-test", "Test")
                ),
                text("Increment"))
        ),
        div(button(
            listOf(onClick(Dec)),
            text("Decrement")
        )),
        div(text("Hello ${model.person.name}!")),
        viewForm(model.person, model.errors)
    )

    private fun viewForm(person: Person, errors: Errors): Html<Msg> =
        form(
            listOf(
                onSubmit(::SetData)
            ),
            input(type("text"), name(Person::name), value(person.name)),
            errors.view(Person::name),
            input(type("number"), name(Person::age), value(person.age.toString())),
            errors.view(Person::age),
            button(text("Save"))
        )

    private fun Errors.view(prop: KProperty<*>): Html<Msg> {
        val rendered = get(prop)
            ?.let { it.map { text<Msg>(it) } }
            ?: listOf()

        return div(
            listOf(Attributes.style("color", "red")),
            *rendered.toTypedArray()
        )

    }


    // UPDATE


    sealed class Msg : Serializable {
        object Inc : Msg()
        object Dec : Msg()
        data class SetData(val data: FormData): Msg()
    }

    override fun update(msg: Msg, model: Model): Model = when (msg) {
        is Inc ->
            model.copy(count = model.count + 1)

        is Dec ->
            model.copy(count = model.count - 1)

        is SetData -> {
            val name: String? = msg.data[Person::name]
            val age: Int? = msg.data[Person::age]

            val nameError = Person::name to if (name == null || name.isBlank()) {
                listOf("Please enter a name")
            } else {
                listOf()
            }

            val ageError = Person::age to if(age == null) {
                listOf("The age is required")
            } else {
                listOf()
            }

            val errors: Map<KProperty<*>, List<String>> = listOf(nameError, ageError).toMap()

            val newPerson = if (errors.isEmpty()) {
                Person(name!!, age!!)
            } else {
                model.person
            }

            model.copy(
                person = newPerson,
                errors = errors
            )
        }
    }

}
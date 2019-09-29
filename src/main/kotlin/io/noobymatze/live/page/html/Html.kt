package io.noobymatze.live.page.html

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Html.Node::class, name = "Node"),
    JsonSubTypes.Type(value = Html.Text::class, name = "Text")
)
sealed class Html<out Msg> {

    data class Node<out Msg>(
        val name: String,
        val attributes: List<Attribute<Msg>>,
        val children: List<Html<Msg>>
    ): Html<Msg>()

    data class Text<out Msg>(
        val content: String
    ): Html<Msg>()


    companion object {

        fun <Msg> node(name: String, attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<out Msg> =
            Node(name, attributes, children.toList())

        fun <Msg> node(name: String, vararg children: Html<Msg>): Html<out Msg> =
            Node(name, listOf(), children.toList())

        fun <Msg> text(content: String): Html<Msg> =
            Text(content)

        fun <Msg> div(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<out Msg> =
            node<Msg>("div", attributes, *children)

        fun <Msg> div(vararg children: Html<Msg>): Html<out Msg> =
            node<Msg>("div", *children)

        fun <Msg> button(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<out Msg> =
            node<Msg>("button", attributes, *children)

        fun <Msg> button(vararg children: Html<Msg>): Html<out Msg> =
            node<Msg>("button", *children)

    }

}


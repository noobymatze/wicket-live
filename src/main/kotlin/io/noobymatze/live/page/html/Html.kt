package io.noobymatze.live.page.html

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable


/**
 *
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Html.Node::class, name = "Node"),
    JsonSubTypes.Type(value = Html.Text::class, name = "Text")
)
sealed class Html<out Msg>: Serializable {

    /**
     *
     * @param name
     * @param attributes
     * @param children
     */
    internal data class Node<out Msg>(
        val name: String,
        val attributes: List<Attribute<Msg>>,
        val children: List<Html<Msg>>
    ): Html<Msg>()

    /**
     *
     * @param content
     */
    internal data class Text<out Msg>(
        val content: String
    ): Html<Msg>()


    companion object {

        fun <Msg> node(name: String, attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            Node(name, attributes, children.toList())

        fun <Msg> node(name: String, vararg children: Html<Msg>): Html<Msg> =
            Node(name, listOf(), children.toList())

        fun <Msg> text(content: String): Html<Msg> =
            Text(content)

        fun <Msg> div(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("div", attributes, *children)

        fun <Msg> div(vararg children: Html<Msg>): Html<Msg> =
            node("div", *children)

        fun <Msg> button(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("button", attributes, *children)

        fun <Msg> button(vararg children: Html<Msg>): Html<Msg> =
            node("button", *children)

    }

}


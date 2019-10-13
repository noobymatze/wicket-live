package io.noobymatze.live.html

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.noobymatze.live.html.Attribute.Type
import io.noobymatze.live.html.Attribute.Type.Event
import java.io.Serializable
import java.util.*


@JsonSerialize(using = Html.Serializer::class)
sealed class Html<out Msg>: Serializable {

    /**
     *
     * @param name
     * @param attributes
     * @param children
     */
    internal data class Node<out Msg>(
        val name: String,
        val attributes: MutableMap<Type, List<Attribute<@UnsafeVariance Msg>>>,
        val children: List<Html<Msg>>
    ) : Html<Msg>()

    /**
     *
     * @param content
     */
    internal data class Text<out Msg>(
        val content: String
    ) : Html<Msg>()


    internal class Serializer: StdSerializer<Html<Any?>>(Html::class.java) {

        override fun serialize(
            html: Html<Any?>,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            when (html) {
                is Node -> gen.apply {
                    writeStartArray()
                    writeString(html.name)
                    if (!html.attributes.isEmpty()) {
                        writeStartObject()
                        val serializer = provider.findValueSerializer(Attribute::class.java)
                        html.attributes.forEach { (type, list) ->
                            writeFieldName(type.identifier.toString())
                            writeStartObject()
                            list.forEach {
                                serializer.serialize(it, gen, provider)
                            }
                            writeEndObject()
                        }
                        writeEndObject()
                    }
                    html.children.forEach {
                        serialize(it, gen, provider)
                    }
                    writeEndArray()
                }

                is Text -> gen.apply {
                    writeString(html.content)
                }
            }
        }

    }

    fun render(): String = when (this) {
        is Node ->
            """<$name${attributes[Type.Attribute]?.joinToString("") { 
                " ${it.key}='${(it.value as Attribute.Value.Str).value}'" 
            } ?: ""}>${children.joinToString("") { it.render() }}</$name>"""

        is Text ->
            content
    }


    fun replaceHandlers(): Map<Int, Attribute.Value.Listener<Msg>> {
        val html = Stack<Html<Msg>>().apply { add(this@Html) }
        val map = mutableMapOf<Int, Attribute.Value.Listener<Msg>>()
        var identifier = 0
        while (html.isNotEmpty()) {
            val next = html.pop()
            if (next is Node) {
                next.attributes[Event]?.let {
                    next.attributes[Event] = it.map { attribute ->
                        val handler = attribute.value as Attribute.Value.Listener<Msg>
                        val ref = Attribute.Value.ListenerRef<Msg>(identifier++)
                        map[ref.id] = handler
                        attribute.copy(value = Attribute.Value.ListenerRef(ref.id))
                    }
                }
                next.children.forEach { html.push(it) }
            }
        }

        return map
    }


    companion object {

        private fun <Msg> organizeAttributes(
            attributes: List<Attribute<Msg>>
        ): MutableMap<Type, List<Attribute<Msg>>> {
            val map = mutableMapOf<Type, MutableList<Attribute<Msg>>>()
            attributes.forEach {
                if (!map.containsKey(it.type)) {
                    map[it.type] = mutableListOf()
                }

                map[it.type]!!.add(it)
            }

            // This is casting up, so no problems here
            @Suppress("UNCHECKED_CAST")
            return map as MutableMap<Type, List<Attribute<Msg>>>
        }

        fun <Msg> node(name: String, attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            Node(name, organizeAttributes(attributes), children.toList())

        fun <Msg> node(name: String, vararg children: Html<Msg>): Html<Msg> =
            Node(name, mutableMapOf(), children.toList())

        fun <Msg> text(content: String): Html<Msg> =
            Text(content)

        fun <Msg> h1(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("h1", attributes, *children)

        fun <Msg> h1(vararg children: Html<Msg>): Html<Msg> =
            node("h1", listOf(), *children)

        fun <Msg> h2(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("h2", attributes, *children)

        fun <Msg> h2(vararg children: Html<Msg>): Html<Msg> =
            node("h2", listOf(), *children)

        fun <Msg> h3(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("h3", attributes, *children)

        fun <Msg> h3(vararg children: Html<Msg>): Html<Msg> =
            node("h3", listOf(), *children)

        fun <Msg> h4(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("h4", attributes, *children)

        fun <Msg> h4(vararg children: Html<Msg>): Html<Msg> =
            node("h4", listOf(), *children)

        fun <Msg> h5(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("h5", attributes, *children)

        fun <Msg> h5(vararg children: Html<Msg>): Html<Msg> =
            node("h5", listOf(), *children)

        fun <Msg> h6(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("h6", attributes, *children)

        fun <Msg> h6(vararg children: Html<Msg>): Html<Msg> =
            node("h6", listOf(), *children)

        fun <Msg> div(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("div", attributes, *children)

        fun <Msg> div(vararg children: Html<Msg>): Html<Msg> =
            node("div", listOf(), *children)

        fun <Msg> p(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("p", attributes, *children)

        fun <Msg> p(vararg children: Html<Msg>): Html<Msg> =
            node("p", listOf(), *children)

        fun <Msg> hr(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("hr", attributes, *children)

        fun <Msg> hr(vararg children: Html<Msg>): Html<Msg> =
            node("hr", listOf(), *children)

        fun <Msg> pre(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("pre", attributes, *children)

        fun <Msg> pre(vararg children: Html<Msg>): Html<Msg> =
            node("pre", listOf(), *children)

        fun <Msg> blockquote(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("blockquote", attributes, *children)

        fun <Msg> blockquote(vararg children: Html<Msg>): Html<Msg> =
            node("blockquote", listOf(), *children)

        fun <Msg> span(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("span", attributes, *children)

        fun <Msg> span(vararg children: Html<Msg>): Html<Msg> =
            node("span", listOf(), *children)

        fun <Msg> a(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("a", attributes, *children)

        fun <Msg> a(vararg children: Html<Msg>): Html<Msg> =
            node("a", listOf(), *children)

        fun <Msg> code(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("code", attributes, *children)

        fun <Msg> code(vararg children: Html<Msg>): Html<Msg> =
            node("code", listOf(), *children)

        fun <Msg> em(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("em", attributes, *children)

        fun <Msg> em(vararg children: Html<Msg>): Html<Msg> =
            node("em", listOf(), *children)

        fun <Msg> strong(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("strong", attributes, *children)

        fun <Msg> strong(vararg children: Html<Msg>): Html<Msg> =
            node("strong", listOf(), *children)

        fun <Msg> i(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("i", attributes, *children)

        fun <Msg> i(vararg children: Html<Msg>): Html<Msg> =
            node("i", listOf(), *children)

        fun <Msg> b(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("b", attributes, *children)

        fun <Msg> b(vararg children: Html<Msg>): Html<Msg> =
            node("b", listOf(), *children)

        fun <Msg> u(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("u", attributes, *children)

        fun <Msg> u(vararg children: Html<Msg>): Html<Msg> =
            node("u", listOf(), *children)

        fun <Msg> sub(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("sub", attributes, *children)

        fun <Msg> sub(vararg children: Html<Msg>): Html<Msg> =
            node("sub", listOf(), *children)

        fun <Msg> sup(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("sup", attributes, *children)

        fun <Msg> sup(vararg children: Html<Msg>): Html<Msg> =
            node("sup", listOf(), *children)

        fun <Msg> br(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("br", attributes, *children)

        fun <Msg> br(vararg children: Html<Msg>): Html<Msg> =
            node("br", listOf(), *children)

        fun <Msg> ol(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("ol", attributes, *children)

        fun <Msg> ol(vararg children: Html<Msg>): Html<Msg> =
            node("ol", listOf(), *children)

        fun <Msg> ul(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("ul", attributes, *children)

        fun <Msg> ul(vararg children: Html<Msg>): Html<Msg> =
            node("ul", listOf(), *children)

        fun <Msg> li(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("li", attributes, *children)

        fun <Msg> li(vararg children: Html<Msg>): Html<Msg> =
            node("li", listOf(), *children)

        fun <Msg> dl(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("dl", attributes, *children)

        fun <Msg> dl(vararg children: Html<Msg>): Html<Msg> =
            node("dl", listOf(), *children)

        fun <Msg> dt(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("dt", attributes, *children)

        fun <Msg> dt(vararg children: Html<Msg>): Html<Msg> =
            node("dt", listOf(), *children)

        fun <Msg> dd(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("dd", attributes, *children)

        fun <Msg> dd(vararg children: Html<Msg>): Html<Msg> =
            node("dd", listOf(), *children)

        fun <Msg> img(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("img", attributes, *children)

        fun <Msg> img(vararg children: Html<Msg>): Html<Msg> =
            node("img", listOf(), *children)

        fun <Msg> iframe(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("iframe", attributes, *children)

        fun <Msg> iframe(vararg children: Html<Msg>): Html<Msg> =
            node("iframe", listOf(), *children)

        fun <Msg> canvas(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("canvas", attributes, *children)

        fun <Msg> canvas(vararg children: Html<Msg>): Html<Msg> =
            node("canvas", listOf(), *children)

        fun <Msg> math(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("math", attributes, *children)

        fun <Msg> math(vararg children: Html<Msg>): Html<Msg> =
            node("math", listOf(), *children)

        fun <Msg> form(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("form", attributes, *children)

        fun <Msg> form(vararg children: Html<Msg>): Html<Msg> =
            node("form", listOf(), *children)

        fun <Msg> input(vararg attributes: Attribute<Msg>): Html<Msg> =
            node("input", attributes.toList())

        fun <Msg> textarea(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("textarea", attributes, *children)

        fun <Msg> textarea(vararg children: Html<Msg>): Html<Msg> =
            node("textarea", listOf(), *children)

        fun <Msg> button(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("button", attributes, *children)

        fun <Msg> button(vararg children: Html<Msg>): Html<Msg> =
            node("button", listOf(), *children)

        fun <Msg> select(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("select", attributes, *children)

        fun <Msg> select(vararg children: Html<Msg>): Html<Msg> =
            node("select", listOf(), *children)

        fun <Msg> option(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("option", attributes, *children)

        fun <Msg> option(vararg children: Html<Msg>): Html<Msg> =
            node("option", listOf(), *children)

        fun <Msg> section(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("section", attributes, *children)

        fun <Msg> section(vararg children: Html<Msg>): Html<Msg> =
            node("section", listOf(), *children)

        fun <Msg> nav(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("nav", attributes, *children)

        fun <Msg> nav(vararg children: Html<Msg>): Html<Msg> =
            node("nav", listOf(), *children)

        fun <Msg> article(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("article", attributes, *children)

        fun <Msg> article(vararg children: Html<Msg>): Html<Msg> =
            node("article", listOf(), *children)

        fun <Msg> aside(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("aside", attributes, *children)

        fun <Msg> aside(vararg children: Html<Msg>): Html<Msg> =
            node("aside", listOf(), *children)

        fun <Msg> header(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("header", attributes, *children)

        fun <Msg> header(vararg children: Html<Msg>): Html<Msg> =
            node("header", listOf(), *children)

        fun <Msg> footer(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("footer", attributes, *children)

        fun <Msg> footer(vararg children: Html<Msg>): Html<Msg> =
            node("footer", listOf(), *children)

        fun <Msg> address(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("address", attributes, *children)

        fun <Msg> address(vararg children: Html<Msg>): Html<Msg> =
            node("address", listOf(), *children)

        fun <Msg> main_(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("main_", attributes, *children)

        fun <Msg> main_(vararg children: Html<Msg>): Html<Msg> =
            node("main_", listOf(), *children)

        fun <Msg> figure(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("figure", attributes, *children)

        fun <Msg> figure(vararg children: Html<Msg>): Html<Msg> =
            node("figure", listOf(), *children)

        fun <Msg> figcaption(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("figcaption", attributes, *children)

        fun <Msg> figcaption(vararg children: Html<Msg>): Html<Msg> =
            node("figcaption", listOf(), *children)

        fun <Msg> table(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("table", attributes, *children)

        fun <Msg> table(vararg children: Html<Msg>): Html<Msg> =
            node("table", listOf(), *children)

        fun <Msg> caption(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("caption", attributes, *children)

        fun <Msg> caption(vararg children: Html<Msg>): Html<Msg> =
            node("caption", listOf(), *children)

        fun <Msg> colgroup(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("colgroup", attributes, *children)

        fun <Msg> colgroup(vararg children: Html<Msg>): Html<Msg> =
            node("colgroup", listOf(), *children)

        fun <Msg> col(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("col", attributes, *children)

        fun <Msg> col(vararg children: Html<Msg>): Html<Msg> =
            node("col", listOf(), *children)

        fun <Msg> tbody(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("tbody", attributes, *children)

        fun <Msg> tbody(vararg children: Html<Msg>): Html<Msg> =
            node("tbody", listOf(), *children)

        fun <Msg> thead(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("thead", attributes, *children)

        fun <Msg> thead(vararg children: Html<Msg>): Html<Msg> =
            node("thead", listOf(), *children)

        fun <Msg> tfoot(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("tfoot", attributes, *children)

        fun <Msg> tfoot(vararg children: Html<Msg>): Html<Msg> =
            node("tfoot", listOf(), *children)

        fun <Msg> tr(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("tr", attributes, *children)

        fun <Msg> tr(vararg children: Html<Msg>): Html<Msg> =
            node("tr", listOf(), *children)

        fun <Msg> td(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("td", attributes, *children)

        fun <Msg> td(vararg children: Html<Msg>): Html<Msg> =
            node("td", listOf(), *children)

        fun <Msg> th(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("th", attributes, *children)

        fun <Msg> th(vararg children: Html<Msg>): Html<Msg> =
            node("th", listOf(), *children)

        fun <Msg> fieldset(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("fieldset", attributes, *children)

        fun <Msg> fieldset(vararg children: Html<Msg>): Html<Msg> =
            node("fieldset", listOf(), *children)

        fun <Msg> legend(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("legend", attributes, *children)

        fun <Msg> legend(vararg children: Html<Msg>): Html<Msg> =
            node("legend", listOf(), *children)

        fun <Msg> label(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("label", attributes, *children)

        fun <Msg> label(vararg children: Html<Msg>): Html<Msg> =
            node("label", listOf(), *children)

        fun <Msg> datalist(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("datalist", attributes, *children)

        fun <Msg> datalist(vararg children: Html<Msg>): Html<Msg> =
            node("datalist", listOf(), *children)

        fun <Msg> optgroup(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("optgroup", attributes, *children)

        fun <Msg> optgroup(vararg children: Html<Msg>): Html<Msg> =
            node("optgroup", listOf(), *children)

        fun <Msg> output(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("output", attributes, *children)

        fun <Msg> output(vararg children: Html<Msg>): Html<Msg> =
            node("output", listOf(), *children)

        fun <Msg> progress(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("progress", attributes, *children)

        fun <Msg> progress(vararg children: Html<Msg>): Html<Msg> =
            node("progress", listOf(), *children)

        fun <Msg> meter(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("meter", attributes, *children)

        fun <Msg> meter(vararg children: Html<Msg>): Html<Msg> =
            node("meter", listOf(), *children)

        fun <Msg> audio(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("audio", attributes, *children)

        fun <Msg> audio(vararg children: Html<Msg>): Html<Msg> =
            node("audio", listOf(), *children)

        fun <Msg> video(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("video", attributes, *children)

        fun <Msg> video(vararg children: Html<Msg>): Html<Msg> =
            node("video", listOf(), *children)

        fun <Msg> source(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("source", attributes, *children)

        fun <Msg> source(vararg children: Html<Msg>): Html<Msg> =
            node("source", listOf(), *children)

        fun <Msg> track(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("track", attributes, *children)

        fun <Msg> track(vararg children: Html<Msg>): Html<Msg> =
            node("track", listOf(), *children)

        fun <Msg> embed(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("embed", attributes, *children)

        fun <Msg> embed(vararg children: Html<Msg>): Html<Msg> =
            node("embed", listOf(), *children)

        fun <Msg> `object`(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("object", attributes, *children)

        fun <Msg> `object`(vararg children: Html<Msg>): Html<Msg> =
            node("object", listOf(), *children)

        fun <Msg> param(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("param", attributes, *children)

        fun <Msg> param(vararg children: Html<Msg>): Html<Msg> =
            node("param", listOf(), *children)

        fun <Msg> ins(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("ins", attributes, *children)

        fun <Msg> ins(vararg children: Html<Msg>): Html<Msg> =
            node("ins", listOf(), *children)

        fun <Msg> del(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("del", attributes, *children)

        fun <Msg> del(vararg children: Html<Msg>): Html<Msg> =
            node("del", listOf(), *children)

        fun <Msg> small(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("small", attributes, *children)

        fun <Msg> small(vararg children: Html<Msg>): Html<Msg> =
            node("small", listOf(), *children)

        fun <Msg> cite(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("cite", attributes, *children)

        fun <Msg> cite(vararg children: Html<Msg>): Html<Msg> =
            node("cite", listOf(), *children)

        fun <Msg> dfn(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("dfn", attributes, *children)

        fun <Msg> dfn(vararg children: Html<Msg>): Html<Msg> =
            node("dfn", listOf(), *children)

        fun <Msg> abbr(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("abbr", attributes, *children)

        fun <Msg> abbr(vararg children: Html<Msg>): Html<Msg> =
            node("abbr", listOf(), *children)

        fun <Msg> time(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("time", attributes, *children)

        fun <Msg> time(vararg children: Html<Msg>): Html<Msg> =
            node("time", listOf(), *children)

        fun <Msg> `var`(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("var", attributes, *children)

        fun <Msg> `var`(vararg children: Html<Msg>): Html<Msg> =
            node("var", listOf(), *children)

        fun <Msg> samp(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("samp", attributes, *children)

        fun <Msg> samp(vararg children: Html<Msg>): Html<Msg> =
            node("samp", listOf(), *children)

        fun <Msg> kbd(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("kbd", attributes, *children)

        fun <Msg> kbd(vararg children: Html<Msg>): Html<Msg> =
            node("kbd", listOf(), *children)

        fun <Msg> s(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("s", attributes, *children)

        fun <Msg> s(vararg children: Html<Msg>): Html<Msg> =
            node("s", listOf(), *children)

        fun <Msg> q(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("q", attributes, *children)

        fun <Msg> q(vararg children: Html<Msg>): Html<Msg> =
            node("q", listOf(), *children)

        fun <Msg> mark(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("mark", attributes, *children)

        fun <Msg> mark(vararg children: Html<Msg>): Html<Msg> =
            node("mark", listOf(), *children)

        fun <Msg> ruby(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("ruby", attributes, *children)

        fun <Msg> ruby(vararg children: Html<Msg>): Html<Msg> =
            node("ruby", listOf(), *children)

        fun <Msg> rt(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("rt", attributes, *children)

        fun <Msg> rt(vararg children: Html<Msg>): Html<Msg> =
            node("rt", listOf(), *children)

        fun <Msg> rp(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("rp", attributes, *children)

        fun <Msg> rp(vararg children: Html<Msg>): Html<Msg> =
            node("rp", listOf(), *children)

        fun <Msg> bdi(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("bdi", attributes, *children)

        fun <Msg> bdi(vararg children: Html<Msg>): Html<Msg> =
            node("bdi", listOf(), *children)

        fun <Msg> bdo(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("bdo", attributes, *children)

        fun <Msg> bdo(vararg children: Html<Msg>): Html<Msg> =
            node("bdo", listOf(), *children)

        fun <Msg> wbr(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("wbr", attributes, *children)

        fun <Msg> wbr(vararg children: Html<Msg>): Html<Msg> =
            node("wbr", listOf(), *children)

        fun <Msg> details(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("details", attributes, *children)

        fun <Msg> details(vararg children: Html<Msg>): Html<Msg> =
            node("details", listOf(), *children)

        fun <Msg> summary(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("summary", attributes, *children)

        fun <Msg> summary(vararg children: Html<Msg>): Html<Msg> =
            node("summary", listOf(), *children)

        fun <Msg> menuitem(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("menuitem", attributes, *children)

        fun <Msg> menuitem(vararg children: Html<Msg>): Html<Msg> =
            node("menuitem", listOf(), *children)

        fun <Msg> menu(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
            node("menu", attributes, *children)

        fun <Msg> menu(vararg children: Html<Msg>): Html<Msg> =
            node("menu", listOf(), *children)

    }

}

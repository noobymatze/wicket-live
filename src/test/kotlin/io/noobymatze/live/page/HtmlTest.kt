package io.noobymatze.live.page

import org.junit.Test


/**
 * Use this to create all the html functions.
 */
class HtmlTest {

    // @Test
    fun test() {
        val values = ("h1, h2, h3, h4, h5, h6" +
            ", div, p, hr, pre, blockquote" +
            ", span, a, code, em, strong, i, b, u, sub, sup, br" +
            ", ol, ul, li, dl, dt, dd" +
            ", img, iframe, canvas, math" +
            ", form, input, textarea, button, select, option" +
            ", section, nav, article, aside, header, footer, address, main_" +
            ", figure, figcaption" +
            ", table, caption, colgroup, col, tbody, thead, tfoot, tr, td, th" +
            ", fieldset, legend, label, datalist, optgroup, output, progress, meter" +
            ", audio, video, source, track" +
            ", embed, object, param" +
            ", ins, del" +
            ", small, cite, dfn, abbr, time, var, samp, kbd, s, q" +
            ", mark, ruby, rt, rp, bdi, bdo, wbr" +
            ", details, summary, menuitem, menu").filter { it != ' ' }.split(",").filter { it.isNotBlank() }

        values.map {
                """
                    |fun <Msg> $it(attributes: List<Attribute<Msg>>, vararg children: Html<Msg>): Html<Msg> =
                    |    node("$it", attributes, *children)
                    |    
                    |fun <Msg> $it(vararg children: Html<Msg>): Html<Msg> =
                    |    node("$it", listOf(), *children)
                    |
                """.trimMargin()
            }.forEach(::println)
    }

    // @Test
    fun createAttributes() {
        val attributes = """
            style, property, attribute, map
          , class, classList, id, title, hidden
          , type_, value, checked, placeholder, selected
          , accept, acceptCharset, action, autocomplete, autofocus
          , disabled, enctype, list, maxlength, minlength, method, multiple
          , name, novalidate, pattern, readonly, required, size, for, form
          , max, min, step
          , cols, rows, wrap
          , href, target, download, hreflang, media, ping, rel
          , ismap, usemap, shape, coords
          , src, height, width, alt
          , autoplay, controls, loop, preload, poster, default, kind, srclang
          , sandbox, srcdoc
          , reversed, start
          , align, colspan, rowspan, headers, scope
          , accesskey, contenteditable, contextmenu, dir, draggable, dropzone
          , itemprop, lang, spellcheck, tabindex
          , cite, datetime, pubdate, manifest
        """.trimIndent().filter { it != ' ' && it != '\n' }.split(",")

        attributes.map {
            """
                |fun <Msg> $it(value: String): Attribute<Msg> =
                |    attribute("$it", value)
                |    
            """.trimMargin()
        }.forEach(::println)

    }
}
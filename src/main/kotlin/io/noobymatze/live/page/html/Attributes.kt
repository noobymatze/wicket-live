package io.noobymatze.live.page.html

import kotlin.reflect.KProperty


object Attributes {

    fun <Msg> attribute(key: String, value: String): Attribute<Msg> =
        Attribute(Attribute.Type.Attribute, key, Attribute.Value.Str(value))

    fun <Msg> style(name: String, value: String): Attribute<Msg> =
        attribute("style", "$name: $value")

    fun <Msg> property(value: String): Attribute<Msg> =
        attribute("property", value)

    fun <Msg> attribute(value: String): Attribute<Msg> =
        attribute("attribute", value)

    fun <Msg> map(value: String): Attribute<Msg> =
        attribute("map", value)

    fun <Msg> `class`(value: String): Attribute<Msg> =
        attribute("class", value)

    fun <Msg> classList(vararg pairs: Pair<String, Boolean>): Attribute<Msg> =
        attribute("class", pairs
            .filter { it.second }
            .joinToString(" ") { it.first })

    fun <Msg> id(value: String): Attribute<Msg> =
        attribute("id", value)

    fun <Msg> title(value: String): Attribute<Msg> =
        attribute("title", value)

    fun <Msg> hidden(value: String): Attribute<Msg> =
        attribute("hidden", value)

    fun <Msg> type(value: String): Attribute<Msg> =
        attribute("type", value)

    fun <Msg> value(value: String): Attribute<Msg> =
        attribute("value", value)

    fun <Msg> checked(value: String): Attribute<Msg> =
        attribute("checked", value)

    fun <Msg> placeholder(value: String): Attribute<Msg> =
        attribute("placeholder", value)

    fun <Msg> selected(value: String): Attribute<Msg> =
        attribute("selected", value)

    fun <Msg> accept(value: String): Attribute<Msg> =
        attribute("accept", value)

    fun <Msg> acceptCharset(value: String): Attribute<Msg> =
        attribute("acceptCharset", value)

    fun <Msg> action(value: String): Attribute<Msg> =
        attribute("action", value)

    fun <Msg> autocomplete(value: String): Attribute<Msg> =
        attribute("autocomplete", value)

    fun <Msg> autofocus(value: String): Attribute<Msg> =
        attribute("autofocus", value)

    fun <Msg> disabled(value: String): Attribute<Msg> =
        attribute("disabled", value)

    fun <Msg> enctype(value: String): Attribute<Msg> =
        attribute("enctype", value)

    fun <Msg> list(value: String): Attribute<Msg> =
        attribute("list", value)

    fun <Msg> maxlength(value: String): Attribute<Msg> =
        attribute("maxlength", value)

    fun <Msg> minlength(value: String): Attribute<Msg> =
        attribute("minlength", value)

    fun <Msg> method(value: String): Attribute<Msg> =
        attribute("method", value)

    fun <Msg> multiple(value: String): Attribute<Msg> =
        attribute("multiple", value)

    fun <Msg> name(value: String): Attribute<Msg> =
        attribute("name", value)

    fun <Msg> name(value: KProperty<*>): Attribute<Msg> =
        attribute("name", value.name)

    fun <Msg> novalidate(value: String): Attribute<Msg> =
        attribute("novalidate", value)

    fun <Msg> pattern(value: String): Attribute<Msg> =
        attribute("pattern", value)

    fun <Msg> readonly(value: String): Attribute<Msg> =
        attribute("readonly", value)

    fun <Msg> required(value: String): Attribute<Msg> =
        attribute("required", value)

    fun <Msg> size(value: String): Attribute<Msg> =
        attribute("size", value)

    fun <Msg> `for`(value: String): Attribute<Msg> =
        attribute("for", value)

    fun <Msg> form(value: String): Attribute<Msg> =
        attribute("form", value)

    fun <Msg> max(value: String): Attribute<Msg> =
        attribute("max", value)

    fun <Msg> min(value: String): Attribute<Msg> =
        attribute("min", value)

    fun <Msg> step(value: String): Attribute<Msg> =
        attribute("step", value)

    fun <Msg> cols(value: String): Attribute<Msg> =
        attribute("cols", value)

    fun <Msg> rows(value: String): Attribute<Msg> =
        attribute("rows", value)

    fun <Msg> wrap(value: String): Attribute<Msg> =
        attribute("wrap", value)

    fun <Msg> href(value: String): Attribute<Msg> =
        attribute("href", value)

    fun <Msg> target(value: String): Attribute<Msg> =
        attribute("target", value)

    fun <Msg> download(value: String): Attribute<Msg> =
        attribute("download", value)

    fun <Msg> hreflang(value: String): Attribute<Msg> =
        attribute("hreflang", value)

    fun <Msg> media(value: String): Attribute<Msg> =
        attribute("media", value)

    fun <Msg> ping(value: String): Attribute<Msg> =
        attribute("ping", value)

    fun <Msg> rel(value: String): Attribute<Msg> =
        attribute("rel", value)

    fun <Msg> ismap(value: String): Attribute<Msg> =
        attribute("ismap", value)

    fun <Msg> usemap(value: String): Attribute<Msg> =
        attribute("usemap", value)

    fun <Msg> shape(value: String): Attribute<Msg> =
        attribute("shape", value)

    fun <Msg> coords(value: String): Attribute<Msg> =
        attribute("coords", value)

    fun <Msg> src(value: String): Attribute<Msg> =
        attribute("src", value)

    fun <Msg> height(value: String): Attribute<Msg> =
        attribute("height", value)

    fun <Msg> width(value: String): Attribute<Msg> =
        attribute("width", value)

    fun <Msg> alt(value: String): Attribute<Msg> =
        attribute("alt", value)

    fun <Msg> autoplay(value: String): Attribute<Msg> =
        attribute("autoplay", value)

    fun <Msg> controls(value: String): Attribute<Msg> =
        attribute("controls", value)

    fun <Msg> loop(value: String): Attribute<Msg> =
        attribute("loop", value)

    fun <Msg> preload(value: String): Attribute<Msg> =
        attribute("preload", value)

    fun <Msg> poster(value: String): Attribute<Msg> =
        attribute("poster", value)

    fun <Msg> default(value: String): Attribute<Msg> =
        attribute("default", value)

    fun <Msg> kind(value: String): Attribute<Msg> =
        attribute("kind", value)

    fun <Msg> srclang(value: String): Attribute<Msg> =
        attribute("srclang", value)

    fun <Msg> sandbox(value: String): Attribute<Msg> =
        attribute("sandbox", value)

    fun <Msg> srcdoc(value: String): Attribute<Msg> =
        attribute("srcdoc", value)

    fun <Msg> reversed(value: String): Attribute<Msg> =
        attribute("reversed", value)

    fun <Msg> start(value: String): Attribute<Msg> =
        attribute("start", value)

    fun <Msg> align(value: String): Attribute<Msg> =
        attribute("align", value)

    fun <Msg> colspan(value: String): Attribute<Msg> =
        attribute("colspan", value)

    fun <Msg> rowspan(value: String): Attribute<Msg> =
        attribute("rowspan", value)

    fun <Msg> headers(value: String): Attribute<Msg> =
        attribute("headers", value)

    fun <Msg> scope(value: String): Attribute<Msg> =
        attribute("scope", value)

    fun <Msg> accesskey(value: String): Attribute<Msg> =
        attribute("accesskey", value)

    fun <Msg> contenteditable(value: String): Attribute<Msg> =
        attribute("contenteditable", value)

    fun <Msg> contextmenu(value: String): Attribute<Msg> =
        attribute("contextmenu", value)

    fun <Msg> dir(value: String): Attribute<Msg> =
        attribute("dir", value)

    fun <Msg> draggable(value: String): Attribute<Msg> =
        attribute("draggable", value)

    fun <Msg> dropzone(value: String): Attribute<Msg> =
        attribute("dropzone", value)

    fun <Msg> itemprop(value: String): Attribute<Msg> =
        attribute("itemprop", value)

    fun <Msg> lang(value: String): Attribute<Msg> =
        attribute("lang", value)

    fun <Msg> spellcheck(value: String): Attribute<Msg> =
        attribute("spellcheck", value)

    fun <Msg> tabindex(value: String): Attribute<Msg> =
        attribute("tabindex", value)

    fun <Msg> cite(value: String): Attribute<Msg> =
        attribute("cite", value)

    fun <Msg> datetime(value: String): Attribute<Msg> =
        attribute("datetime", value)

    fun <Msg> pubdate(value: String): Attribute<Msg> =
        attribute("pubdate", value)

    fun <Msg> manifest(value: String): Attribute<Msg> =
        attribute("manifest", value)

}

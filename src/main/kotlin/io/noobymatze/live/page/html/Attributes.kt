package io.noobymatze.live.page.html


object Attributes {

    fun <Msg> attribute(key: String, value: String): Attribute<Msg> =
        Attribute.Attr(key, value)

}

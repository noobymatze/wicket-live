package io.noobymatze.live.page.html

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Attribute.Attr::class, name = "Attr"),
    JsonSubTypes.Type(value = Attribute.EventListener.OnClick::class, name = "OnClick")
)
sealed class Attribute<out Msg> {

    data class Attr<out Msg>(val key: String, val value: String): Attribute<Msg>()

    sealed class EventListener<out Msg>: Attribute<Msg>() {
        data class OnClick<out Msg>(val msg: Msg): EventListener<Msg>()
    }


    companion object {

        fun <Msg> attribute(key: String, value: String): Attribute<Msg> =
            Attr(key, value)
    }
}


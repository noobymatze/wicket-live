package io.noobymatze.live.page.html

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.noobymatze.live.page.html.BrowserEvent.Payload
import java.io.Serializable
import java.lang.IllegalStateException


/**
 *
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Attribute.Attr::class, name = "Attr"),
    JsonSubTypes.Type(value = Attribute.Event::class, name = "Event")
)
sealed class Attribute<out Msg>(val type: Type): Serializable {

    /**
     *
     */
    enum class Type {
        Attr,
        Event
    }

    /**
     *
     * @param key
     * @param value
     */
    internal data class Attr<out Msg>(
        val key: String,
        val value: String
    ): Attribute<Msg>(Type.Attr)

    /**
     *
     * @param type
     */
    internal data class Event<out Msg>(
        val event: String,
        var handler: Handler<@UnsafeVariance Msg>
    ): Attribute<Msg>(Type.Event)

    /**
     *
     */
    @JsonSerialize(using = Handler.Serializer::class)
    sealed class Handler<out Msg>: Serializable {

        /**
         *
         */
        data class Fn<out Msg>(
            private val f: (Payload?) -> Msg
        ): Handler<Msg>(), (Payload?) -> Msg {

            override fun invoke(event: Payload?): Msg =
                f(event)

        }

        /**
         *
         */
        data class Custom<out Msg>(val identifier: Int): Handler<Msg>()

        /**
         *
         */
        class Serializer: StdSerializer<Handler<Any?>>(Handler::class.java) {

            override fun serialize(
                handler: Handler<Any?>?,
                gen: JsonGenerator,
                provider: SerializerProvider
            ) {

                when (handler) {
                    is Fn ->
                        gen.writeNull()

                    is Custom ->
                        gen.writeNumber(handler.identifier)
                }

            }

        }
    }
}


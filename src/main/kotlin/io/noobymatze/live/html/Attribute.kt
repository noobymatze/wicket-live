package io.noobymatze.live.html

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.noobymatze.live.html.BrowserEvent.Payload
import java.io.Serializable


@JsonSerialize(using = Attribute.Serializer::class)
data class Attribute<out Msg>(
    val type: Type,
    val key: String,
    val value: Value<Msg>
): Serializable {

    enum class Type(val identifier: Int) {
        Event(0),
        Attribute(1),
        Property(2)
    }

    sealed class Value<out Msg>: Serializable {

        data class Str<out Msg>(
            val value: String
        ): Value<Msg>()

        data class Listener<out Msg>(
            val handle: (Payload?) -> Msg
        ): Value<Msg>(), (Payload?) -> Msg {

            override fun invoke(payload: Payload?): Msg =
                handle(payload)

        }

        data class ListenerRef<out Msg>(
            val id: Int
        ): Value<Msg>()

    }

    /**
     *
     */
    fun <NewMsg> map(f: (Msg) -> NewMsg): Attribute<NewMsg> = when (value) {
        is Value.Str ->
            this as Attribute<NewMsg>

        is Value.Listener ->
            Attribute(type, key, Value.Listener { f(value.handle(it)) })

        is Value.ListenerRef ->
            this as Attribute<NewMsg>
    }


    class Serializer: StdSerializer<Attribute<Any?>>(Attribute::class.java) {

        override fun serialize(
            attribute: Attribute<Any?>,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            gen.writeFieldName(attribute.key)
            when (val value = attribute.value) {
                is Value.Str ->
                    gen.writeString(value.value)

                is Value.Listener ->
                    gen.writeNull()

                is Value.ListenerRef ->
                    gen.writeNumber(value.id)
            }
        }

    }

}

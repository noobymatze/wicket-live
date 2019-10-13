package io.noobymatze.live.html

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable


data class BrowserEvent(
    val handlerId: Int,
    val payload: Payload?
): Serializable {

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = Payload.Form::class, name = "form")
    )
    sealed class Payload: Serializable {
        data class Form(val data: Map<String, String?>): Payload()
    }
}

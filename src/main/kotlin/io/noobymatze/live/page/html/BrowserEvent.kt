package io.noobymatze.live.page.html

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
        JsonSubTypes.Type(value = Payload.FormData::class, name = "form")
    )
    sealed class Payload(): Serializable {
        data class FormData(val data: Map<String, Any?>): Payload()
    }
}

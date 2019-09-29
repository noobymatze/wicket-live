package io.noobymatze.live.page.html

import java.io.Serializable


data class BrowserEvent(
    val handlerId: Int,
    val payload: Payload?
): Serializable {

    class Payload(): Serializable
}

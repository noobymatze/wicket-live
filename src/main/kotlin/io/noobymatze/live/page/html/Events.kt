package io.noobymatze.live.page.html

import io.noobymatze.live.page.html.Attribute.Event.Handler
import io.noobymatze.live.page.html.BrowserEvent.Payload


object Events {

    fun <Msg> on(event: String, f: (Payload?) -> Msg): Attribute<Msg> =
        Attribute.Event(event, Handler.Fn(f))

    fun <Msg> onClick(msg: Msg): Attribute<Msg> =
        on("click") { msg }


}


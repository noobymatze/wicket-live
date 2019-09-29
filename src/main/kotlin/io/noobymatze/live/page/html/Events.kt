package io.noobymatze.live.page.html


object Events {

    fun <Msg> onClick(msg: Msg): Attribute<Msg> =
        Attribute.EventListener.OnClick(msg)

    // fun <Msg> onInput(msg: (String) -> Msg): Attribute<Msg> =
    //    Attribute.EventListener("input") { msg("") }

}


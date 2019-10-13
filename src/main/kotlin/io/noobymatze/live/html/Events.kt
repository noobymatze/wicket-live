package io.noobymatze.live.html

import io.noobymatze.live.html.BrowserEvent.Payload


object Events {

    /**
     *
     */
    fun <Msg> on(event: String, f: (Payload?) -> Msg): Attribute<Msg> =
        Attribute(Attribute.Type.Event, event, Attribute.Value.Listener(f))

    /**
     *
     */
    fun <Msg> onClick(msg: Msg): Attribute<Msg> =
        on("click") { msg }

    /**
     *
     */
    fun <Msg> onDoubleClick(msg: Msg): Attribute<Msg> =
        on("dblclick") { msg }

    /**
     *
     */
    fun <Msg> onMouseDown(msg: Msg): Attribute<Msg> =
        on("mousedown") { msg }

    /**
     *
     */
    fun <Msg> onMouseUp(msg: Msg): Attribute<Msg> =
        on("mouseup") { msg }

    /**
     *
     */
    fun <Msg> onMouseEnter(msg: Msg): Attribute<Msg> =
        on("mouseenter") { msg }

    /**
     *
     */
    fun <Msg> onMouseLeave(msg: Msg): Attribute<Msg> =
        on("mouseleave") { msg }

    /**
     *
     */
    fun <Msg> onMouseOver(msg: Msg): Attribute<Msg> =
        on("mouseover") { msg }

    /**
     *
     */
    fun <Msg> onMouseOut(msg: Msg): Attribute<Msg> =
        on("mouseout") { msg }

    /**
     *
     */
    fun <Msg> onInput(f: (String) -> Msg): Attribute<Msg> =
        on("input") { f("") } // TODO: change this

    /**
     *
     */
    fun <Msg> onCheck(f: (Boolean) -> Msg): Attribute<Msg> =
        on("change") { f(true) } // TODO: Fix this

    /**
     *
     */
    fun <Msg> onSubmit(f: (FormData) -> Msg): Attribute<Msg> =
        on("submit") { payload ->
            when (payload) {
                is Payload.Form ->
                    f(FormData(payload.data))

                else ->
                    throw RuntimeException("Unexpected payload: $payload")
            }
        } // TODO: fix this and make

    /**
     *
     */
    fun <Msg> onBlur(msg: Msg): Attribute<Msg> =
        on("blur") { msg }

    /**
     *
     */
    fun <Msg> onFocus(msg: Msg): Attribute<Msg> =
        on("focus") { msg }

    //fun <Msg> stopPropagationOn(): Attribute<Msg> =
    //    on("") { msg }

    //fun <Msg> preventDefaultOn(): Attribute<Msg> =
    //    on("") { msg }

    //fun <Msg> custom(): Attribute<Msg> =
    //    on("") { msg }

    //fun <Msg> targetValue(): Attribute<Msg> =
    //    on("") { msg }

    //fun <Msg> targetChecked(): Attribute<Msg> =
    //    on("") { msg }

    //fun <Msg> keyCode(): Attribute<Msg> =
    //    on("") { msg }


}


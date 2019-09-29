package io.noobymatze.live.page

import io.noobymatze.live.page.html.Html


interface Program<Model, Msg> {

    fun init(): Model

    fun update(msg: Msg, model: Model): Model

    fun view(model: Model): Html<Msg>

}
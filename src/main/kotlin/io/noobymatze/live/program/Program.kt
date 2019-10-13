package io.noobymatze.live.program

import io.noobymatze.live.html.Html


/**
 * A [Program] describes a Wicket live program.
 *
 * It can be instantiated in different modes.
 */
interface Program<Flags, Model, Msg> {

    /**
     * Initialize the [Program]
     *
     * @params
     */
    fun init(flags: Flags): Pair<Model, Cmd<Msg>>

    /**
     * @param msg
     * @param model
     */
    fun update(msg: Msg, model: Model): Pair<Model, Cmd<Msg>>

    /**
     *
     * @param model
     */
    fun view(model: Model): Html<Msg>

    /**
     * @param model
     * @return a new Sub
     */
    fun subscriptions(model: Model): Sub<Msg> = Sub.none


    companion object {

        /**
         *
         * @param init
         * @param update
         * @param view
         */
        fun <Flags, Model, Msg> program(
            init: (Flags) -> Model,
            update: (Msg, Model) -> Model,
            view: (Model) -> Html<Msg>
        ): Program<Flags, Model, Msg> = object: Program<Flags, Model, Msg> {

            override fun init(flags: Flags): Pair<Model, Cmd<Msg>> =
                init(flags) to Cmd.none

            override fun update(msg: Msg, model: Model): Pair<Model, Cmd<Msg>> =
                update(msg, model) to Cmd.none


            override fun view(model: Model): Html<Msg> =
                view(model)

        }

    }

}
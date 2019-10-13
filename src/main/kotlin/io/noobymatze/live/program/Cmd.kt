package io.noobymatze.live.program


/**
 *
 */
sealed class Cmd<out Msg> {

    /**
     *
     */
    internal object None: Cmd<Nothing>()

    /**
     *
     * @param f
     */
    internal data class Effect<out A, out Msg>(
        internal val toMsg: (Result<Throwable, @UnsafeVariance A>) -> Msg,
        internal val run: () -> A
    ): Cmd<Msg>()

    /**
     *
     * @param commands
     */
    internal data class Batch<out Msg>(
        internal val commands: List<Cmd<Msg>>
    ): Cmd<Msg>()


    companion object {

        /**
         *
         */
        val none: Cmd<Nothing> = None

        /**
         *
         * @param toMsg
         * @param run
         * @return
         */
        fun <Msg, A> effect(
            toMsg: (Result<Throwable, A>) -> Msg,
            run: () -> A
        ): Cmd<Msg> =
            Effect(toMsg, run)

        /**
         *
         * @param commands
         * @return
         */
        fun <Msg> batch(commands: List<Cmd<Msg>>): Cmd<Msg> =
            Batch(commands)

    }

}
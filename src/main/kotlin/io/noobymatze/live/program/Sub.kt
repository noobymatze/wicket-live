package io.noobymatze.live.program


/**
 *
 */
sealed class Sub<out Msg> {

    /**
     *
     */
    object None: Sub<Nothing>()


    companion object {

        /**
         *
         */
        val none: Sub<Nothing> = None

    }

}

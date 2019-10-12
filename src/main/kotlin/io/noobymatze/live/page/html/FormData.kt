package io.noobymatze.live.page.html

import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

data class FormData(
    private val data: Map<String, String?>
): Serializable {


    operator fun get(name: String): String? = data[name]
    operator fun <R> get(prop: KProperty<R>): R? {
        val value = data[prop.name]
        return when (prop.returnType.jvmErasure) {
            String::class ->
                value as R?

            Int::class ->
                value?.toIntOrNull() as R?

            Double::class ->
                value?.toDoubleOrNull() as R?

            else ->
                null
        }
    }

}


package io.noobymatze.live.page.internal

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlin.reflect.KClass


internal object JSON {

    private val mapper = ObjectMapper()
        .registerKotlinModule()

    @Throws(JsonProcessingException::class)
    fun <T: Any> unsafeStringify(value: T): String =
        mapper.writeValueAsString(value)

    @Throws(JsonProcessingException::class)
    fun <T: Any> unsafeParse(value: String, clazz: KClass<T>): T =
        mapper.readValue(value, clazz.java)

    @Throws(JsonProcessingException::class)
    fun <T> unsafeParse(value: String, clazz: Class<T>): T =
        mapper.readValue(value, clazz)
}
package io.noobymatze.live.page

import org.apache.wicket.protocol.ws.api.registry.IKey
import java.io.Serializable


data class LiveSession(
    val applicationKey: String,
    val key: IKey,
    val sessionId: String
): Serializable
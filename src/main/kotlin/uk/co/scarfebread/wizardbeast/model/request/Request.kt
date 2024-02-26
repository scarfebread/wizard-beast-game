package uk.co.scarfebread.wizardbeast.model.request

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

sealed class Request() {
    fun toJson(json: Json): String {
        return json.encodeToString(this)
    }
}

package uk.co.scarfebread.wizardbeast.model.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import uk.co.scarfebread.wizardbeast.model.request.Request

@Serializable
data class PlayerActionRequest(
    val id: String,
    val actions: List<Action>,
    val action: String = "update"
) : Request() {
    @Serializable
    class Action(val action: String, val value: String)
}

package uk.co.scarfebread.wizardbeast.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PlayerActionRequest(
    val id: String,
    val actions: List<Action>,
    val action: String = "update"
) : Request() {
    @Serializable
    class Action(val key: Int, val currentlyPressed: Boolean)
}

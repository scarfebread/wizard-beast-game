package uk.co.scarfebread.wizardbeast.state.publishable

import kotlinx.serialization.Serializable
import uk.co.scarfebread.wizardbeast.model.Player

@Serializable
data class PlayerState(
    val id: String,
    val name: String,
    var x: Float = 0F,
    var y: Float = 0F,
) {
    fun toPlayer() = Player(id, name, x, y)
}


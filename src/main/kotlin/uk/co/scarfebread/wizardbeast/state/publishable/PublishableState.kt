package uk.co.scarfebread.wizardbeast.state.publishable

import kotlinx.serialization.Serializable
import uk.co.scarfebread.wizardbeast.state.publishable.action.PlayerAction

@Serializable
data class PublishableState(
    val player: PlayerState,
    val players: List<PlayerAction>,
    val projectiles: List<String>, // TODO
    val enemies: List<String>, // TODO
    val timestamp: Long
)

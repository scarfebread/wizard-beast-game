package uk.co.scarfebread.wizardbeast.engine.state.publishable.action

import kotlinx.serialization.Serializable

@Serializable
data class MoveAction(
    val player: String,
    val x: Float,
    val y: Float,
    val input: Input,
    val action: String = "move",
) : PlayerAction

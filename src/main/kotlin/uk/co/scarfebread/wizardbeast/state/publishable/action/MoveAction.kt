package uk.co.scarfebread.wizardbeast.state.publishable.action

import kotlinx.serialization.Serializable

@Serializable
data class MoveAction(
    val name: String,
    val x: Float,
    val y: Float,
    val action: String = "move",
) : PlayerAction

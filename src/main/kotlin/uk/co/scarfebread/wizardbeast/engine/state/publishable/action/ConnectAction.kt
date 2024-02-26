package uk.co.scarfebread.wizardbeast.engine.state.publishable.action

import kotlinx.serialization.Serializable
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.PlayerAction

@Serializable
data class ConnectAction(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val action: String = "connect",
) : PlayerAction

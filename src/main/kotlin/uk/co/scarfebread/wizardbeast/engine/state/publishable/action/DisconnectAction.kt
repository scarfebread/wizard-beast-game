package uk.co.scarfebread.wizardbeast.engine.state.publishable.action

import kotlinx.serialization.Serializable

@Serializable
data class DisconnectAction(
    val player: String,
    val action: String = "disconnect",
) : PlayerAction

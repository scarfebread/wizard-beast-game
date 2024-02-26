package uk.co.scarfebread.wizardbeast.state.publishable.action

import kotlinx.serialization.Serializable

@Serializable
data class DisconnectAction(
    val name: String,
    val action: String = "disconnect",
) : PlayerAction

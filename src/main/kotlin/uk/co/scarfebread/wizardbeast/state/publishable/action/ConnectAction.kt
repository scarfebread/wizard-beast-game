package uk.co.scarfebread.wizardbeast.state.publishable.action

import kotlinx.serialization.Serializable

@Serializable
data class ConnectAction(
    val name: String,
    val x: Int,
    val y: Int,
    val action: String = "connect",
) : PlayerAction

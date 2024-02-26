package uk.co.scarfebread.wizardbeast.state.publishable

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val name: String,
    var x: Int = 0,
    var y: Int = 0,
)

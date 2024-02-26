package uk.co.scarfebread.wizardbeast.model

import kotlinx.serialization.Serializable

// TODO what's the point of this class vs PlayerState
@Serializable
data class Player(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
)

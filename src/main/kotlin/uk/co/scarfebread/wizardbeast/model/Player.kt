package uk.co.scarfebread.wizardbeast.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
)

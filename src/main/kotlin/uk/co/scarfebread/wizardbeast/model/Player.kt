package uk.co.scarfebread.wizardbeast.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(val id: String, val name: String, val x: Int, val y: Int)

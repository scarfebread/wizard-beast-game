package model

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, var position: Float)

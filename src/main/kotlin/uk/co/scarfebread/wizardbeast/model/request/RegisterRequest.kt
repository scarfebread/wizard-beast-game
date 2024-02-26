package uk.co.scarfebread.wizardbeast.model.request

import kotlinx.serialization.Serializable

@Serializable
class RegisterRequest(
    val name: String,
    val action: String = "register"
) : Request()

package uk.co.scarfebread.wizardbeast.model.request

import kotlinx.serialization.Serializable

@Serializable
class DeregisterRequest(
    val id: String,
    val action: String = "deregister"
) : Request()

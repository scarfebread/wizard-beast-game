package uk.co.scarfebread.wizardbeast.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AcknowledgeRequest(
    val stateId: Long,
    val player: String,
) : Request()

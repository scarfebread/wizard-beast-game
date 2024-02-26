package uk.co.scarfebread.wizardbeast.state

import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readUTF8Line
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import uk.co.scarfebread.wizardbeast.model.Player
import uk.co.scarfebread.wizardbeast.model.request.AcknowledgeRequest
import uk.co.scarfebread.wizardbeast.model.request.DeregisterRequest
import uk.co.scarfebread.wizardbeast.model.request.PlayerActionRequest
import uk.co.scarfebread.wizardbeast.model.request.PlayerActionRequest.Action
import uk.co.scarfebread.wizardbeast.model.request.RegisterRequest
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState
import uk.co.scarfebread.wizardbeast.state.publishable.PublishableState
import java.util.UUID

class BackendClient(
    private val clientSocket: BoundDatagramSocket,
    private val server: InetSocketAddress,
    private val gameStateManager: GameStateManager,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private val registrations = mutableMapOf<String, (PlayerState) -> Unit>()

    fun listen() = runBlocking {
        while (true) {
            val datagram = clientSocket.receive()
            val message = datagram.packet.readUTF8Line() ?: continue
            val split = message.split("--", limit = 3)

            if (split.size != 3) {
                println("[WARN] Invalid request: $message")
                continue
            }

            val eventType = split.first()
            val payload = split.second()
            val requestId = split.last()

            when(eventType) {
                "state" -> {
                    gameStateManager.processServerState(
                        payload.deserialise<PublishableState>().also {
                            acknowledge(it.player.id, it.stateId, requestId)
                        }
                    )
                }
                "registered" -> {
                    registrations[requestId]?.invoke(payload.deserialise<PlayerState>())
                }
                "invalid" -> {
                    println("[WARN] Invalid request: $message")
                }
            }
        }
    }

    fun registerPlayer(playerName: String, callback: (playerState: PlayerState) -> Unit) {
        val requestId = requestId()

        registrations[requestId] = { callback(it) }

        runBlocking {
            clientSocket.send(
                Datagram(
                    ByteReadPacket(
                        RegisterRequest(playerName)
                            .toJson(json)
                            .toRequest("register", requestId)
                            .encodeToByteArray()
                    ),
                    server
                )
            )
        }
    }

    fun movePlayer(player: String, x: Float, y: Float) {
        runBlocking {
            clientSocket.send(
                createActionDataGram(
                    player,
                    listOf(
                        Action("x", x.toString()),
                        Action("y", y.toString()),
                    ),
                    requestId()
                )
            )
        }
    }

    fun deletePlayer(player: String) {
        runBlocking {
            clientSocket.send(
                Datagram(
                    ByteReadPacket(
                        DeregisterRequest(player)
                            .toJson(json)
                            .encodeToByteArray()
                    ),
                    server
                )
            )
        }
    }

    private fun acknowledge(player: String, stateId: Long, requestId: String) {
        runBlocking {
            clientSocket.send(
                Datagram(
                    ByteReadPacket(
                        AcknowledgeRequest(stateId, player)
                            .toJson(json)
                            .toRequest("acknowledge", requestId)
                            .encodeToByteArray()
                    ),
                    server
                )
            )
        }
    }

    private fun createActionDataGram(player: String, actions: List<Action>, requestId: String) = Datagram(
        ByteReadPacket(
            PlayerActionRequest(
            player,
            actions
        ).toJson(json)
            .toRequest("update", requestId)
            .encodeToByteArray()
        ),
        server
    )

    private inline fun <reified T> String.deserialise() = json.decodeFromString<T>(this)

    private fun requestId() = UUID.randomUUID().toString()

    private fun List<String>.second() = this[1]

    private fun String.toRequest(event: String, requestId: String) = "$event--$this--$requestId"
}

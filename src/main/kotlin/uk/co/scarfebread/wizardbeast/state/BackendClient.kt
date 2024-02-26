package uk.co.scarfebread.wizardbeast.state

import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readUTF8Line
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import uk.co.scarfebread.wizardbeast.model.Player
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
            val split = message.split("-", limit = 3)

            if (split.size != 3) {
                println("[WARN] Invalid request: $message")
                continue
            }

            val eventType = split.first()
            val requestId = split.second()
            val payload = split.last()

            when(eventType) {
                "state" -> {
                    gameStateManager.processServerState(payload.deserialise<PublishableState>())
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

    fun registerPlayer(playerName: String) {
        val requestId = requestId()

        registrations[requestId] = {
            gameStateManager.playerRegistered(it)
        }

        runBlocking {
            clientSocket.send(
                Datagram(
                    ByteReadPacket(
                        RegisterRequest(requestId, playerName)
                            .toJson(json)
                            .encodeToByteArray()
                    ),
                    server
                )
            )
        }
    }

    fun movePlayer(player: Player) {
        runBlocking {
            clientSocket.send(
                createActionDataGram(
                    player,
                    listOf(
                        Action("x", player.x.toString()),
                        Action("y", player.y.toString()),
                    )
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

    private fun createActionDataGram(player: Player, actions: List<Action>) = Datagram(
        ByteReadPacket(
            PlayerActionRequest(
                player.id,
                actions
            ).toJson(json).encodeToByteArray()
        ),
        server
    )

    private inline fun <reified T> String.deserialise() = json.decodeFromString<T>(this)

    private fun requestId() = UUID.randomUUID().toString()

    private fun List<Any>.second() = this[1]
}

package uk.co.scarfebread.wizardbeast.state.client

import uk.co.scarfebread.wizardbeast.model.Player
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState
import uk.co.scarfebread.wizardbeast.state.publishable.PublishableState
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.ConnectAction
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.DisconnectAction
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.MoveAction
import uk.co.scarfebread.wizardbeast.game.actor.OtherPlayerSprite

class GameStateManager {
    var isReady = false

    lateinit var player: Player
    val players = mutableListOf<OtherPlayerSprite>()
    private val enemies = mutableListOf<String>()
    private val projectiles = mutableListOf<String>()

    private val buffer = mutableMapOf<Long, PublishableState>()

    private var lastStateProcessed = 0L
    private var latestState = 0L

    fun processServerState(publishableState: PublishableState) {
        player.x = publishableState.player.x
        player.y = publishableState.player.y

        publishableState.stateId.run {
            buffer[this] = publishableState
            if (this > latestState) {
                latestState = this
            }
            buffer.prune(this)
        }
    }

    fun processState() {
        if (buffer.isEmpty()) {
            return
        }

        val stateId = currentStateId() - BUFFER

        if (stateId == lastStateProcessed) {
            return
        }

        if (buffer[stateId] != null) {
            lastStateProcessed = processPlayerState(stateId, NO_MOVEMENT_WEIGHTING)
        } else if (stateId < latestState) {
            val nextStateId = getNextState(stateId)

            val distanceToPreviousState = stateId - lastStateProcessed
            val distanceToNextState = nextStateId - stateId
            val range = distanceToPreviousState + distanceToNextState
            val movementWeighting = distanceToPreviousState / range

            // we do not assign lastStateProcessed as it's a prediction
            // and if we receive the actual state we want to re-run this code
            processPlayerState(nextStateId, movementWeighting)
        } else if (stateId > latestState) {
            // TODO this means we're processing as we receive, which can lead to jank
            // post on reddit to ask how you deal with this (variable buffer?)
            lastStateProcessed = processPlayerState(latestState, NO_MOVEMENT_WEIGHTING)
        }
    }

    fun playerRegistered(playerState: PlayerState) {
        player = playerState.toPlayer()
        isReady = true
    }

    private fun processPlayerState(stateId: Long, movementWeighting: Long): Long {
        buffer[stateId]!!.players.forEach { action ->
            when (action) {
                is ConnectAction -> if (players.firstOrNull { it.player.id == action.id } == null) {
                    players.add(
                        OtherPlayerSprite(
                            PlayerState(
                                action.id,
                                action.name
                            ),
                            action.x,
                            action.y
                        )
                    )
                }
                is DisconnectAction -> players
                    .firstOrNull { it.player.id == action.player }
                    ?.let {
                        it.disconnected = true
                        players.remove(it)
                    }
                is MoveAction -> players.first { it.player.id == action.player }.apply {
                    predictMovement(movementWeighting, action.x, action.y)
                }
            }
        }

        return stateId
    }

    private fun MutableMap<Long, PublishableState>.prune(stateId: Long) {
        runCatching {
            entries.removeIf {
                it.key < stateId - BUFFER_LIMIT
            }
        }.onFailure {
            // TODO avoid this concurrency problem
            it.printStackTrace()
        }
    }

    // TODO we need time synchronisation between client and server if we're doing this
    private fun currentStateId(): Long {
        buffer[latestState]!!.let {
            val currentTimestamp = System.currentTimeMillis()

            var stateId = it.stateId
            var timestamp = it.timestamp

            while(true) {
                timestamp += 1000 / 64

                if (timestamp > currentTimestamp) {
                    break
                } else {
                    stateId++
                }
            }

            return stateId
        }
    }

    private fun getNextState(stateId: Long): Long {
        for (i in stateId + 1 .. latestState) {
            if (buffer[i] != null) {
                return i
            }
        }

        throw Exception("Should never get here")
    }

    companion object {
        private const val BUFFER = 5
        private const val BUFFER_LIMIT = 64
        private const val NO_MOVEMENT_WEIGHTING = 1L
    }
}

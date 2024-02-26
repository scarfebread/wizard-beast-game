package uk.co.scarfebread.wizardbeast.state.client

import uk.co.scarfebread.wizardbeast.model.Player
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState
import uk.co.scarfebread.wizardbeast.state.publishable.PublishableState
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.ConnectAction
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.DisconnectAction
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.MoveAction
import uk.co.scarfebread.wizardbeast.game.actor.OtherPlayerSprite

class GameStateManager {
    lateinit var player: Player
    val players = mutableListOf<OtherPlayerSprite>()
    private val enemies = mutableListOf<String>()
    private val projectiles = mutableListOf<String>()

    private val buffer = mutableMapOf<Long, PublishableState>()

    var nextStateAt = System.currentTimeMillis()
    var lastPulledState = 0L
    private var lastState = 0L

    fun players() = players.toList()

    fun processServerState(publishableState: PublishableState) {
        player.x = publishableState.player.x
        player.y = publishableState.player.y

        buffer[publishableState.stateId] = publishableState

        buffer.prune(publishableState.stateId)
    }

    fun processState() {
        if (buffer.isEmpty()) {
            return
        }

        val stateId = currentStateId() - BUFFER

        if (stateId == lastState || stateId > buffer.keys.last()) {
            return
        }

        if (buffer[stateId] != null) {
            processPlayerState(stateId, NO_MOVEMENT_WEIGHTING)

            lastState = stateId
        } else {
            val nextStateId = getNextState(stateId)

            val distanceToPreviousState = stateId - lastState
            val distanceToNextState = nextStateId - stateId
            val range = distanceToPreviousState + distanceToNextState
            val movementWeighting = distanceToPreviousState / range

            processPlayerState(nextStateId, movementWeighting)
        }
    }

    fun playerRegistered(playerState: PlayerState) {
        player = playerState.toPlayer()
    }

    private fun processPlayerState(stateId: Long, movementWeighting: Long) {
        buffer[stateId]!!.players.forEach { action ->
            when (action) {
                is ConnectAction -> players.add(
                    OtherPlayerSprite(
                        PlayerState(
                            action.id,
                            action.name
                        ),
                        action.x,
                        action.y
                    )
                )
                is DisconnectAction -> {
                    players.firstOrNull { it.name == action.name }?.let { // TODO should be ID
                        it.disconnected = true
                        players.remove(it)
                    }
                }
                is MoveAction -> {
                    players.first { it.name == action.name }.apply {
                        predictMovement(movementWeighting, action.x, action.y)
                    }
                }
            }
        }
    }

    private fun MutableMap<Long, PublishableState>.prune(stateId: Long) {
        if (size > BUFFER_LIMIT) {
            for (i in keys.first() .. stateId - BUFFER_LIMIT) {
                remove(i)
            }
        }
    }

    // TODO we need time synchronisation between client and server if we're doing this
    private fun currentStateId(): Long {
        buffer.entries.maxBy { it.key }.value.let {
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
        for (i in stateId + 1 .. buffer.keys.max()) {
            if (buffer[i] != null) {
                return i
            }
        }

        throw Exception("Should never get here")
    }

    companion object {
        private const val BUFFER = 5
        private const val BUFFER_LIMIT = 64
        private const val NO_MOVEMENT_WEIGHTING = 0L
    }
}

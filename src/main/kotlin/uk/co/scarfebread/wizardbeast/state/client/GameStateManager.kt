package uk.co.scarfebread.wizardbeast.state.client

import uk.co.scarfebread.wizardbeast.model.Player
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState
import uk.co.scarfebread.wizardbeast.state.publishable.PublishableState
import uk.co.scarfebread.wizardbeast.state.publishable.action.ConnectAction
import uk.co.scarfebread.wizardbeast.state.publishable.action.DisconnectAction
import uk.co.scarfebread.wizardbeast.state.publishable.action.MoveAction

class GameStateManager {
    private lateinit var player: Player
    private val players = mutableListOf<PlayerState>()
    private val enemies = mutableListOf<String>()
    private val projectiles = mutableListOf<String>()

    fun players() = players.toList()

    fun processServerState(publishableState: PublishableState) {
        publishableState.players.forEach { action ->
            when (action) {
                is ConnectAction -> players.add(PlayerState(action.id, action.name, action.x, action.y))
                is DisconnectAction -> players.removeAll { it.name == action.name } // TODO should be ID
                is MoveAction -> {
                    players.first { it.name == action.name }.apply {
                        x = action.x
                        y = action.y
                    }
                }
            }
        }
    }

    fun playerRegistered(playerState: PlayerState) {
        player = playerState.toPlayer()
    }
}

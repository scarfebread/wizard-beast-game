package uk.co.scarfebread.wizardbeast.state.client

import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.PlayerAction

class ClientState(
    val stateId: Long,
    val players: List<PlayerAction>,
    val projectiles: List<String>, // TODO
    val enemies: List<String>, // TODO
)

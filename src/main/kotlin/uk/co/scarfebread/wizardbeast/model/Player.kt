package uk.co.scarfebread.wizardbeast.model

import uk.co.scarfebread.wizardbeast.game.actor.PlayerControlledWizard

data class Player(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
    var lastState: Long = 0L,
    var wizard: PlayerControlledWizard? = null
)

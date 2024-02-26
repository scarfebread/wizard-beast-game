package uk.co.scarfebread.wizardbeast.game.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import uk.co.scarfebread.wizardbeast.game.actor.PlayerControlledWizard

class PlayerInputListener(private val wizard: PlayerControlledWizard) : InputListener() {
    private val actions = mutableMapOf<Int, Boolean>()

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        processKeyAction(keycode, true)

        return true
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        processKeyAction(keycode, false)

        return true
    }

    private fun processKeyAction(keycode: Int, currentlyPressed: Boolean) {
        actions[keycode] = currentlyPressed

        when(keycode) {
            Input.Keys.LEFT -> {
                wizard.movingLeft = currentlyPressed
                actions[keycode] = currentlyPressed
            }
            Input.Keys.RIGHT -> {
                wizard.movingRight = currentlyPressed
                actions[keycode] = currentlyPressed
            }
            Input.Keys.UP -> {
                wizard.movingUp = currentlyPressed
                actions[keycode] = currentlyPressed
            }
            Input.Keys.DOWN -> {
                wizard.movingDown = currentlyPressed
                actions[keycode] = currentlyPressed
            }
        }
    }

    fun consumeActions(): Map<Int, Boolean> {
        return actions.toMap().also {
            actions.clear()
        }
    }
}

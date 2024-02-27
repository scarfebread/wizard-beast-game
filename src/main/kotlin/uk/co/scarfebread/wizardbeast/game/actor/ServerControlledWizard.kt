package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.Input
import uk.co.scarfebread.wizardbeast.model.Location
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState

class ServerControlledWizard(
    player: PlayerState,
    x: Float,
    y: Float,
    private var previousX: Float = x,
    private var previousY: Float = y,
    var connected: Boolean = true,
    var disconnected: Boolean = false,
) : WizardSprite(player.toPlayer(), Location(x, y)) {
    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disconnected) {
            remove()
            return
        }

        synchroniseLocation()
        animate()

        batch.draw(
            Texture(texture),
            location.x,
            location.y,
            SIZE,
            SIZE
        )
    }

    private fun synchroniseLocation() {
        val xDiff = player.x - location.x
        val yDiff = player.y - location.y

        if (
            !input.left &&
            !input.right &&
            xDiff != 0f
        ) {
            synchroniseX(xDiff)
        }

        if (
            !input.up &&
            !input.down &&
            yDiff != 0f
        ) {
            synchroniseY(yDiff)
        }
    }

    fun predictMovement(movementWeighting: Long, x: Float, y: Float, input: Input) {
        previousX = location.x
        previousY = location.y
        player.x = move(movementWeighting, location.x, x)
        player.y = move(movementWeighting, location.y, y)
        this.setInput(input)
    }

    private fun move(movementWeighting: Long, oldLocation: Float, newLocation: Float): Float {
        return if (oldLocation != newLocation) {
            val difference = newLocation - oldLocation

            oldLocation + (difference * movementWeighting)
        } else {
            oldLocation
        }
    }
}

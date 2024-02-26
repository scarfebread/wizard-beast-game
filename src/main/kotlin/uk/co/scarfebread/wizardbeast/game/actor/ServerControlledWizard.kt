package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState

class ServerControlledWizard(
    val player: PlayerState,
    private var x: Float,
    private var y: Float,
    private var previousX: Float = x,
    private var previousY: Float = y,
    var connected: Boolean = true,
    var disconnected: Boolean = false,
) : WizardSprite() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disconnected) {
            remove()
            return
        }

        batch.draw(
            Texture(texture),
            x,
            y,
            SIZE,
            SIZE
        )
    }

    fun predictMovement(movementWeighting: Long, x: Float, y: Float) {
        previousX = this.x
        previousY = this.y
        this.x = move(movementWeighting, this.x, x)
        this.y = move(movementWeighting, this.y, y)
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

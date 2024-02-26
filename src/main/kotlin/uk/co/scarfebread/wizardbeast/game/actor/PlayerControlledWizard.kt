package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.model.Player

class PlayerControlledWizard(
    val player: Player,
    private var x: Float,
    private var y: Float,
) : WizardSprite() {
    var movingRight = false
    var movingLeft = false
    var movingUp = false
    var movingDown = false

    override fun draw(batch: Batch, parentAlpha: Float) = runBlocking {
        animate()

        batch.draw(
            Texture(texture),
            x,
            y,
            SIZE,
            SIZE
        )
    }

    private fun animate() {
        if (movingRight) { x += SPEED * Gdx.graphics.deltaTime }
        if (movingLeft) { x -= SPEED * Gdx.graphics.deltaTime }
        if (movingUp) { y += SPEED * Gdx.graphics.deltaTime }
        if (movingDown) { y -= SPEED * Gdx.graphics.deltaTime }

        if (!x.withinReasonableDistanceOf(player.x) || !y.withinReasonableDistanceOf(player.y)) {
            println("forcing player position")
            x = player.x
            y = player.y
        }
    }

    private fun Float.withinReasonableDistanceOf(otherPosition: Float) =
        this < otherPosition + REASONABLE_DISTANCE &&
        this > otherPosition - REASONABLE_DISTANCE

    companion object {
        private const val SPEED = 100f
        private const val REASONABLE_DISTANCE = 50f
    }
}

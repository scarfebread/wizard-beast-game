package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.model.Location
import uk.co.scarfebread.wizardbeast.model.Player

class PlayerControlledWizard(player: Player, x: Float, y: Float) : WizardSprite(player, Location(x, y)) {
    val inputsSinceState = mutableListOf<Int>()

    init {
        player.wizard = this
    }

    override fun draw(batch: Batch, parentAlpha: Float) = runBlocking {
        animate()

        batch.draw(
            Texture(texture),
            location.x,
            location.y,
            SIZE,
            SIZE
        )
    }

    fun synchroniseLocation() {
        val xDiff = player.x - location.x
        val yDiff = player.y - location.y

        if (
            !inputsSinceState.contains(Keys.LEFT) &&
            !inputsSinceState.contains(Keys.RIGHT) &&
            !input.left &&
            !input.right &&
            xDiff != 0f
        ) {
            synchroniseX(xDiff)
        }

        // TODO refactor duplication
        if (
            !inputsSinceState.contains(Keys.UP) &&
            !inputsSinceState.contains(Keys.DOWN) &&
            !input.up &&
            !input.down &&
            yDiff != 0f
        ) {
            synchroniseY(yDiff)
        }

        inputsSinceState.clear()
    }
}

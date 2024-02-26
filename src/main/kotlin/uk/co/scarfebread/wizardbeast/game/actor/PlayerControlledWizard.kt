package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.model.Location
import uk.co.scarfebread.wizardbeast.model.Player

class PlayerControlledWizard(player: Player, x: Float, y: Float) : WizardSprite(player, Location(x, y)) {
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
}

package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class OtherPlayerSprite(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
    var previousX: Float = x,
    var previousY: Float = y,
    var connected: Boolean = true,
    var disconnected: Boolean = false,
) : Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disconnected) {
            remove()
        }

        batch.draw(
            Texture(Gdx.files.internal("src/main/resources/assets/bucket.png")),
            x,
            y,
            25f,
            25f
        )
    }

    fun predictMovement(movementWeighting: Long, x: Float, y: Float) {
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

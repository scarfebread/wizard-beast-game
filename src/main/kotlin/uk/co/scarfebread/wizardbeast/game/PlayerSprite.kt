package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class PlayerSprite(private var x: Float, private var y: Float) : Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(
            Texture(Gdx.files.internal("src/main/resources/assets/bucket.png")),
            x,
            y,
            25f,
            25f
        )
    }

    fun up() { y += SPEED }
    fun down() { y -= SPEED }
    fun left() { x -= SPEED }
    fun right() { x += SPEED }

    companion object {
        private const val SPEED = 3f
    }
}

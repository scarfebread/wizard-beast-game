package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import uk.co.scarfebread.wizardbeast.model.Player
import uk.co.scarfebread.wizardbeast.state.BackendClient

class PlayerSprite(
    private val backendClient: BackendClient,
    private val player: Player,
    private var x: Float,
    private var y: Float
) : Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(
            Texture(Gdx.files.internal("src/main/resources/assets/bucket.png")),
            x,
            y,
            25f,
            25f
        )

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { left() }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { right() }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) { up() }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { down() }
    }

    private fun up() { y += SPEED }
    private fun down() { y -= SPEED }
    private fun left() { x -= SPEED }
    private fun right() { x += SPEED }

    fun publish() {
        if (x != player.x || y != player.y) {
            backendClient.movePlayer(player)
        }
    }

    fun animate() {

    }

    companion object {
        private const val SPEED = 3f
    }
}
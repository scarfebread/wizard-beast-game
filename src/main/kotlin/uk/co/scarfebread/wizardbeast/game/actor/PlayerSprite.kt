package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.model.Player
import uk.co.scarfebread.wizardbeast.state.BackendClient

class PlayerSprite(
    private val backendClient: BackendClient,
    private val player: Player,
    private var x: Float,
    private var y: Float,
) : Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) = runBlocking {
        batch.draw(
            Texture(Gdx.files.internal("src/main/resources/assets/bucket.png")),
            x,
            y,
            25f,
            25f
        )

        animate()

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { left() }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { right() }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) { up() }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { down() }
    }

    private fun up() { y += SPEED; publish() }
    private fun down() { y -= SPEED; publish() }
    private fun left() { x -= SPEED; publish() }
    private fun right() { x += SPEED; publish() }

    private fun publish() = runBlocking {
        // TODO I should only send 64 times a second
        if (x != player.x || y != player.y) {
            launch(Dispatchers.IO) {
                backendClient.movePlayer(player.id, x, y)
            }
        }
    }

    private fun animate() {
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
        private const val SPEED = 3f
        private const val REASONABLE_DISTANCE = 50f
    }
}

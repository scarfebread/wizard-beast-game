package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Actor
import uk.co.scarfebread.wizardbeast.engine.state.publishable.action.Input
import uk.co.scarfebread.wizardbeast.model.Location
import uk.co.scarfebread.wizardbeast.model.Player

sealed class WizardSprite(val player: Player, protected var location: Location) : Actor() {
    protected val texture: FileHandle = Gdx.files.internal("src/main/resources/assets/bucket.png")
    val input: Input = Input()

    protected fun animate() {
        if (input.right) { location.x += SPEED * Gdx.graphics.deltaTime }
        if (input.left) { location.x -= SPEED * Gdx.graphics.deltaTime }
        if (input.up) { location.y += SPEED * Gdx.graphics.deltaTime }
        if (input.down) { location.y -= SPEED * Gdx.graphics.deltaTime }

        if (!location.x.withinReasonableDistanceOf(player.x) || !location.y.withinReasonableDistanceOf(player.y)) {
            println("forcing player ${player.name} position")
            location.x = player.x
            location.y = player.y
        }
    }

    private fun Float.withinReasonableDistanceOf(otherPosition: Float) =
        this < otherPosition + REASONABLE_DISTANCE &&
        this > otherPosition - REASONABLE_DISTANCE

    protected fun setInput(input: Input) = this.input.apply {
        this.down = input.down
        this.up = input.up
        this.left = input.left
        this.right = input.right
    }

    protected fun synchroniseX(diff: Float) = run { location.x += synchroniseLocation(diff) }

    protected fun synchroniseY(diff: Float) = run { location.y += synchroniseLocation(diff) }

    private fun synchroniseLocation(diff: Float): Float {
        // TODO this mainly fixes the problem but there's a slight jank to it visually
        // maybe it should do speed * delta time? we'd just need to make sure it doesn't overshoot
        (diff * Gdx.graphics.deltaTime).let { speed ->
            // TODO I think if we fixed the above TODO we wouldn't need this
            return if (speed > 0.1f || speed < -0.1f) {
                speed
            } else {
                0f
            }
        }
    }

    companion object {
        internal const val SIZE = 25f
        internal const val SPEED = 100f
        internal const val REASONABLE_DISTANCE = 50f
    }
}

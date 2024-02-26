package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import uk.co.scarfebread.wizardbeast.game.actor.PlayerControlledWizard
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager

class GameScreen(
    private val stage: Stage,
    backendClient: BackendClient,
    private val gameStateManager: GameStateManager
) : Screen {
    private var camera: OrthographicCamera = OrthographicCamera()
    private var playerControlledWizard: PlayerControlledWizard = PlayerControlledWizard(
        backendClient,
        gameStateManager.player,
        gameStateManager.player.x,
        gameStateManager.player.y
    )

    init {
        camera.setToOrtho(false, 800f, 480f)

        stage.addActor(playerControlledWizard)
        gameStateManager.players.forEach {
            stage.addActor(it)
        }
    }

    override fun render(delta: Float) {
        gameStateManager.processState()

        gameStateManager.players.forEach {
            if (it.connected) {
                it.connected = false
                stage.addActor(it)
            }
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        camera.update()

        stage.act(delta)
        stage.draw()

        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) { }
    override fun hide() { }
    override fun pause() { }
    override fun resume() { }
    override fun show() {}
    override fun dispose() {}
}

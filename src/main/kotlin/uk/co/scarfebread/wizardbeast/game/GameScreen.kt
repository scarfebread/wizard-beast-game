package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.game.actor.PlayerControlledWizard
import uk.co.scarfebread.wizardbeast.game.input.PlayerInputListener
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager


class GameScreen(
    private val stage: Stage,
    private val backendClient: BackendClient,
    private val gameStateManager: GameStateManager
) : Screen {
    private val camera: OrthographicCamera = OrthographicCamera()
    private val playerControlledWizard = PlayerControlledWizard(
        gameStateManager.player,
        gameStateManager.player.x,
        gameStateManager.player.y
    )
    private val inputListener = PlayerInputListener(playerControlledWizard)

    init {
        camera.setToOrtho(false, 800f, 480f)

        stage.addActor(playerControlledWizard)

        gameStateManager.players.forEach {
            stage.addActor(it)
        }

        stage.addListener(inputListener)

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) = runBlocking {
        inputListener.consumeActions().let {
            if (it.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    backendClient.registerInput(playerControlledWizard.player.id, it)
                }
            }
        }

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
    }

    override fun resize(width: Int, height: Int) { }
    override fun hide() { }
    override fun pause() { }
    override fun resume() { }
    override fun show() {}
    override fun dispose() {}
}

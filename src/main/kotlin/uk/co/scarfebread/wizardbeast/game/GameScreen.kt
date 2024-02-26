package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.game.actor.PlayerSprite
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager

class GameScreen(
    private val stage: Stage,
    backendClient: BackendClient,
    gameStateManager: GameStateManager
) : Screen {
    private var camera: OrthographicCamera = OrthographicCamera()
    private var playerSprite: PlayerSprite = PlayerSprite(
        backendClient,
        gameStateManager.player,
        gameStateManager.player.x,
        gameStateManager.player.y
    )

    init {
        camera.setToOrtho(false, 800f, 480f)

        stage.addActor(playerSprite)
    }

    override fun render(delta: Float) = runBlocking {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        camera.update()

        stage.act(delta)
        stage.draw()

        launch(Dispatchers.IO) {
            playerSprite.publish()
        }

        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) { }
    override fun hide() { }
    override fun pause() { }
    override fun resume() { }
    override fun show() {}
    override fun dispose() {}
}

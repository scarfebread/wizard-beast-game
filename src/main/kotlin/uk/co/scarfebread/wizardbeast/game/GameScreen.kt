package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.ScreenUtils
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager

class GameScreen(private val game: WizardBeast, private val gameStateManager: GameStateManager) : Screen {
    private var camera: OrthographicCamera = OrthographicCamera()
    private var playerSprite: PlayerSprite = PlayerSprite(gameStateManager.player.x, gameStateManager.player.y)

    init {
        camera.setToOrtho(false, 800f, 480f)

        game.stage.addActor(playerSprite)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        camera.update()

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.stage.act(delta)
        game.stage.draw()

        Gdx.input.inputProcessor = game.stage

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { playerSprite.left() }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { playerSprite.right() }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) { playerSprite.up() }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { playerSprite.down() }
    }

    override fun resize(width: Int, height: Int) { }
    override fun hide() { }
    override fun pause() { }
    override fun resume() { }

    override fun show() {

    }

    override fun dispose() {

    }
}

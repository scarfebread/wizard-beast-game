package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.utils.ScreenUtils
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager

class MainMenu(
    private val game: WizardBeast,
    private val backendClient: BackendClient,
    private val gameStateManager: GameStateManager
) : Screen {
    private var camera: OrthographicCamera = OrthographicCamera()
    private val usernameTextField = TextField(
        USERNAME_DEFAULT_TEXT,
        TextFieldStyle(
            game.font,
            Color.WHITE,
            null,
            null,
            null
        )
    ).apply {
        setPosition(100f, 100f)
    }
    private val usernameLabel = label("Wizard Beast - the game", 100f, 150f)
    private val usernameError = label("you suck at providing a name", 100f, 50f)

    private var submitted = false

    init {
        camera.setToOrtho(false, 800f, 480f)

        game.stage.addActor(usernameTextField)
        game.stage.addActor(usernameLabel)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.stage.act(delta)
        game.stage.draw()

        Gdx.input.inputProcessor = game.stage

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            usernameTextField.text.let { username ->
                if (username == "" || username == USERNAME_DEFAULT_TEXT) {
                    game.stage.addActor(usernameError)
                } else {
                    if (submitted) return

                    submitted = true
                    usernameError.remove()

                    backendClient.registerPlayer(username) {
                        runCatching {
                            gameStateManager.playerRegistered(it)
                            game.screen.dispose()
                            game.screen = GameScreen(game.stage, backendClient, gameStateManager)
                            dispose()
                        }.onFailure {
                            usernameError.setText(it.message)
                            game.stage.addActor(usernameError)
                            submitted = false
                        }
                    }
                }
            }
        }
    }

    override fun show() {

    }

    override fun resize(width: Int, height: Int) {
        game.stage.viewport.update(width, height, true)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        usernameLabel.remove()
        usernameError.remove()
        usernameTextField.remove()
    }

    private fun label(text: String, x: Float, y: Float) = Label(
        text,
        LabelStyle(
            game.font,
            Color.WHITE
        )
    ).apply {
        setPosition(x, y)
    }

    companion object {
        private const val USERNAME_DEFAULT_TEXT = "<enter name>"
    }
}

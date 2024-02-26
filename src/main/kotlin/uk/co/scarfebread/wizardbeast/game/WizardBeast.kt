package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager
import uk.co.scarfebread.wizardbeast.state.publishable.PlayerState

class WizardBeast(
    private val client: BackendClient,
    private val playerId: String,
    private val players: List<PlayerState>,
    private val backendClient: BackendClient,
    private val gameStateManager: GameStateManager
) : Game() {
    lateinit var stage: Stage
    lateinit var font: BitmapFont
    private val registrationEnabled = false

    override fun create() {
        font = BitmapFont()
        stage = Stage()
        if (registrationEnabled) {
            this.setScreen(MainMenu(this, backendClient, gameStateManager))
        } else {
            this.setScreen(GameScreen(this, gameStateManager))
        }
    }

    override fun dispose() {
        getScreen().dispose()

        font.dispose()

        stage.dispose()
    }
}

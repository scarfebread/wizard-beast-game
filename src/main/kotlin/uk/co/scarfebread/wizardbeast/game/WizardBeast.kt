package uk.co.scarfebread.wizardbeast.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager

class WizardBeast(
    private val backendClient: BackendClient,
    private val gameStateManager: GameStateManager
) : Game() {
    lateinit var stage: Stage
    lateinit var font: BitmapFont
    private val registrationEnabled = true

    override fun create() {
        font = BitmapFont()
        stage = Stage()
        if (registrationEnabled) {
            this.setScreen(MainMenu(this, backendClient, gameStateManager))
        } else {
            this.setScreen(GameScreen(stage, backendClient, gameStateManager))
        }
    }

    override fun dispose() {
        screen.dispose()
        font.dispose()
        stage.dispose()

        backendClient.deregisterPlayer(
            gameStateManager.player.id
        )
        backendClient.close()
    }
}

package uk.co.scarfebread.wizardbeast.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor

sealed class WizardSprite : Actor() {
    internal val texture = Gdx.files.internal("src/main/resources/assets/bucket.png")

    companion object {
        internal const val SIZE = 25f
    }
}

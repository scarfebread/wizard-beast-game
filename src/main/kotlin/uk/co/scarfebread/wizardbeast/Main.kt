package uk.co.scarfebread.wizardbeast

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import uk.co.scarfebread.wizardbeast.game.WizardBeast
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.printStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.game.MainMenu
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.state.client.GameStateManager
import java.util.*

fun main(args: Array<String>) = runBlocking {
    val clientPort = args[0]

    val gameStateManager = GameStateManager()

    val server = InetSocketAddress("127.0.0.1", 9002)

    val client = BackendClient(
        aSocket(SelectorManager(Dispatchers.IO))
            .udp()
            .bind(InetSocketAddress("127.0.0.1", clientPort.toInt())),
        server,
        gameStateManager
    )

    launch(Dispatchers.IO) {
        client.listen()
    }

    val game = WizardBeast(client, gameStateManager)

    runCatching {
        Lwjgl3Application(game, Lwjgl3ApplicationConfiguration().apply {
            setTitle("Wizard Beast")
            setWindowedMode(800, 480)
            useVsync(true)
            setForegroundFPS(60)
        })
    }.onFailure {
        println(it.message)
        it.printStackTrace()
    }

    println("bye")
}

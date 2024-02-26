package uk.co.scarfebread.wizardbeast

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import uk.co.scarfebread.wizardbeast.game.Bucket
import uk.co.scarfebread.wizardbeast.game.Drop
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.scarfebread.wizardbeast.state.BackendClient
import uk.co.scarfebread.wizardbeast.client.OnlineManager
import java.util.*

fun main(args: Array<String>) {
    runBlocking {
        val server = InetSocketAddress("127.0.0.1", 9002)
        val client = BackendClient(
            aSocket(SelectorManager(Dispatchers.IO))
                .udp()
                .bind(InetSocketAddress("127.0.0.1", 9003)),
            server
        )

        val onlineManager = OnlineManager(client)
        val buckets = mutableListOf<Bucket>()
        val playerId = UUID.randomUUID().toString()

        launch {
            onlineManager.updatePlayers(buckets, playerId)
        }

//        Runtime.getRuntime().addShutdownHook(Thread({ client.deleteUser(playerId) }, "Shutdown-thread"))

        val game = Drop(client, playerId, buckets)

        Lwjgl3Application(game, Lwjgl3ApplicationConfiguration().apply {
            setTitle("Drop")
            setWindowedMode(800, 480)
            useVsync(true)
            setForegroundFPS(60)
        })
    }
}

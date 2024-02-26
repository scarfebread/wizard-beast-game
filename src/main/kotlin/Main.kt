import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import game.Bucket
import game.Drop
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import online.BackendClient
import online.OnlineManager
import java.util.*

fun main(args: Array<String>) {
    runBlocking {
        val client = BackendClient(
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }
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

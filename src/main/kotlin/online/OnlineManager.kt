package online

import game.Bucket
import kotlinx.coroutines.delay

class OnlineManager(private val client: BackendClient) {
    suspend fun updatePlayers(buckets: MutableList<Bucket>, playerId: String) {
        while (true) {
            client.getUsers(playerId).let { updatedUsers ->
                buckets.forEach { bucket ->
                    val updatedUser = updatedUsers.firstOrNull { it.id == bucket.id }

                    if (updatedUser == null) {
                        buckets.remove(bucket)
                    } else {
                        bucket.x = updatedUser.position
                    }
                }

                updatedUsers.forEach { updatedUser ->
//                buckets.firstOrNull { it.id == updatedUser.id } ?: {
//                    println("adding bucket ${updatedUser.id}")
//                    buckets.add(Bucket(updatedUser.id))
//                }

                    val eh = buckets.firstOrNull { it.id == updatedUser.id }
                    if (eh == null) {
                        buckets.add(Bucket.create(updatedUser.id))
                    }
                }

                delay(50)
            }
        }
    }
}

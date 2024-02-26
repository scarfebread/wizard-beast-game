package online

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import model.User

class BackendClient(private val client: HttpClient) {
    fun getUsers(user: String): List<User> {
        return runBlocking {
            client.get("$BASE_URL/users/$user").body<List<User>>()
        }
    }

    fun updateUser(user: User) {
        runBlocking {
            client.put("$BASE_URL/users/") {
                setBody(user)
                contentType(ContentType.Application.Json)
            }
        }
    }

    fun deleteUser(user: String) {
        runBlocking {
            client.delete("$BASE_URL/users/$user")
        }
    }

    companion object {
        const val BASE_URL = "http://localhost:8095"
    }
}

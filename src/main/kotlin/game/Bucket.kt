package game

import com.badlogic.gdx.math.Rectangle

class Bucket(val id: String) : Rectangle() {
    companion object {
        const val X = (800 / 2 - 64 / 2).toFloat()
        const val Y = 20f
        const val WIDTH = 64f
        const val HEIGHT = 64f

        fun create(id: String): Bucket {
            return Bucket(id).apply {
                x = X
                y = Y
                width = WIDTH
                height = HEIGHT
            }
        }
    }
}

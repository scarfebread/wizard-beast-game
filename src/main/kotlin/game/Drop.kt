package game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.User
import online.BackendClient

class Drop(private val client: BackendClient, private val playerId: String, private val buckets: List<Rectangle>) : ApplicationAdapter() {
    private var dropImage: Texture? = null
    private lateinit var bucketImage: Texture
    private var dropSound: Sound? = null
    private var rainMusic: Music? = null
    private lateinit var batch: SpriteBatch
    private var camera: OrthographicCamera? = null
    private lateinit var bucket: Rectangle
    private var raindrops: Array<Rectangle>? = null
    private var lastDropTime: Long = 0

    override fun create() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = Texture(Gdx.files.internal("src/main/resources/assets/drop.png"))
        bucketImage = Texture(Gdx.files.internal("src/main/resources/assets/bucket.png"))

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("src/main/resources/assets/drop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("src/main/resources/assets/rain.mp3")).also {
            // start the playback of the background music immediately
            it.isLooping = true
            it.play()
        }

        // create the camera and the SpriteBatch
        camera = OrthographicCamera()
        camera!!.setToOrtho(false, 800f, 480f)
        batch = SpriteBatch()

        // create a Rectangle to logically represent the bucket
        bucket = Rectangle()
        bucket.x = (800 / 2 - 64 / 2).toFloat() // center the bucket horizontally
        bucket.y = 20f // bottom left corner of the bucket is 20 pixels above the bottom screen edge
        bucket.width = 64f
        bucket.height = 64f

        // create the raindrops array and spawn the first raindrop
        raindrops = Array()
        spawnRaindrop()
    }

    private fun spawnRaindrop() {
        val raindrop = Rectangle()
        raindrop.x = MathUtils.random(0, 800 - 64).toFloat()
        raindrop.y = 480f
        raindrop.width = 64f
        raindrop.height = 64f
        raindrops!!.add(raindrop)
        lastDropTime = TimeUtils.nanoTime()
    }

    override fun render() = runBlocking {
        val allBuckets = buckets.plus(bucket)

        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        // tell the camera to update its matrices.
        camera!!.update()

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.projectionMatrix = camera!!.combined

        // begin a new batch and draw the bucket and
        // all drops
        batch.begin()
        allBuckets.forEach {
            batch.draw(bucketImage, it.x, it.y)
        }
        for (raindrop in raindrops!!) {
            batch.draw(dropImage, raindrop.x, raindrop.y)
        }
        batch.end()

        // process user input
        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos[Gdx.input.x.toFloat(), Gdx.input.y.toFloat()] = 0f
            camera!!.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= 200 * Gdx.graphics.deltaTime
            launch {
                client.updateUser(
                    User(
                        id = playerId,
                        position = bucket.x
                    )
                )
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 200 * Gdx.graphics.deltaTime
            launch {
                client.updateUser(
                    User(
                        id = playerId,
                        position = bucket.x
                    )
                )
            }
        }

        // make sure the bucket stays within the screen bounds
        allBuckets.forEach {
            if (it.x < 0) it.x = 0f
            if (it.x > 800 - 64) it.x = (800 - 64).toFloat()
        }

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop()

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the latter case we play back
        // a sound effect as well.
        val iter: MutableIterator<Rectangle> = raindrops!!.iterator()
        while (iter.hasNext()) {
            val raindrop = iter.next()
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0) iter.remove()
            allBuckets.forEach {
                if (raindrop.overlaps(it)) {
                    dropSound!!.play()
                    iter.remove()
                }
            }
        }
    }

    override fun dispose() {
        dropImage!!.dispose()
        bucketImage.dispose()
        dropSound!!.dispose()
        rainMusic!!.dispose()
        batch.dispose()

        client.deleteUser(playerId)
    }
}

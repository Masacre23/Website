import utils.game.GameObject
import utils.game.Rectangle
import utils.common.Vector2
import utils.html.fillStyle
import kotlin.math.floor

class Player(imgSrc: String): GameObject(imgSrc){
    private val spriteArea = Vector2()
    private var elapsedTime = 0.0

    override fun draw(dt: Double) {
        // Elapsed extensions.time
        elapsedTime += dt / 200.0

        if (elapsedTime > 3600) {
            elapsedTime -= 3600
        }

        ctx.fillStyle = "#0f0"

        //player.fill(ctx);
        if(!Game.pause) {
            val currentArea = (floor(floor(elapsedTime * 1)) % 4 * 32).toInt()
            when {
                Input.LEFT.isPressed() || Input.A.isPressed() -> {
                    spriteArea.x = currentArea.toDouble()
                    spriteArea.y = 32.0
                }
                Input.RIGHT.isPressed() || Input.D.isPressed() -> {
                    spriteArea.x = currentArea.toDouble()
                    spriteArea.y = 64.0
                }
                Input.UP.isPressed() || Input.W.isPressed() -> {
                    spriteArea.x = currentArea.toDouble()
                    spriteArea.y = 96.0
                }
                Input.DOWN.isPressed() || Input.S.isPressed() -> {
                    spriteArea.x = currentArea.toDouble()
                    spriteArea.y = 0.0
                }
            }
        }
        //if (doorColliding.getOrNull(i) != true || !pause)
        rect.drawImageArea(ctx, view, spriteArea.x, spriteArea.y, 32, 32)
    }

    fun move(dt: Double, camPos: Vector2, wall: List<Rectangle>, scale: Vector2, background: GameObject){
        val speed = 0.0001 * dt
        val distance = canvas.height / scale.y * speed
        if (Input.UP.isPressed() || Input.W.isPressed()) {
            val futureRect = Rectangle(rect.x, rect.y - distance, rect.width, rect.height)
            if (wall.none { it.intersects(futureRect) }) {
                if(camPos.y >= 0 || y > canvas.height/2){
                    y -= distance
                }else {
                    camPos.y += distance
                }
            }
        }
        if (Input.RIGHT.isPressed() || Input.D.isPressed()) {
            val futureRect = Rectangle(rect.x + distance, rect.y, rect.width, rect.height)
            if (wall.none { it.intersects(futureRect) }) {
                if(x < canvas.width / 2 || camPos.x < -background.width + canvas.width) {
                    x += distance
                }else {
                    camPos.x -= distance
                }
            }
        }
        if (Input.DOWN.isPressed() || Input.S.isPressed()) {
            val futureRect = Rectangle(rect.x, rect.y + distance, rect.width, rect.height)
            if (wall.none { it.intersects(futureRect) }) {
                if(y < canvas.height / 2 || camPos.y < -background.height + canvas.height){
                    y += distance
                }else {
                    camPos.y -= distance
                }
            }
        }
        if (Input.LEFT.isPressed() || Input.A.isPressed()) {
            val futureRect = Rectangle(rect.x - distance, rect.y, rect.width, rect.height)
            if (wall.none { it.intersects(futureRect) }) {
                if(camPos.x >= 0 || x > canvas.width/2) {
                    x -= distance
                }else {
                    camPos.x += distance
                }
            }
        }

        // Out Screen
        if (x > canvas.width - width) {
            x = canvas.width - width
        }
        if (y > canvas.height - height) {
            y = canvas.height - height
        }
        if (x < 0) {
            x = 0.0
        }
        if (y < 0) {
            y = 0.0
        }
    }
}
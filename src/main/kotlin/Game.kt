
import utils.html.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import utils.common.*
import utils.game.GameObject
import utils.game.Rectangle
import kotlin.js.Date

class Game(val htmlManager: HtmlManager) {
    val canvas = document.getElementById("canvas") as HTMLCanvasElement
    val ctx = canvas.getContext("2d")!!
    enum class Zoom { IN, OUT }
    companion object {
        var pause = false
        val camGlobalPos = Vector2()

        val onZoom = Signal<Zoom>()
    }
    val scale = Vector2(0.4, 0.4)
    var lastUpdate = Date.now()
    var player = Player("img/avatar.png")

    var background = GameObject("img/map.png")

    val contracts = mutableListOf(
        Contract(Vector2(14,9)),
        Contract(Vector2(3,25)),
        Contract(Vector2(24,23)),
        Contract(Vector2(51,20))
    )

    val walls = mutableListOf<Rectangle>()
    val doors = mutableListOf<Rectangle>()
    var doorColliding = mutableListOf<Boolean>()
    var win = false
    var fullscreen = false

    val sizeY:Double
        get() = canvas.height / scale.y/29
    val sizeX: Double
        get() = canvas.width / scale.x /60

    init {
        window.addEventListener("resize", {
            val camx = camGlobalPos.x / sizeX
            val camy = camGlobalPos.y / sizeY

            val playerx = player.x / sizeX
            val playery = player.y / sizeY

            resize()
            camGlobalPos.x = camx * sizeX
            camGlobalPos.y = camy * sizeY
            player.x = playerx * sizeX
            player.y = playery * sizeY
        }, false)


        var previousZoom = Date.now()
        onZoom.add{ zoom:Zoom ->
            if(Date.now() - previousZoom  >= 250) {
                val previousScale = Vector2(scale.x, scale.y)
                if (zoom == Zoom.OUT) {
                    if (scale.x < 0.6) {
                        scale.x += 0.05
                        scale.y += 0.05
                    }
                } else {
                    if (scale.x > 0.4) {
                        scale.x -= 0.05
                        scale.y -= 0.05
                    }
                }
                previousZoom = Date.now()

                val camx = camGlobalPos.x / (canvas.width / previousScale.x / 60)
                val camy = camGlobalPos.y / (canvas.height / previousScale.y / 29)

                val playerx = player.x / (canvas.width / previousScale.x / 60)
                val playery = player.y / (canvas.height / previousScale.y / 29)

                resize()
                player.x = playerx * sizeX
                player.y = playery * sizeY
                camGlobalPos.x = camx * sizeX
                camGlobalPos.y = camy * sizeY
            }
        }

        window.addEventListener("load", { init() }, false)// JavaScript Document
    }

    fun init(){
        //Initialize variables

        7.times { doorColliding.add(false) }
        resize()
        player.x = canvas.width/ 2.0//10.0
        player.y = canvas.height/ 2.0//15.0

        //Start game
        run()
        document.getElementById("loader").hide()
    }

    fun reset(){
        /*player.x = random(buffer.width / 10 - 1) * 10;
        player.y = random(buffer.height / 10 - 1) * 10;
        money.x = random(buffer.width / 10 - 1) * 10;
        money.y = random(buffer.height / 10 - 1) * 10;*/
        win = false;
    }

    fun run(){
        window.requestAnimationFrame { run() };
        val now = Date.now()
        val dt = now - lastUpdate;
        lastUpdate = now

        // Pause/Unpause
        /*if (GameInputs.lastPress === Input.ENTER) {
            pause = !pause;
            GameInputs.lastPress = Input.NONE;
        }*/

        if(!pause) {
            update(dt)
        }

        //if(gameover) reset()

        resize()
        draw(dt)
    }

    fun update(dt: Double){
        //resize();
        if (win) {
            reset()
        }

        // Change Direction
        player.move(dt, camGlobalPos, walls, scale, background)

        // Contract Intersects

        contracts.forEach {
            if (player.intersects(it)) {
                contracts.remove(it)
                if (contracts.isEmpty()) {
                    win = true
                    pause = true
                    js("""confetti.start(30000);""")
                    launch {
                        sleep(30.seconds)
                        win = false
                        pause = false
                    }
                }
            }
        }

        // Wall Intersects
       /* while (walls.any { contract.intersects(it) }){
            contractPos = utils.common.Vector2(Random.nextDouble() * 60, Random.nextDouble() * 29)
            contract.x = contractPos.x * sizeX + camGlobalPos.x
            contract.y = contractPos.y * sizeY + camGlobalPos.y
        }*/

        //Door intersect
        doors.forEachIndexed { index, door ->
            if (player.intersects(door)) {
                if (!doorColliding[index]) {
                    when(index){
                        0->htmlManager.education.show()
                        1->htmlManager.experience.show()
                        2->htmlManager.skills.show()
                        3->htmlManager.work.show()
                        4->htmlManager.gameJams.show()
                        5->htmlManager.personal.show()
                        6->htmlManager.about.show()
                    }

                    pause = true
                    doorColliding[index] = true
                }
            } else doorColliding[index] = false
        }
    }

    fun draw(dt: Double){
        //Clean
        ctx.fillStyle = "#030"
        //ctx.fillRect(0, 0, buffer.width, buffer.height);
        ctx.fillRect(0, 0, canvas.width, canvas.height)

        //Draw map
        //ctx.drawImage(background, 0, 0, buffer.width, buffer.height);
        background.apply {
            x = camGlobalPos.x
            y = camGlobalPos.y
            width = canvas.width / scale.x
            height = canvas.height / scale.y
            draw(dt)
        }

        //Draw Name
        ctx.font = (canvas.width / scale.x / 70).toString() + "px pixel"
        //ctx.fillStyle = 'rgba(75,128,75,0.5)';
        //ctx.fillStyle = 'rgba(75,128,75,0.5)';
        ctx.fillStyle = "rgba(255,255,255,0.6)"
        ctx.fillText("Adrian", canvas.width / scale.x / 50 + camGlobalPos.x, canvas.height /scale.y / 10 + camGlobalPos.y)
        ctx.fillText("Guevara Alonso", canvas.width / scale.x / 50 + camGlobalPos.x, canvas.height / scale.y / 7 + camGlobalPos.y)

        ctx.font = (canvas.width / scale.x / 100).toString() + "px pixel"
        ctx.fillStyle = "rgba(125, 93, 59, 0.6)"

        if(htmlManager.enableGamesBackground)
            ctx.fillText("Game Developer", canvas.width / scale.x / 50 + camGlobalPos.x, canvas.height / scale.y / 5.6 + camGlobalPos.y)
        else
            ctx.fillText("Game & Web Developer", canvas.width / scale.x / 50 + camGlobalPos.x, canvas.height / scale.y / 5.6 + camGlobalPos.y)

        ctx.font = (0.7 * sizeX).toString() + "px calibri"
        ctx.fillStyle = "rgba(0, 0, 0, 0.6)"
        ctx.fillText("adrianguevaralonso@gmail.com", 1.25 * sizeX + camGlobalPos.x, 6.9 * sizeY + camGlobalPos.y)

        //Draw Houses texts
        ctx.font = (0.55 * sizeX).toString() + "px pixel"
        ctx.fillStyle = "rgba(255,255,255,0.6)"
        ctx.fillText("Education",  1.25 * sizeX + camGlobalPos.x, 19.5 * sizeY + camGlobalPos.y)
        ctx.fillText("Experience",  8.1 * sizeX + camGlobalPos.x, 23.5 * sizeY + camGlobalPos.y)
        ctx.fillStyle = "rgba(255,255,255,0.8)"
        ctx.fillText("Skills",  21 * sizeX + camGlobalPos.x, 13.5 * sizeY + camGlobalPos.y)
        ctx.fillText("Work",  24.3 * sizeX + camGlobalPos.x, 11.5 * sizeY + camGlobalPos.y)
        ctx.fillText("Jams",  32.2 * sizeX + camGlobalPos.x, 11.5 * sizeY + camGlobalPos.y)
        ctx.fillText("Personal",  34.4 * sizeX + camGlobalPos.x, 13.5 * sizeY + camGlobalPos.y)
        ctx.fillStyle = "rgba(255,255,255,0.7)"
        ctx.fillText("About",  51.2 * sizeX + camGlobalPos.x, 9.5 * sizeY + camGlobalPos.y)

        //Draw player
        if(!doorColliding.contains(true))
            player.draw(dt)

        //Draw contract
        contracts.forEach { it.draw() }

        // Draw walls
        //ctx.fillStyle = "rgba(128, 128, 128, 0.5)"
        //walls.forEach { it.fill(ctx) }
/*
        //Draw doors
        ctx.fillStyle = '#f00';
        for (var i = 0, l = door.length; i < l; i += 1) {
            door[i].fill(ctx);
        }
        */
        //Draw score
        /*ctx.fillStyle = '#fff';
        ctx.fillText('Score: ' + score, 0, 10);
        ctx.fillText('Canvas: ' + canvas.width, 0, 30);
        ctx.fillText('Pos: ' + player.x, 0, 40);*/

        if(pause) {
            ctx.textAlign = "center"
            ctx.fillStyle = "#0000008f"
            ctx.fillRect(0, 0, canvas.width, canvas.height)
            ctx.fillStyle = "#FFF"

            console.log(win)
            if (win) {
                ctx.fillText("ADRIAN HAS BEEN HIRED", canvas.width / 2, canvas.height / 2)
                ctx.fillText("THANKS FOR YOUR HELP", canvas.width / 2, canvas.height / 2 + canvas.height/18)
            }
            else ctx.fillText("PAUSE", canvas.width / 2, canvas.height / 2)

            ctx.textAlign = "left"
        }
    }

    fun resize() {
        canvas.width = window.innerWidth
        canvas.height = (window.innerWidth / 1.5).toInt() //Esto es para que el texto no se deforme

        player.width = canvas.width / scale.x /62.0
        player.height = canvas.height / scale.y /29.0

        contracts.forEach {
            it.x = it.position.x * sizeX + camGlobalPos.x
            it.y = it.position.y * sizeY + camGlobalPos.y
            it.width = 0.75 * canvas.width / scale.x /60
            it.height = 0.75 * canvas.height / scale.y /29
        }

        //Doors
        doors.clear()
        doors.add(Rectangle(3 * sizeX + camGlobalPos.x, 18 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))
        doors.add(Rectangle(10 * sizeX + camGlobalPos.x, 22 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))
        doors.add(Rectangle(22 * sizeX + camGlobalPos.x, 12 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))
        doors.add(Rectangle(25 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))
        doors.add(Rectangle(33 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))
        doors.add(Rectangle(36 * sizeX + camGlobalPos.x, 12 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))
        doors.add(Rectangle(52 * sizeX + camGlobalPos.x, 8 * sizeY + camGlobalPos.y, 1 * sizeX, 0.35 * sizeY))

        walls.clear()

        val offsetX = 0.6 * sizeX
        val offsetY = - 0.75 * sizeY

        //Primer recuadro
        walls.add(Rectangle(1 * sizeX + camGlobalPos.x, 9 * sizeY + camGlobalPos.y, 1 * sizeX, sizeY + offsetY))
        walls.add(Rectangle(12 * sizeX + camGlobalPos.x, 9 * sizeY + camGlobalPos.y, 1 * sizeX, sizeY + offsetY))
        walls.add(Rectangle(13 * sizeX + camGlobalPos.x, 11 * sizeY + camGlobalPos.y, 1 * sizeX, sizeY + offsetY))

        //Segundo recuadro
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 0 * sizeY + camGlobalPos.y, 12 * sizeX - offsetX, sizeY + offsetY))
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 0 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 5 * sizeY + offsetY))
        walls.add(Rectangle(26 * sizeX + camGlobalPos.x + offsetX/2, 0 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 5 * sizeY + offsetY))
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 6 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 7 * sizeY + offsetY))
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 12 * sizeY + camGlobalPos.y, 7 * sizeX - offsetX, 1 * sizeY + offsetY))
        walls.add(Rectangle(23 * sizeX + camGlobalPos.x, 12 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(21 * sizeX + camGlobalPos.x, 4 * sizeY + camGlobalPos.y, 6 * sizeX, 6 * sizeY + offsetY))
        walls.add(Rectangle(21 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(24 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(26 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(19 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 2 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(16 * sizeX + camGlobalPos.x, 8 * sizeY + camGlobalPos.y, 2 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(17 * sizeX + camGlobalPos.x, 1 * sizeY + camGlobalPos.y, 2 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(23 * sizeX + camGlobalPos.x, 1 * sizeY + camGlobalPos.y, 2 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(20 * sizeX + camGlobalPos.x, 8 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        //Tercer recuadro
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x + offsetX/2, 0 * sizeY + camGlobalPos.y, 6 * sizeX - offsetX, 1 * sizeY + offsetY))
        walls.add(Rectangle(39 * sizeX + camGlobalPos.x + offsetX/2, 0 * sizeY + camGlobalPos.y, 6 * sizeX - offsetX, 1 * sizeY + offsetY))
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x + offsetX/2, 1 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 5 * sizeY + offsetY))
        walls.add(Rectangle(44 * sizeX + camGlobalPos.x + offsetX/2, 1 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 4 * sizeY + offsetY))
        walls.add(Rectangle(44 * sizeX + camGlobalPos.x + offsetX/2, 6 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 7 * sizeY + offsetY))
        walls.add(Rectangle(38 * sizeX + camGlobalPos.x, 12 * sizeY + camGlobalPos.y, 6 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x, 5 * sizeY + camGlobalPos.y, 6 * sizeX, 5 * sizeY + offsetY))
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(34 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(35 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(35 * sizeX + camGlobalPos.x, 10 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(35 * sizeX + camGlobalPos.x, 12 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(37 * sizeX + camGlobalPos.x, 12 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        walls.add(Rectangle(39 * sizeX + camGlobalPos.x, 11 * sizeY + camGlobalPos.y, 3 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(40 * sizeX + camGlobalPos.x, 9 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(39 * sizeX + camGlobalPos.x, 6 * sizeY + camGlobalPos.y, 1 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(42 * sizeX + camGlobalPos.x, 6 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        //Cuarto recuadro
        walls.add(Rectangle(46 * sizeX + camGlobalPos.x, 0 * sizeY + camGlobalPos.y, 5 * sizeX, 13 * sizeY + offsetY))
        walls.add(Rectangle(54 * sizeX + camGlobalPos.x, 0 * sizeY + camGlobalPos.y, 6 * sizeX, 13 * sizeY + offsetY))
        walls.add(Rectangle(51 * sizeX + camGlobalPos.x, 0 * sizeY + camGlobalPos.y, 3 * sizeX, 8 * sizeY + offsetY))
        walls.add(Rectangle(51 * sizeX + camGlobalPos.x, 8 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(53 * sizeX + camGlobalPos.x, 8 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        //Quinto recuadro
        walls.add(Rectangle(2 * sizeX + camGlobalPos.x, 16 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(2 * sizeX + camGlobalPos.x, 16 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(2 * sizeX + camGlobalPos.x, 18 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(4 * sizeX + camGlobalPos.x, 18 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        walls.add(Rectangle(9 * sizeX + camGlobalPos.x, 20 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(9 * sizeX + camGlobalPos.x, 20 * sizeY + camGlobalPos.y, 3 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(9 * sizeX + camGlobalPos.x, 22 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(11 * sizeX + camGlobalPos.x, 22 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        walls.add(Rectangle(5 * sizeX + camGlobalPos.x, 20 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(3 * sizeX + camGlobalPos.x, 23 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(4 * sizeX + camGlobalPos.x, 28 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        //wall.add(utils.game.Rectangle(10 * sizeX + camPos.x, 25 * sizeY + camPos.y, 1 * sizeX + camPos.x, 1 * sizeY + camPos.y + offsetY))

        //Sexto recuadro
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 16 * sizeY + camGlobalPos.y, 8 * sizeX - offsetX, 5 * sizeY + offsetY))
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 21 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 2 * sizeY + offsetY))
        walls.add(Rectangle(15 * sizeX + camGlobalPos.x + offsetX/2, 24 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 5 * sizeY))
        walls.add(Rectangle(16 * sizeX + camGlobalPos.x, 28 * sizeY + camGlobalPos.y, 10 * sizeX, 1 * sizeY ))
        walls.add(Rectangle(26 * sizeX + camGlobalPos.x + offsetX/2, 24 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 5 * sizeY))
        walls.add(Rectangle(26 * sizeX + camGlobalPos.x + offsetX/2, 18 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 5 * sizeY + offsetY))
        walls.add(Rectangle(23 * sizeX + camGlobalPos.x, 18 * sizeY + camGlobalPos.y, 3 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(23 * sizeX + camGlobalPos.x, 16 * sizeY + camGlobalPos.y, 1 * sizeX, 2 * sizeY + offsetY))

        walls.add(Rectangle(24 * sizeX + camGlobalPos.x, 19 * sizeY + camGlobalPos.y, 2 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(23 * sizeX + camGlobalPos.x, 21 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(25 * sizeX + camGlobalPos.x, 24 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(20 * sizeX + camGlobalPos.x, 24 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(23 * sizeX + camGlobalPos.x, 27 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        //Septimo recuadro
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x + offsetX/2, 24 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 6 * sizeY + offsetY))
        walls.add(Rectangle(33 * sizeX + camGlobalPos.x + offsetX/2, 28 * sizeY + camGlobalPos.y, 5 * sizeX - offsetX, 1 * sizeY))
        walls.add(Rectangle(39 * sizeX + camGlobalPos.x + offsetX/3, 24 * sizeY + camGlobalPos.y, 6 * sizeX - offsetX, 6 * sizeY + offsetY))
        walls.add(Rectangle(39 * sizeX + camGlobalPos.x + offsetX/2, 16 * sizeY + camGlobalPos.y, 6 * sizeX - offsetX, 4 * sizeY + offsetY))
        walls.add(Rectangle(44 * sizeX + camGlobalPos.x + offsetX/2, 19 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 4 * sizeY + offsetY))
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x + offsetX/2, 18 * sizeY + camGlobalPos.y, 4 * sizeX - offsetX, 4 * sizeY + offsetY))
        walls.add(Rectangle(32 * sizeX + camGlobalPos.x + offsetX/2, 22 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 1 * sizeY + offsetY))
        walls.add(Rectangle(35 * sizeX + camGlobalPos.x + offsetX/2, 16 * sizeY + camGlobalPos.y, 1 * sizeX - offsetX, 2 * sizeY + offsetY))
        walls.add(Rectangle(36 * sizeX + camGlobalPos.x + offsetX/2, 16 * sizeY + camGlobalPos.y, 2 * sizeX - offsetX, 1 * sizeY + offsetY))

        walls.add(Rectangle(37 * sizeX + camGlobalPos.x, 17 * sizeY + camGlobalPos.y, 1 * sizeX, 1 * sizeY + offsetY))

        //Octavo recuadro
        walls.add(Rectangle(46 * sizeX + camGlobalPos.x, 16 * sizeY + camGlobalPos.y, 6 * sizeX, 3 * sizeY + offsetY))
        walls.add(Rectangle(46 * sizeX + camGlobalPos.x, 20 * sizeY + camGlobalPos.y, 5 * sizeX, 3 * sizeY + offsetY))
        walls.add(Rectangle(55 * sizeX + camGlobalPos.x, 16 * sizeY + camGlobalPos.y, 6 * sizeX, 4 * sizeY + offsetY))
        walls.add(Rectangle(53 * sizeX + camGlobalPos.x, 22 * sizeY + camGlobalPos.y, 7 * sizeX, 4 * sizeY + offsetY))

        walls.add(Rectangle(54.15 * sizeX + camGlobalPos.x, 18 * sizeY + camGlobalPos.y, 0.9 * sizeX, 2 * sizeY + offsetY))
        walls.add(Rectangle(52.05 * sizeX + camGlobalPos.x, 20 * sizeY + camGlobalPos.y, 0.9 * sizeX, 1 * sizeY + offsetY))
        walls.add(Rectangle(49.05 * sizeX + camGlobalPos.x, 25 * sizeY + camGlobalPos.y, 0.9 * sizeX, 1 * sizeY + offsetY))

        //Rio
        walls.add(Rectangle(28.25 * sizeX + camGlobalPos.x, 0 * sizeY + camGlobalPos.y, 2.5 * sizeX, 5 * sizeY + offsetY))
        walls.add(Rectangle(28.25 * sizeX + camGlobalPos.x, 6 * sizeY + camGlobalPos.y, 2.5 * sizeX, 7 * sizeY + offsetY))
        walls.add(Rectangle(28.25 * sizeX + camGlobalPos.x, 16 * sizeY + camGlobalPos.y, 2.5 * sizeX, 7 * sizeY + offsetY))
        walls.add(Rectangle(28.25 * sizeX + camGlobalPos.x, 24 * sizeY + camGlobalPos.y, 2.5 * sizeX, 7 * sizeY + offsetY))
    }
}
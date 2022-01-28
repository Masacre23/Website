package utils.game

import utils.html.drawImage
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image

open class GameObject(imgSrc: String, protected val rect: Rectangle = Rectangle()){
    protected val canvas = document.getElementById("canvas") as HTMLCanvasElement
    protected val ctx = canvas.getContext("2d")!!
    protected val view = Image()
    var x: Double
        get() = rect.x
        set(value) { rect.x = value }

    var y: Double
        get() = rect.y
        set(value) { rect.y = value }

    var width: Double
        get() = rect.width
        set(value) { rect.width = value }

    var height: Double
        get() = rect.height
        set(value) { rect.height = value }

    init {
        view.src = imgSrc
    }

    open fun draw(dt: Double = 0.0){
        ctx.drawImage(view, x, y, width, height)
    }

    fun intersects(rect: Rectangle) = this.rect.intersects(rect)
    fun intersects(go: GameObject) = this.rect.intersects(go.rect)

}
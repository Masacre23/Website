package utils.game

import org.w3c.dom.Image
import org.w3c.dom.RenderingContext

class Rectangle(x: Number = 0, y: Number = 0, width: Number = 0, height: Number = 0) {
    var x: Double = x.toDouble()
    var y: Double = y.toDouble()
    var width: Double = width.toDouble()
    var height: Double = height.toDouble()

    fun intersects(rect: Rectangle): Boolean{
        return intersectsFromTop(rect) && intersectsFromLeft(rect) && intersectsFromBottom(rect) && intersectsFromRight(rect)
    }

    private fun intersectsFromTop(rect: Rectangle) = y < rect.y + rect.height
    private fun intersectsFromBottom(rect: Rectangle) = y + height > rect.y
    private fun intersectsFromRight(rect: Rectangle) = x + width > rect.x
    private fun intersectsFromLeft(rect: Rectangle) = x < rect.x + rect.width

    fun fill(ctx: RenderingContext){
        ctx.asDynamic().fillRect(this.x, this.y, this.width, this.height)
    }

    fun drawImage(ctx: RenderingContext, img: Image){
        ctx.asDynamic().drawImage(img, this.x, this.y, this.width, this.height)
    }

    fun drawImageArea(ctx: RenderingContext, img: Image, sx: Number, sy: Number, sw: Number, sh: Number){
        ctx.asDynamic().drawImage(img,sx,sy,sw,sh,this.x,this.y,this.width,this.height)
    }
}
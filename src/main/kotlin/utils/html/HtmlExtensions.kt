package utils.html

import utils.common.Promise
import utils.common.async
import utils.common.launch
import utils.common.sleep
import utils.common.seconds
import kotlinx.browser.document
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import org.w3c.dom.*
import org.w3c.dom.events.Event

fun head(block: TagConsumer<Any>.()->Unit){
    document.head!!.append {
        block()
    }
}

fun body(block: TagConsumer<Any>.()->Unit){
    document.body!!.append {
        block()
    }
}

fun Document.show(id: String, flex: Boolean = false){
    getElementById(id).show(flex)
}

fun Document.hide(id: String){
    getElementById(id).hide()
}

fun Element?.show(flex: Boolean = false){
    this.asDynamic().style.display = if(flex) "flex" else "block"
}

fun Element?.hide(){
    this.asDynamic().style.display = "none"
}

fun Element.isVisible() = this.asDynamic().style.display != "none" && opacity != 0f
fun Element.isNotVisible() = !isVisible()

var Element.opacity: Float
    set(value){ this.asDynamic().style.opacity = value.toString() }
    get(){
        return if(this.asDynamic().style.opacity != undefined && this.asDynamic().style.opacity != "")
            (this.asDynamic().style.opacity as String).toFloat()
        else 1f
    }

var Element.backgroundColor: String
    set(value) { this.asDynamic().style.backgroundColor = value }
    get() = this.asDynamic().style.backgroundColor

var Element.backgroundImage: String
    set(value) { this.asDynamic().style.backgroundImage = value }
    get() = this.asDynamic().style.backgroundImage


fun showModal(id: String): Promise<Unit> {
    return async {
        document.getElementById(id)!!.apply {
            this.show()
            launch {
                sleep(0.seconds)
                this.addClass("show")
            }
        }
        Unit
    }
}

fun hideModal(id: String): Promise<Unit> {
    return async {
        document.getElementById(id)!!.apply {
            this.removeClass("show")

            launch {
                sleep(0.2.seconds)
                this.hide()
            }
        }
    }
}

var RenderingContext.fillStyle: String
    get() = this.asDynamic().fillStyle as String
    set(value){ this.asDynamic().fillStyle = value }

var RenderingContext.textAlign: String
    get() = this.asDynamic().textAlign as String
    set(value){ this.asDynamic().textAlign = value }

var RenderingContext.font: String
    get() = this.asDynamic().font as String
    set(value){ this.asDynamic().font = value }

fun RenderingContext.drawImage(image: Image, x: Number, y: Number, width: Number, height: Number){
    this.asDynamic().drawImage(image, x, y, width, height)
}

fun RenderingContext.fillText(text: String, x: Number, y: Number){
    this.asDynamic().fillText(text, x, y)
}

fun RenderingContext.fillRect(x: Int, y: Number, width: Number, height: Number){
    this.asDynamic().fillRect(x, y, width, height)
}

val Event.offsetX: Int
    get() {
        var ret = this.asDynamic().offsetX
        if(ret == undefined) {
            ret =  this.asDynamic().touches[0].pageX - this.asDynamic().touches[0].target.offsetLeft - this.asDynamic().touches[0].target.offsetWidth;
        }
        return ret
    }

val Event.offsetY: Int
    get() {
        var ret = this.asDynamic().offsetY
        if(ret == undefined) {
            ret = this.asDynamic().touches[0].pageY - this.asDynamic().touches[0].target.offsetTop - this.asDynamic().touches[0].target.offsetParent.getBoundingClientRect().y
        }
        return ret
    }

@HtmlTagMarker
fun TagConsumer<Any>.imgZoom(src: String, classes: String = ""){
    img(classes = "embed-responsive-item $classes") {
        id = src
        this.src = src

        onClickFunction = {
            document.getElementById(src)!!.removeClass("zoom")
        }
    }

    div("zoom-icon") {
        img {
            this.src = "img/zoom-in.svg"
        }

        onClickFunction = {
            document.getElementById(src)!!.addClass("zoom")
        }
    }
}

fun pauseYoutubeVideos(){
    document.getElementsByTagName("iframe").asList().map { (it as HTMLIFrameElement) }.filter { it.src.contains("youtube") }.forEach {
        it.contentWindow!!.postMessage("{\"event\":\"command\",\"func\":\"pauseVideo\",\"args\":\"\"}", "*") // src = it.src
    }
}

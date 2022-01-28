package utils.html

import utils.common.Signal
import utils.common.dispatch
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.js.onClickFunction

class Popup(id: String, classes: String, fade: Boolean, closable: Boolean, stretch: Boolean, container: TagConsumer<Any>, content: (FlowContent)->Unit) {
    companion object{
        private var counter = 0
    }

    val id = if(id.isNotEmpty()) id else "popup_$counter"
    var showing = false
    val onHide = Signal<Unit>()

    init {
        counter++
        container.apply {
            div("modal ${if (fade) "fade" else ""} popup") {
                this.id = this@Popup.id
                tabIndex = "-1"
                role = "dialog"

                div("modal-dialog modal-dialog-centered $classes") {
                    role = "document"
                    if(stretch)
                        style = "height: 0%;"

                    div("modal-content") {
                        if(stretch)
                            style = "min-height: 100%;"

                        content(this)
                        if(closable){
                            span("close") {
                                style = "position: absolute; top: 6px; right: 8px; cursor: pointer;"
                                +"X"
                                onClickFunction = { hide() }
                            }
                        }
                    }
                }
            }
        }
        document.getElementById(id).hide()
    }

    fun show() {
        showing = true
        showModal(id).then { showing = false }
    }
    fun hide(){
        hideModal(id)
        onHide.dispatch()
        Game.pause = false
    }
    fun isVisible() = document.getElementById(id)!!.isVisible()
    fun isNotVisible() = document.getElementById(id)!!.isNotVisible()
}
@HtmlTagMarker
fun TagConsumer<Any>.popup(classes: String = "", id: String = "", fade: Boolean = true, closable: Boolean = true, stretch: Boolean = false, content: (FlowContent) -> Unit = {}) = Popup(id, classes, fade, closable, stretch, this, content)
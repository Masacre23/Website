package utils.html

import kotlinx.browser.document
import kotlinx.dom.addClass
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Element
import org.w3c.dom.get

class Carousel(id: String, container: TagConsumer<Any>, indicatorsImages: List<String>, stretch: Boolean, content: (FlowContent) -> Unit) {
    companion object{
        private var counter = 0
        private var carousels = mutableListOf<Carousel>()
        fun resetCarousel(){
            carousels.forEach {
                it.view.children.get(0)!!.children.get(it.currectSlide)!!.children.get(0)!!.addClass("active")
            }
        }
    }

    val id = if(id.isNotEmpty()) id else "carousel_$counter"
    var currectSlide = 0
    val view: Element
        get() = document.getElementById(id)!!

    init {
        counter++
        carousels.add(this)
        container.apply {

            div("carousel carousel-light slide") {
                this.id = this@Carousel.id
                attributes["data-bs-ride"] = "carousel"
                attributes["data-bs-interval"] = "false"
                style = "height: 100%;"

                ul("carousel-indicators list-inline") {
                    indicatorsImages.forEachIndexed { index, src ->
                        li("list-inline-item") {
                            a(classes = if (index == 0) "active" else "") {
                                attributes["data-bs-target"] = "#${this@Carousel.id}"
                                attributes["data-bs-slide-to"] = "$index"
                                if(index == 0)
                                    attributes["aria-current"] = "true"
                                attributes["aria-label"] = "Slide ${index + 1}"

                                img(src = src, classes = "img-fluid")

                                onClickFunction = {
                                    currectSlide = index
                                    pauseYoutubeVideos()
                                }
                            }
                        }
                    }
                }

                div("carousel-inner"){
                    content(this)
                }

                button(type = ButtonType.button, classes = "carousel-control-prev"){
                    attributes["data-bs-target"] = "#${this@Carousel.id}"
                    attributes["data-bs-slide"] = "prev"
                    style = "max-height: 100vh;${
                        if(stretch){
                            "margin-top: 40vh; height: 100px;"
                        }else ""
                    }"
                    span("carousel-control-prev-icon"){
                        attributes["aria-hidden"] = "true"
                        style = "width: 60px; height: 60px;max-height: 100vh;"
                    }
                    span("visually-hidden"){ +"Previous" }

                    onClickFunction = {
                        pauseYoutubeVideos()
                    }
                }
                button(type = ButtonType.button, classes = "carousel-control-next"){
                    attributes["data-bs-target"] = "#${this@Carousel.id}"
                    attributes["data-bs-slide"] = "next"
                    style = "max-height: 100vh;${
                        if(stretch){
                            "margin-top: 40vh; height: 100px;"
                        }else ""
                    }"
                    span("carousel-control-next-icon"){
                        attributes["aria-hidden"] = "true"
                        style = "width: 60px; height: 60px;"
                    }
                    span("visually-hidden"){ +"Next" }

                    onClickFunction = {
                        pauseYoutubeVideos()
                    }
                }
            }
        }
    }
}

@HtmlTagMarker
fun TagConsumer<Any>.carousel(id: String = "", indicatorsImages: List<String>, stretch: Boolean = false, content: (FlowContent) -> Unit = {}) = Carousel(id, this, indicatorsImages, stretch, content)

@HtmlTagMarker
fun TagConsumer<Any>.carouselItem(active: Boolean = false, content: (FlowContent) -> Unit = {}){
    div("carousel-item ${if(active) "active" else ""}"){
        content(this)
    }
}
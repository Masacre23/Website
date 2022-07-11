import utils.common.launch
import utils.common.sleep
import utils.html.*
import utils.common.seconds
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.js.*
import utils.common.times
import org.w3c.dom.*
import org.w3c.dom.events.Event

class HtmlManager (){
    lateinit var info: Popup
    lateinit var education: Popup
    lateinit var experience: Popup
    lateinit var skills: Popup
    lateinit var work: Popup
    lateinit var gameJams: Popup
    lateinit var personal: Popup
    lateinit var about: Popup

    private val popups: List<Popup>
        get() = listOf(info, education, experience, skills, work, gameJams, personal, about)

    init {
        head {
            meta(charset="UTF-8")
            title {
                +"Adrián Guevara Alonso"
            }
            meta(name="viewport", content="width=device-width, initial-scale=1.0")
            link(href="main.css", rel="stylesheet", type="text/css")
            link(rel = "apple-touch-icon", href = "img/face.png")
            link(rel = "shortcut icon", href = "img/face.png")
            link(rel="stylesheet", href="https://use.fontawesome.com/releases/v5.0.8/css/all.css")
        }

        body {
            div {
                id = "loader"
                style = "position: absolute; width: 100vw; height: 100vh; background: white; z-index: 999999"
            }
            div("d-flex justify-content-center") {
                nav("navbar navbar-expand-lg navbar-light") {
                    div("container-fluid") {
                        a(/*classes = "navbar-brand",*/ href = "#Info") {
                            // +"adrianguevaralonso.es"
                            img(src = "img/face.png") {
                                style = "height: 30px; margin-right: 15px;"
                            }
                            //+"Adrian Guevara Alonso"
                            onClickFunction = {
                                info.show()
                            }
                        }
                        button(classes = "navbar-toggler", type = ButtonType.button) {
                            attributes["data-bs-toggle"] = "collapse"
                            attributes["data-bs-target"] = "#navbarSupportedContent"
                            attributes["aria-controls"] = "navbarSupportedContent"
                            attributes["aria-expanded"] = "false"
                            attributes["aria-label"] = "Toggle navigation"
                            span("navbar-toggler-icon")

                            onClickFunction = {
                                document.getElementsByTagName("section").asList().first().apply {
                                    if(classList.contains("hide")){ removeClass("hide")
                                    }else addClass("hide")
                                }
                            }
                        }
                        div(classes = "collapse navbar-collapse") {
                            id = "navbarSupportedContent"
                            ul(classes = "navbar-nav me-auto mb-2 mb-lg-0") {
                                li(classes = "nav-item") {
                                    a(classes = "nav-link", href = "#Education") {
                                        +"Education"
                                        onClickFunction = {
                                            education.show()
                                            active()
                                        }
                                    }
                                }
                                li(classes = "nav-item") {
                                    a(classes = "nav-link", href = "#Experience") {
                                        +"Experience"
                                        onClickFunction = {
                                            experience.show()
                                            active()
                                        }
                                    }
                                }
                                li(classes = "nav-item") {
                                    a(classes = "nav-link", href = "#Skills") {
                                        +"Skills"
                                        onClickFunction = {
                                            skills.show()
                                            active()
                                        }
                                    }
                                }
                                li(classes = "nav-item dropdown") {
                                    a(classes = "nav-link dropdown-toggle", href = "#Projects") {
                                        id = "navbarDropdown"
                                        role = "button"
                                        attributes["data-bs-toggle"] = "dropdown"
                                        attributes["aria-expanded"] = "false"
                                        +"Projects"

                                        onClickFunction = {
                                            active()
                                        }
                                    }
                                    ul(classes = "dropdown-menu dropdown-menu-light") {
                                        attributes["aria-labelledby"] = "navbarDropdown"
                                        li {
                                            a(classes = "dropdown-item", href = "#Work") {
                                                +"Work"
                                                onClickFunction = {
                                                    work.show()
                                                    Carousel.resetCarousel()
                                                }
                                            }
                                        }
                                        li {
                                            a(classes = "dropdown-item", href = "#Jams") {
                                                +"Game Jams"
                                                onClickFunction = {
                                                    gameJams.show()
                                                    Carousel.resetCarousel()
                                                }
                                            }
                                        }
                                        /*li {
                                        hr(classes = "dropdown-divider")
                                    }*/
                                        li {
                                            a(classes = "dropdown-item", href = "#Personal") {
                                                +"Personal"
                                                onClickFunction = {
                                                    personal.show()
                                                    Carousel.resetCarousel()
                                                }
                                            }
                                        }
                                    }
                                }
                                li(classes = "nav-item") {
                                    a(classes = "nav-link", href = "#About") {
                                        +"About me"
                                        onClickFunction = {
                                            about.show()
                                            active()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            main("screen-border"){
                div("screen"){
                    div("embed-responsive embed-responsive-16by9") {
                        style = "overflow: visible;"
                        //style = "position: fixed; top: 56px; max-height: calc(100% - 56px)"
                        canvas("embed-responsive-item") {
                            id = "canvas"
                            +"Canvas not supported by your browser ."
                            style = "display: none;"
                        }

                        video("embed-responsive-item"){
                            id = "intro"
                            src = "video/gameboy_intro.mp4"
                        }
                    }
                }
                img(src="img/gameboy_logo.png")
            }

            div("speaker-holder"){
                5.times { div("dot") }
                2.times { div("dot large-dot") }
                2.times { div("dot") }
                2.times { div("dot large-dot") }
                5.times { div("dot") }
            }

            footer(classes ="footer mt-auto py-3 h-100 text-center text-white") {
                div("controls"){
                    /*div("btn-direction"){
                        div("vertical")
                        div("horizontal")
                    }
                    div("btn-AB")*/

                    div("pad-container dpad-container") {
                        fun DIV.dPadClick(id: String, input: Input){
                            { it: Event ->
                                console.log("down")
                                document.getElementsByClassName(id)[0].apply {
                                    GameInputs.apply {
                                        inputs.find { it.keyCode == input.keyCode }?.apply {
                                            if(this != Input.NONE) {
                                                pressing = true
                                                lastPress = this
                                                console.log(this)
                                            }
                                        }
                                    }
                                }
                                Unit
                            }.apply {
                                onTouchStartFunction =  this
                                onMouseDownFunction = this
                            };

                            { it: Event ->
                                console.log("up")
                                document.getElementsByClassName(id)[0].apply {
                                    GameInputs.apply {
                                        inputs.forEach {
                                            if(it != Input.NONE)
                                                it.pressing = false
                                        }
                                    }
                                }
                                Unit
                            }.apply {
                                onTouchEndFunction =  this
                                onMouseUpFunction = this
                            }
                        }

                        div("d-pad"){
                            div("d-pad-up"){ dPadClick("d-pad-up", Input.UP) }
                            div("d-pad-down"){ dPadClick("d-pad-down", Input.DOWN) }
                            div("d-pad-right"){ dPadClick("d-pad-right", Input.RIGHT) }
                            div("d-pad-left"){ dPadClick("d-pad-left", Input.LEFT) }
                        }
                    }

                    div("pad-container buttons-container"){
                        div("b-button") {
                            span("letter") { +"B" };

                            { it: Event ->
                                document.getElementsByClassName("b-button")[0]!!.addClass("hide-before")
                                Game.apply {
                                    onZoom.dispatch(Game.Zoom.OUT)
                                }
                                Unit
                            }.apply {
                                onTouchStartFunction =  this
                                onMouseDownFunction = this
                            };

                            { it: Event ->
                                document.getElementsByClassName("b-button")[0]!!.removeClass("hide-before")
                                Unit
                            }.apply {
                                onTouchEndFunction =  this
                                onMouseUpFunction = this
                            }
                        }

                        div("a-button") {
                            span("letter") { +"A" };

                            { it: Event ->
                                document.getElementsByClassName("a-button")[0]!!.addClass("hide-before")
                                Game.apply {
                                    onZoom.dispatch(Game.Zoom.IN)
                                }
                                Unit
                            }.apply {
                                onTouchStartFunction =  this
                                onMouseDownFunction = this
                            };

                            { it: Event ->
                                document.getElementsByClassName("a-button")[0]!!.removeClass("hide-before")
                                Unit
                            }.apply {
                                onTouchEndFunction =  this
                                onMouseUpFunction = this
                            }
                        }
                    }
                }
                div(classes ="container mt-auto d-flex justify-content-center h-100") {
                    section("d-flex") {
                        style = "padding-left: 13px;"
                        /*div("pad-container") {
                            a(classes = "btn btn-primary btn-circle m-1") {
                                style = "background-color: #3b5998;"
                                href = "#!"
                                role = "button"
                                target="_blank"
                                i(classes = "fab fa-facebook-f")
                            }
                        }
                        div("pad-container") {
                            a(classes = "btn btn-primary btn-circle m-1") {
                                style = "background-color: #55acee;"
                                href = "#!"
                                role = "button"
                                target="_blank"
                                i(classes = "fab fa-twitter")
                            }
                        }*/

                        div("pad-container") {
                            a(classes = "btn btn-primary btn-circle m-1", target = "_blank") {
                                style = "background-color: #dd4b39;"
                                href = "https://www.youtube.com/channel/UC8YP4dNzfQz4_GjDlXYKzJw"
                                role = "button"
                                target="_blank"
                                i(classes = "fab fa-youtube")
                            }
                        }

                        /*div("pad-container") {
                            a(classes = "btn btn-primary btn-circle m-1") {
                                style = "background-color: #ac2bac;"
                                href = "#!"
                                role = "button"
                                target="_blank"
                                i(classes = "fab fa-instagram")
                            }
                        }*/

                        div("pad-container") {
                            a(classes = "btn btn-primary btn-circle m-1", target = "_blank") {
                                style = "background-color: #0082ca;"
                                href = "https://www.linkedin.com/in/adrian-guevara-alonso/"
                                role = "button"
                                target="_blank"
                                i(classes = "fab fa-linkedin-in")
                            }
                        }

                        div("pad-container") {
                            a(classes = "btn btn-primary btn-circle m-1", target = "_blank") {
                                style = "background-color: #333333;"
                                href = "https://github.com/Masacre23"
                                role = "button"
                                target="_blank"
                                i(classes = "fab fa-github")
                            }
                        }
                    }
                }
            }

            info = popup("modal-lg", "Info") {
                h1 { +"Info" }
                ul {
                    li {
                        //+"Este soy yo, usa las flechas o wasd para moverme."
                        +"This is me, to move use \"w,a,s,d\" and the arrows in mobile (resize the window on desktop to see it)."
                    }
                    li {
                        style = "background-image: url(img/house.png);"
                        //+"Entra en las casas para ver información sobre mí y mis proyectos."
                        +"Enter the buildings to see info about me and my projects."
                    }
                    li {
                        style = "background-image: url(img/contrato.png);"
                        //+"Recoge las ofertas de empleo que hay repartidas por el mapa."
                        +"Collect the job offers that are scattered around the map."
                    }
                }
            }

            info.onHide.once {
                (document.getElementById("intro")!! as HTMLVideoElement).play()
                launch {
                    sleep(5.seconds)
                    document.getElementById("intro")!!.hide()
                    document.getElementById("canvas")!!.show()
                }
            }

            education = popup("modal-lg", "Education") {
                h1 { +"Education" }
                ul {
                    li {
                        style = "background-image: url(img/Udemy-Emblem.png);"
                        +"Unreal Engine 4 Mastery: Create Multiplayer Games with C++. (In progress) "
                        a("https://www.udemy.com/course/unrealengine-cpp/"){ +"Link" }
                    }
                    li {
                        style = "background-image: url(img/Udemy-Emblem.png);"
                        +"C++ Course: Basic to Advanced. (2021) "
                        a("https://www.udemy.com/certificate/UC-7d2cb415-e4cc-462d-a0e9-59a24d337612/"){ +"Link" }
                    }
                    li {
                        style = "background-image: url(img/upc.png);"
                        //+"Master de Diseño y Creación de Videojuegos en la Universidad Politécnica de Cataluña.(2016/17)"
                        +"Master of Design and Creation of Videogames at Polytechnic University of Catalonia.(2016/17)"
                    }
                    li {
                        style = "background-image: url(img/uv.png);"
                        //+"Graduado en Ingeniería Multimedia en la Universidad de Valencia.(2012/16)"
                        +"Graduated in Multimedia Engineering at the University of Valencia.(2012/16)\n"
                        +"Multimedia is a computer grade but it has some different subjects like animation, simulation or video games."
                    }
                }
            }

            experience = popup("modal-lg", "Experience") {
                h1 { +"Experience" }
                ul {
                    li {
                        style = "background-image: url(img/playjoy.png);"
                        //+"Playjoy es una app de juegos de mesa para movil, en la cual me he encargado de programar la mayoría de los juegos, "
                        //+ "también me he encargado del backend de la página web, algunas características de la app como el sistema de pagos"
                        //+ "y he enseñado y gestionado a los programadores junniors que se han ido incorporando al proyecto.(2017-Actualidad)"
                        /*+"Playjoy is a collection of different board games like, domino, ludo, UNO,... It's available for desktop, android and IOS."
                        +"I have programmed most of the games, the web page backend, some app features like in-app purchases."
                        +"Furthermore, I had the opportunity of managing a few junior programmers and teach them. (2017-now)"*/
                        strong { +"5 years " }
                        +"of experience in "
                        strong { +"Playjoy" }
                        +" (2017-now).\n\n"
                        strong { +"Playjoy " }
                        +"is a collection of different "
                        strong { +"board games "}
                        +("like, domino, ludo, UNO,... It's a project with multiple parts: HTML5 games, web and app."
                        + " I was the first employee of the company so I started in an early stage and I had to do low programing for the game engine and the web page. \n\n"
                        +"My focus was the games, client side and server side, I programmed a total of 5 games: "
                        +"Chinchon, UNO, Ludo, Durak and Bingo. \n\nFurthermore, I had to teach the next employees and supervise them, this helped me to gain a few managing skills."
                        +"\n\nIn relation to the website I have implemented all the backend, including an admin page.\n\n"
                        +"Finally for the app I only have done one feature but it was a big one, I implemented an in-app purchases plugin and created a system to parse data from google and our database and convert it to IOS json and android csv."
                        )
                    }
                    li{
                        style = "background-image: url(img/ns-icon.png)"
                        strong { +"3 years " }
                        +"of experience in "
                        strong { +"No Spoon Tech Lab" }
                        +(" (2017-2020).\n\n"
                        +"In No Spoon I worked on an app called \"Fallas\". It's the official app of a holiday from Valencia. It's an app based on Pokemon Go.\n\n"
                        +"It's made with Unity 3D, my focus was on the client side.")
                    }
                    li {
                        style = "background-image: url(img/starcruiser.png);"
                        //+"Programador con 5 meses de experiencia de prácticas en Starcruiser Studio. (2016)"
                        strong { +"6 months " }
                        +"of experience in "
                        strong{ +"Starcruiser Studio"}
                        +"(2016).\n\nI was commissioned to do a prototype of the next game in "
                        strong { +"Unity3D"}
                        +", a First Person Shooter where we had to manage our attack and defense units to destroy the enemy base."
                    }
                }
            }

            skills = popup("modal-lg", "Skills") {
                h1 { +"Skills" }
                ul {
                    li {
                        b{ +"Industry Knowledge:" }
                        +"Computer Animation, Game Design, Game Development"
                    }
                    li {
                        b{ +"Technologies:" }
                        +"Kotlin, JavaScript, Java, C#, C++, C, Python, HTML, CSS, SCSS, FreeMarker, Bootstrap, Xml, PHP, OpenGL, MySQL"
                    }
                    li {
                        b{ +"Tools:" }
                        +"Unity, IntelliJ, Visual Studio, Unreal Engine, Blender, Android Studio, GitHub, Jira, Slack"
                    }
                    li {
                        b{ +"Languages:" }
                        +"Spanish, Catalan, English"
                    }
                }
            }

            work = popup("modal-xl", "Work", stretch = false) {
                h1 { +"Work" }
                carousel(indicatorsImages = listOf("img/bingo3.png", "img/durak.png","img/parchis.png","img/chupate2.png", "img/chinchon.png", "img/fallas.jpg"), stretch = false) {
                    carouselItem(true) {
                        div("images-container"){
                            style = "max-width: 70%;"
                            div("embed-responsive embed-responsive-16by9") {
                                imgZoom("img/bingo1.png", "object-position-top")
                            }
                        }
                        div("images-container") {
                            style = "max-width: 70%;"
                            div("embed-responsive embed-responsive-16by9") {
                                imgZoom("img/bingo2.png", "object-position-top")
                            }
                            div("embed-responsive embed-responsive-16by9") {
                                imgZoom("img/bingo3.png", "object-position-top")
                            }
                        }
                        p {
                            b {
                                +"Bingo("
                                a(href = "https://playjoy.com/en/bingo/", target = "_blank") { +"Web" }
                                +"): "
                            }
                            +"The goal of the game is to be the first to complete 1 line, 2 lines and the entire card (bingo) as the balls come out."
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/durak.png", "object-position-bottom")
                        }
                        p {
                            b {
                                +"Durak("
                                a(href = "https://playjoy.com/en/durak/", target = "_blank") { +"Web" }
                                +"): "
                            }
                            +("Durak is a Russian origin card game of attack and defense.\n" +
                                    "\n" +
                                    "It is said that in this game there are no winners, only one loser. The last person with cards in their hand is the loser (the fool or “durak”).\n" +
                                    "\n" +
                                    "The objective is to throw away all your cards before your opponents in different attack and defense rounds until there are no cards in the deck.")
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/parchis.png", "object-position-bottom")
                        }
                        p {
                            b {
                                +"Ludo("
                                a(href = "https://playjoy.com/en/ludo/", target = "_blank") { +"Web" }
                                +"): "
                            }
                            +"The objective of Ludo is to take your 4 pawns from home (or yard) to the finish line going across all the board before the opponents."
                            ul {
                                li { +"This mode is played in pairs (4 players) and with 2 dice." }
                                li { +"The team that takes all its pawns first to the finish line wins." }
                            }
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/chupate2.png", "object-position-bottom")
                        }
                        p {
                            b {
                                +"UNO("
                                a(href = "https://playjoy.com/en/uno/", target = "_blank") { +"Web" }
                                +"): "
                            }
                            +("Uno classic is a card game, that is similar to UNO for 4 players.\n" +
                                    "\n" +
                                    "Like Dominoes, you must try to throw all your cards before your opponents and add up the points of their cards.\n" +
                                    "\n" +
                                    "With these rules, four players play in pairs. The first pair to reach 100 points wins the game.")
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/chinchon.png", "object-position-bottom")
                        }
                        p {
                            b {
                                +"Chinchon("
                                a(href = "https://playjoy.com/en/chinchon/", target = "_blank") { +"Web" }
                                +"): "
                            }
                            +("Chinchon is a card game played with the Spanish deck (also known as Txintxon or Conga) belonging to the same family as Gin Rummy.\n" +
                                    "\n" +
                                    "The objective of the game is to combine your 7 cards before your opponents do it. The possible card combinations are: three of a kind, four of a kind or straight flushes (3 or more consecutive cards of the same suit).\n" +
                                    "\n" +
                                    "It is played individually with the Spanish 40-card deck and the 1 of Coins as a wild card.\n" +
                                    "\n" +
                                    "The rules of this game are similar to: Rummy, Remigio, Canasta")
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/fallas2.jpg", "object-position-center")
                        }
                        p {
                            b {
                                +"Fallas("
                                a(href = "https://play.google.com/store/apps/details?id=com.nospoon.fallasplayandgo", target = "_blank") { +"Store" }
                                +"): "
                            }
                            +("Fallas is an app about our festivities here in Valencia." +
                                    "\n" +
                                    "\n" +
                                    "It's an app similar to Pokemon Go. Users can use their cell phones to overcome geolocated missions that are strategically located in Fallas monuments or key places in the city, get points and compete for gifts through augmented reality games."+
                                    "\n" +
                                    "\n" +
                                    "In addition, the application offers all the tourist information related to the Fallas, such as the location of the monuments, photos, descriptions and featured events, and you can rate the monuments and leave comments."
                                    )
                        }
                    }
                }
            }

            gameJams = popup("modal-xl", "GameJams") {
                h1 { +"Game Jams" }
                carousel(indicatorsImages = listOf("img/office_novice.png", "img/classic_snake_game.png","img/duck_crossing_mini.jpg","img/pymes.png")) {
                    carouselItem(true) {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/2ho-cjviPFc?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Office Novice("
                                a(href = "https://hermesse.itch.io/novice-office", target = "_blank") { +"Web" }
                                +", "
                                a(href = "https://github.com/Masacre23/LD49", target = "_blank") { +"Git" }
                                +"): "
                            }
                            /*+"Eres el novato de la oficina y todos se están aprovechando te ti!"
                            ul {
                                li { +"Coge el teléfono" }
                                li { +"Haz fotocopias" }
                                li { +"Manten felices a los empleados" }
                                li { +"Arregla los ordenadores" }
                                li { +"Haz que tu jefe esté orgulloso" }
                            }*/

                            +"You are the novice of the office, and everyone is taking advantage of you!"
                            ul {
                                li { +"Take calls" }
                                li { +"Make photocopies" }
                                li { +"Keep employees happy" }
                                li { +"Fix computers" }
                                li { +"Make your boss proud" }
                            }
                            +"\n(2021)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/classic_snake_game.png")
                        }
                        p {
                            b {
                                +"A classic snake game?("
                                a(href = "https://hermesse.itch.io/a-classic-snake-game", target = "_blank") { +"Web" }
                                +", "
                                a(href = "https://github.com/snakegamejam/korgejam", target = "_blank") { +"Git" }
                                +"): "
                            }
                            //+"El clasico juego de la serpiente, o quizás no tan clasico?...Juega para averiguarlo. El juego está hecho en korge, un motor de juegos experimental con kotlin."
                            +"A classic snake game, or maybe it's not as classic as you think...Play it to find out. The game is made in korge, an experimental engine made with kotlin. (2020)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/duck_crossing.png")
                        }
                        p {
                            b {
                                +"Duck Crossing("
                                a(href = "https://hermesse.itch.io/duck-crossing", target = "_blank") { +"Web" }
                                +", "
                                a(href = "https://github.com/ArcadeTeam/LD46", target = "_blank") { +"Git" }
                                +"): "
                            }
                            //+"Eres una mamá pato que ha perdido a sus patitos, tu deber es encontrarlos y protegerlos de cualquier mal."
                            +"A mamma duck has lost her ducklings, find them and protect them from evil. (2020)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            imgZoom("img/pymes.gif")
                        }
                        p {
                            b {
                                +"Pyme's Genocide("
                                a(href = "https://jaimuepe.itch.io/pymes-genocide", target = "_blank") { +"Web" }
                                +", "
                                a(href = "https://github.com/ArcadeTeam/LD43", target = "_blank") { +"Git" }
                                +"): "
                            }
                            //+"Eres Pyme, una aventurera espacial, debido a daños en tu nave has acabado estrellándote en un planeta lleno de misteriosas criaturas (Las cuales puedes usar de combustible para tu jetpack). Encuentra las 4 piezas necesarias para reparar tu nave y volver a casa."
                            +"You are Pyme, a space adventurer, due to damage to your ship you ended up crashing on a planet full of mysterious creatures (which you can use as fuel for your jetpack). Find the 4 pieces needed to repair your ship and return home. (2018)"
                        }
                    }
                }
            }

            personal = popup("modal-xl", "Personal") {
                h1 { +"Personal" }
                carousel(indicatorsImages = listOf("img/ustealthgame.webp", "img/sunsetGalaxy.webp","img/upsidedown.jpg", "img/wolf_engine.webp", "img/doubledragon.jpg","img/feedyourplanet.jpg","img/deadoralive.jpg")){
                    carouselItem(true) {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/VBBwageSZLk?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Unreal Stealth Game: "
                            }
                            //+"Proyecto del master realizado en Unity 3d junto a cuatro miembros más."
                            +"A game made in a Udemy course (2021) "
                            a(href = "https://www.udemy.com/course/unrealengine-cpp/", target = "_blank"){
                                +"www.udemy.com/course/unrealengine-cpp/"
                            }
                        }
                    }
                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/RazqcQx_HPw?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Sunset Galaxy: "
                            }
                            //+"Proyecto del master realizado en Unity 3d junto a cuatro miembros más."
                            +"A spherical minecraft prototype, currently in progress. (2019)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/XzZSLt3-GAs?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Upside Down("
                                a(href = "https://github.com/SleepyCreepy/Upside-Down", target = "_blank") { +"Git" }
                                +", "
                                a(href = "https://github.com/SleepyCreepy/Upside-Down/releases/tag/1.0-beta", target = "_blank") { +"Download" }
                                +"): "
                            }
                            //+"Proyecto del master realizado en Unity 3d junto a cuatro miembros más."
                            +"Master's project realized in Unity 3d with four other members. (2017)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/1StwlJNXf2Y?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Wolf engine("
                                a(href = "https://github.com/SleepyCreepy/WolfEngine", target = "_blank") { +"Git" }
                                +", "
                                a(href = "https://github.com/SleepyCreepy/WolfEngine/releases/download/1.0.0/Wolf.Engine.v1.0.rar") { +"Download" }
                                +"): "
                            }
                            +"Game engine made enterilly with C++ using SDL library, with help of two programmers. (2017)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/w1E6819xLXg?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Double Dragon III("
                                a(href = "https://masacre23.github.io/DoubleDragon/", target = "_blank"){ +"Web" }
                                +"): "
                            }
                            //+"Versión del famoso juego arcade, realizado en C++."
                            +"Version of the famous arcade game, made it with C++. (2017)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/EW-VpzlLMnQ?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Feed Your Planet: "
                            }
                            //+"Juego Multijugador para móvil similar a \"agar.io\", realizado en Unity3D."
                            +"Multiplayer mobile game like \"agar.io\", made it with Unity3D. (2016)"
                        }
                    }

                    carouselItem {
                        div("embed-responsive embed-responsive-16by9") {
                            iframe(classes = "embed-responsive-item") {
                                src = "https://www.youtube.com/embed/DbmxqT8sanI?enablejsapi=1"

                                attributes["frameborder"] = "0"
                                attributes["allowfullscreen"] = "true"
                            }
                        }
                        p {
                            b {
                                +"Dead Or Alive: "
                            }
                            //+"Proyecto de animación, realizado con Python en Blender."
                            +"Animation project, created with Python in Blender. (2016)"
                        }
                    }
                }
            }

            about = popup("modal-lg", "About") {
                h1 { +"About me" }
                img(classes = "profile", src = "img/yo.jfif")
                p{
                    /* +("Siempre he sido un apasionado de la informática y los videojuegos.\n" +
                     "\n" +
                     "Soy graduado en Ingeniería Multimedia por la Universidad de Valencia, tengo un Máster de Diseño y Desarrollo de Videojuegos de la Universidad Politécnica de Cataluña.\n" +
                     "\n" +
                     "Tengo conocimientos centrados en la programación de videojuegos y desarrollo web, ya sea por mi formación o de manera autodidacta. Sobretodo quiero destacar C++, C, C# y Unity3D en cuanto a desarrollo de videojuegos y HTML5, CSS y JavaScript en desarrollo web.\n" +
                     "\n" +
                     "Tengo siempre ganas de aprender y si me motiva algo no puedo parar. Si no estoy en una game jam, estoy haciendome algún proyecto personal.\n" +
                     "\n" +
                     "Me gusta trabajar en equipo y que haya una visión en común clara del proyecto que permita llevarlo a cabo de manera satisfactoria, agradable y eficiente.")*/

                    +("I have always been passionate about computers and video games.\n" +
                            "\n" +
                            "I have a degree in Multimedia Engineering from the University of Valencia, and a Master's Degree in Video Game Design and Development from the Polytechnic University of Catalonia.\n" +
                            "\n" +
                            "My knowledge is focused on video game programming and web development, either by my formation or self-taught. Above all, I would highlight my experience with Kotlin, C++, C# and Unity3D in terms of game development and HTML5, Bootstrap, SASS and JavaScript in web development.\n" +
                            "\n" +
                            "I'm always willing to learn and if I'm motivated by a project I can't stop until it's done. If I'm not participating in a game jam, I'm tinkering with personal projects.\n" +
                            "\n" +
                            "I like to work in a team with a clear common vision of the project that allows us to carry it out in a satisfactory, enjoyable and efficient way.")
                }
            }
        }

        window.addEventListener("mousedown", {event->
            popups.forEach {
                if (document.getElementById(it.id)!!.children.asList().first().contains(event.target as HTMLElement)) {
                    // Clicked in box
                } else if (it.isVisible() && !it.showing) {
                    it.hide()
                    pauseYoutubeVideos()
                    return@addEventListener
                }
            }
        });

        if(!(document.location?.hash == "#Info" || document.location?.hash == ""))
            document.location?.href = ""
        else info.show()
    }

    private fun A.active() {
        document.getElementsByTagName("a").asList().forEach {
            it.removeClass("active")
            if((it.asDynamic().href as String).endsWith(this.href)) {
                it.addClass("active")
            }
        }
    }
}
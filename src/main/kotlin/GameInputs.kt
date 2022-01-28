import GameInputs.inputs
import kotlinx.browser.document


enum class Input(val keyCode: Int = -1, var pressing: Boolean = false) {
    ENTER(13),
    LEFT(37),
    UP(38),
    RIGHT(39),
    DOWN(40),
    W(87),
    A(65),
    S(83),
    D(68),
    NONE;

    fun isPressed() = inputs.find { it.keyCode == keyCode }!!.pressing
}

object GameInputs {
    val inputs = Input.values()
    var lastPress = Input.NONE

    init {
        document.addEventListener("keydown",
            {evt->
                val keyCode = evt.asDynamic().keyCode as Int
                inputs.find { it.keyCode == keyCode }?.apply {
                    pressing = true
                    lastPress = this
                }
            }, false)

        document.addEventListener("keyup",{ evt->
            val keyCode = evt.asDynamic().keyCode as Int
            inputs.find { it.keyCode == keyCode }?.pressing = false
        },false)
    }
}
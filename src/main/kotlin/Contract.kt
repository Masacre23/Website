import utils.game.GameObject
import utils.game.Rectangle
import utils.common.Vector2

class Contract(val position: Vector2): GameObject("img/contrato.png", Rectangle(position.x, position.y, 20, 20))
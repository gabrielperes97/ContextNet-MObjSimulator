package gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes

import javax.json.JsonValue

interface ActuatorType {

    val type: String

    val startupState : Any

    fun validate(json: JsonValue): Boolean

    fun decode(json: JsonValue): Any

}
package gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes

import javax.json.JsonValue

class CallActuator : ActuatorType {
    override val type: String
        get() = "Call"

    override val startupState: Any
        get() = true

    override fun validate(json: JsonValue): Boolean {
        return true
    }

    override fun decode(json: JsonValue): String {
        return true.toString()
    }
}
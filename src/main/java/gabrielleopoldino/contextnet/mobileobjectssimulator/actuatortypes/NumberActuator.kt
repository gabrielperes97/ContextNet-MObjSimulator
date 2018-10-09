package gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes

import javax.json.JsonNumber
import javax.json.JsonValue

class NumberActuator : ActuatorType {

    override val type: String
        get() = "Number"

    override val startupState: Any
        get() = 0

    override fun validate(json: JsonValue): Boolean {
        return (json.valueType == JsonValue.ValueType.NUMBER)
    }

    override fun decode(json: JsonValue): Number {
        return (json as JsonNumber).numberValue()
    }

}
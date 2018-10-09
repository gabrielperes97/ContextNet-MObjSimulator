package gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes

import javax.json.JsonValue

class BooleanActuator : ActuatorType {

    override val type: String
        get() = "Boolean"

    override val startupState: Any
        get() = false

    override fun validate(json: JsonValue): Boolean {
        return (json.valueType == JsonValue.ValueType.TRUE || json.valueType == JsonValue.ValueType.FALSE)
    }

    override fun decode(json: JsonValue): Boolean {
        return (json.valueType == JsonValue.ValueType.TRUE)

    }

}
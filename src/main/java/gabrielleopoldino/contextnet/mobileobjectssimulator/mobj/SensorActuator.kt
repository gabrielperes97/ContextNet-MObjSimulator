package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.ActuatorType
import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.SensorType
import javax.json.JsonObject

class SensorActuator constructor(override val name: String, val sensorType: SensorType, val actuatorType: ActuatorType): SensorImpl, ActuatorImpl{

    private val sensor = Sensor(name, sensorType)
    private val actuator = Actuator(name, actuatorType)

    override val status: Any
        get() = sensor.name

    override fun write(json: JsonObject) {
        actuator.write(json)
    }

    override fun read(): JsonObject {
        return sensor.read()
    }
}
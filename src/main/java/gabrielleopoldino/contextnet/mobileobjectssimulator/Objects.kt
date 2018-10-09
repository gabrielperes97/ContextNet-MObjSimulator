package gabrielleopoldino.contextnet.mobileobjectssimulator

import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.BooleanActuator
import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.CallActuator
import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.NumberActuator
import gabrielleopoldino.contextnet.mobileobjectssimulator.mobj.*
import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.BooleanSensor
import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.NumberSensor
import java.util.*

object Objects {

    fun getObj1(): MOBJ {
        val interruptor = SensorActuator("interruptor", sensorType = BooleanSensor(), actuatorType = BooleanActuator())
        return MOBJ(uuid = UUID.fromString("8ae195a5-474b-4921-ac6f-4cfad99859b6"),
                sensors = mapOf(
                        "Temperatura" to Sensor("Temperatura", NumberSensor(10, 30)),
                        "Pressão" to Sensor("Pessão", NumberSensor(40, 60)),
                        "Humidade" to Sensor("Humidade", NumberSensor(90, 110)),
                        interruptor.name to interruptor),
                actuators = mapOf(
                        "Corrente" to Actuator("Corrente", NumberActuator()),
                        interruptor.name to interruptor,
                        "Buzina" to Actuator("Buzina", CallActuator()))
                )
    }
}
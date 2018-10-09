package gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes

import java.util.concurrent.ThreadLocalRandom

class BooleanSensor: SensorType {
    override val type: String
        get() = "Boolean"

    override fun generateRandom():String {
        return ThreadLocalRandom.current().nextBoolean().toString()
    }
}
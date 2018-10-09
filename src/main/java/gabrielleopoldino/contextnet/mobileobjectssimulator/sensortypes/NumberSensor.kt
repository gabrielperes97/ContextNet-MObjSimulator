package gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes

import java.util.concurrent.ThreadLocalRandom

class NumberSensor constructor(val lowerBound:Number, val upperBound: Number): SensorType {
    override val type: String
        get() = "Number"

    override fun generateRandom():String {
        return ThreadLocalRandom.current().nextDouble(lowerBound.toDouble(), upperBound.toDouble()).toString()
    }
}
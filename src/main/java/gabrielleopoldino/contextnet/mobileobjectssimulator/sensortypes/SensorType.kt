package gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes

interface SensorType {

    val type: String

    fun generateRandom():String

}
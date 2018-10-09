package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import javax.json.JsonObject

interface SensorImpl {
    val name: String

    fun read():JsonObject
}
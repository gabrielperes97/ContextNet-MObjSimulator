package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import javax.json.JsonObject

interface ActuatorImpl {
    val status: Any
    val name: String

    fun write(json: JsonObject)
}
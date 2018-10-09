package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.ActuatorType
import javax.json.JsonObject

class Actuator constructor(override val name:String, val type: ActuatorType) : ActuatorImpl {

    override val status: Any = type.startupState

    override fun write(json: JsonObject){
        val data = json["data"]
        if (data != null)
            println("$name changed from ${status.toString()} to ${type.decode(data)}")
        else
            error("$name received a non-data message")
    }

}
package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.SensorType
import javax.json.Json
import javax.json.JsonObject

class Sensor constructor(override val name: String, val mock: SensorType) : SensorImpl {

    override fun read():JsonObject{
        val json = Json.createObjectBuilder()
                .add("Name", name)
                .add("Data", mock.generateRandom())
                .build()

        return json
    }


}
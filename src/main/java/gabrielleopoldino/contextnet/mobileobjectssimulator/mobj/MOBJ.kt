package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import java.util.*
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject

class MOBJ(val uuid: UUID, val sensors: Map<String, SensorImpl>, val actuators: Map<String, ActuatorImpl>) {

    /**
     * Called when the MOBJ processReceivedData a requisition to generateDataToSend your data
     * @return A JSON with yours sensors data and your identification
     */
    fun generateDataToSend(): JsonObject {
        val sensorJsonArray = Json.createArrayBuilder()
        sensors.forEach { key, sensor -> sensorJsonArray.add(sensor.read()) }

        val json = Json.createObjectBuilder()
                .add("uuid", uuid.toString())
                .add("sensors", sensorJsonArray)
                .build()
        return json
    }

    /**
     * Called when the MOBJ processReceivedData data.
     * @param json A JsonObject with the data
     */
    fun processReceivedData(actuatorsData: JsonArray){
        actuatorsData.forEach {
            val aData = it as JsonObject
            val aName = aData["name"]
            if (aName != null) {
                val act = actuators[aName.toString()]
                if (act != null) {
                    act.write(aData)
                }
            }
        }
    }

    fun handleMessages(json: JsonObject, sendJson: (json : JsonObject) -> Unit){
        val actuatorsData = json.getJsonArray("actuators")
        if(actuatorsData != null)
        {
            processReceivedData(actuatorsData)
        }

        val waitResponse = json.getBoolean("waitResponse")
        if(waitResponse)
        {
            val dataToSend = generateDataToSend()
            sendJson(dataToSend)
        }
    }
}
package gabrielleopoldino.contextnet.mobileobjectssimulator

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.ActuatorType
import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.BooleanActuator
import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.CallActuator
import gabrielleopoldino.contextnet.mobileobjectssimulator.actuatortypes.NumberActuator
import gabrielleopoldino.contextnet.mobileobjectssimulator.mobj.*
import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.BooleanSensor
import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.NumberSensor
import gabrielleopoldino.contextnet.mobileobjectssimulator.sensortypes.SensorType
import leopoldino.smrudp.SecurityProfile
import java.io.FileInputStream
import java.lang.System.exit
import java.net.InetSocketAddress
import java.security.SecureRandom
import java.util.*
import javax.json.Json
import javax.json.JsonObject
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import kotlin.collections.HashMap

object MobileObjectsSimulator {

    private class Args {

        @Parameter(names = ["-f", "--files"], description = "The MObj file descriptor", required = true)
        var file : String = ""

        @Parameter(names = ["-i", "--ip"], description = "The MobileHub Ip address [127.0.0.1]")
        var mobilehubIp : String = "127.0.0.1"

        @Parameter(names = ["-p", "--port"], description = "The MobileHub port [12346]")
        var mobilehubPort : Int = 12346
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val parsedArgs : Args = Args()
        val jc = JCommander.newBuilder()
                .addObject(parsedArgs)
                .build()
        jc.parse(*args)

        val file = FileInputStream(parsedArgs.file)
        val jsonReader = Json.createReader(file)
        val json = jsonReader.readObject()

        val mobjs = decode(json, InetSocketAddress(parsedArgs.mobilehubIp, parsedArgs.mobilehubPort))
        println("${mobjs.size} MObjs founded")
        println("Starting MObjs in MobileHub ${parsedArgs.mobilehubIp}:${parsedArgs.mobilehubPort}")
        mobjs.forEach{
            it.start()
        }
    }

    fun decode(json : JsonObject, addr : InetSocketAddress): List<MOBJCommunication>{
        val jObjs  = json.getJsonArray("objects")
        val objs = ArrayList<MOBJCommunication>()
        jObjs.forEach {
            val sensors = HashMap<String, SensorImpl>()

            it.asJsonObject().getJsonArray("sensors").forEach {
                val sensorDescription = it.asJsonObject()
                val sensorType : SensorType
                when(sensorDescription.getString("type")){
                    "number" -> {
                        sensorType = NumberSensor(
                                lowerBound = sensorDescription.getJsonNumber("lowerBound").numberValue(),
                                upperBound = sensorDescription.getJsonNumber("upperBound").numberValue()
                                )
                    }
                    "boolean" -> {
                        sensorType = BooleanSensor()
                    }
                    else -> {
                        error("Sensor type not found")
                    }
                }
                val sensor = Sensor(name = sensorDescription.getString("name"), mock = sensorType)
                sensors.put(sensor.name, sensor)
            }

            val actuators = HashMap<String, ActuatorImpl>()

            it.asJsonObject().getJsonArray("actuators").forEach {
                val actuatorDescription = it.asJsonObject()
                val actuatorType : ActuatorType
                when (actuatorDescription.getString("type")){
                    "number" -> {
                        actuatorType = NumberActuator()
                    }
                    "boolean" -> {
                        actuatorType = BooleanActuator()
                    }
                    "call" -> {
                        actuatorType = CallActuator()
                    }
                    else -> {
                        error("Actuator type no found")
                    }
                }
                val actuator = Actuator(name = actuatorDescription.getString("name"), type = actuatorType)
                actuators.put(actuator.name, actuator)
            }

            it.asJsonObject().getJsonArray("sensorsActuators").forEach {
                val sensorsActuatorsDescription = it.asJsonObject()
                val sensorType : SensorType
                val sensorDescription = sensorsActuatorsDescription.getJsonObject("sensor")
                when(sensorDescription.getString("type")){
                    "number" -> {
                        sensorType = NumberSensor(
                                lowerBound = sensorDescription.getJsonNumber("lowerBound").numberValue(),
                                upperBound = sensorDescription.getJsonNumber("upperBound").numberValue()
                        )
                    }
                    "boolean" -> {
                        sensorType = BooleanSensor()
                    }
                    else -> {
                        error("Sensor type not found")
                    }
                }

                val actuatorType : ActuatorType
                val actuatorDescription = sensorsActuatorsDescription.getJsonObject("actuator")
                when (actuatorDescription.getString("type")){
                    "number" -> {
                        actuatorType = NumberActuator()
                    }
                    "boolean" -> {
                        actuatorType = BooleanActuator()
                    }
                    "call" -> {
                        actuatorType = CallActuator()
                    }
                    else -> {
                        error("Actuator type no found")
                    }
                }

                val sensorActuator = SensorActuator(name = sensorsActuatorsDescription.getString("name"),
                        sensorType = sensorType,
                        actuatorType = actuatorType)
                sensors.put(sensorActuator.name, sensorActuator)
                actuators.put(sensorActuator.name, sensorActuator)
            }

            val mobj : MOBJ
            val communication : MOBJCommunication

            val securityData = it.asJsonObject().get("security")
            if (securityData == null)
            {
                val uuid : UUID
                if (it.asJsonObject()["uuid"] == null)
                    uuid = UUID.randomUUID()
                else
                    uuid = UUID.fromString(it.asJsonObject().getString("uuid"))

                mobj = MOBJ(uuid, sensors, actuators)
                communication = MOBJCommunication(mobj = mobj, address = addr)
            }
            else{
                val identity = SecurityProfile.loadKeyStoreFromFile(securityData.asJsonObject().getString("identity"),
                        securityData.asJsonObject().getString("identityPassword"),
                        "PKCS12")
                val uuid = SecurityProfile.getUuidFromKeystore(identity) ?: error("Identity has no UUID")
                val trust = SecurityProfile.loadKeyStoreFromFile(securityData.asJsonObject().getString("trust"), "")

                mobj = MOBJ(uuid, sensors, actuators)

                val context = SSLContext.getInstance("TLSv1.2")

                val keyFact = KeyManagerFactory.getInstance("SunX509")
                keyFact.init(identity, securityData.asJsonObject().getString("identityPassword").toCharArray())
                val trustFact = TrustManagerFactory.getInstance("SunX509")
                trustFact.init(trust)

                context.init(keyFact.keyManagers, trustFact.trustManagers, SecureRandom())

                val privateKey = SecurityProfile.getPrivateKeyFromKeystore(identity, securityData.asJsonObject().getString("identityPassword"))
                communication = MOBJSecureCommunication(mobj = mobj, address = addr, context = context, privateKey = privateKey)
            }
            objs.add(communication)
        }

        return objs
    }




}
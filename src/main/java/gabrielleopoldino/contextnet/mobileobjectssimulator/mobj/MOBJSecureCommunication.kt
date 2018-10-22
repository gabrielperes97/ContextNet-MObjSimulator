package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import java.io.*
import java.net.InetSocketAddress
import java.security.PrivateKey
import java.security.Signature
import java.util.*
import javax.json.Json
import javax.json.JsonObject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

class MOBJSecureCommunication constructor(address: InetSocketAddress, mobj: MOBJ, val context: SSLContext, privateKey : PrivateKey): MOBJCommunication(address, mobj){

    private val socket : SSLSocket = context.socketFactory.createSocket() as SSLSocket
    private val connectThread = ConnectThread()
    private val sendReceiveThread = SendReceiveThread()

    val signer = Signature.getInstance("SHA1withRSA")

    init {
        socket.needClientAuth = true
        signer.initSign(privateKey)
    }

    override fun start() {
        connectThread.start()
    }

    inner class ConnectThread : Thread("Connect Thread"){
        override fun run() {
            while (!socket.isConnected){
                try {
                    socket.connect(address, 1000)
                    println("Connected to ${socket.inetAddress.hostAddress}")
                    sendReceiveThread.start()
                }catch (e: IOException)
                {

                }
            }
        }
    }

    inner class SendReceiveThread: Thread("Receive Thread"){

        private lateinit var reader: BufferedReader
        private lateinit var out: PrintStream

        override fun start() {
            reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            out = PrintStream(socket.getOutputStream())
            super.start()
        }

        override fun run() {
            while (socket.isConnected){
                val str = reader.readLine()
                val json = Json.createReader(StringReader(str)).readObject()
                //Process message and Send-back data
                mobj.handleMessages(json) {

                    out.println(signJson(it))
                    out.flush()
                }
            }
        }
    }

    fun signJson(message: JsonObject): JsonObject {

        signer.update(message.toString().toByteArray())
        val signBytes = signer.sign()
        return Json.createObjectBuilder(message)
                .add("MObj Signature", Arrays.toString(signBytes))
                .build()

    }

}
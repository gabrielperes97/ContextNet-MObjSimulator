package gabrielleopoldino.contextnet.mobileobjectssimulator.mobj

import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import javax.json.Json
import javax.json.JsonReader
import javax.json.JsonWriter

class MOBJCommunication constructor(val address: InetSocketAddress, val mobj: MOBJ){

    private val socket : Socket = Socket()
    private val connectThread = ConnectThread()
    private val sendReceiveThread = SendReceiveThread()

    fun start() {
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
                mobj.handleMessages(json) {
                    out.println(it)
                    out.flush()
                }
            }
        }
    }

}
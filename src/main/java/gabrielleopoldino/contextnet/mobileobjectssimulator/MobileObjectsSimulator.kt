package gabrielleopoldino.contextnet.mobileobjectssimulator

import gabrielleopoldino.contextnet.mobileobjectssimulator.mobj.MOBJCommunication
import java.lang.System.exit
import java.net.InetSocketAddress

object MobileObjectsSimulator {

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size <= 2) {
            println("Falta especificar Ip e porta")
            exit(1)
        }
        val mobj1 = MOBJCommunication(mobj = Objects.getObj1(), address = InetSocketAddress(args[0], args[1].toInt()))
        mobj1.start()
    }
}
package com.example.fortuna

import android.net.InetAddresses
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPListener(private val port: Int, private val onPacketReceived: (String, String) -> Unit)
{
    private var listening: Boolean = true

    fun startListening() {
        val thread = Thread {
            try {
                val socket = DatagramSocket(port)
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)

                while (listening) {
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    packet.address
                    onPacketReceived(message, packet.address.toString())
                }
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    fun stopListening() {
        listening = false
    }
}


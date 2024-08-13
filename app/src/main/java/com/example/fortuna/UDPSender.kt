package com.example.fortuna

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPSender(private val address: String, private val port: Int)
{
    private var sending = false

    fun sendUdpPacket(message: String)
    {
        val thread = Thread {
            try {
                val socket = DatagramSocket()
                val buffer = message.toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(address), port)
                socket.send(packet)
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    fun startSendUdpCyclicPacket(message: String, cycleTimeMs: Int)
    {
        if(sending)
            sending = false
        val thread = Thread {
            try {
                val socket = DatagramSocket()
                val buffer = message.toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(address), port)
                sending = true
                while(sending) {
                    socket.send(packet)
                    Thread.sleep(cycleTimeMs +0L)
                }
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    fun stopSendUdpCyclicPacket()
    {
        sending = false
    }

}
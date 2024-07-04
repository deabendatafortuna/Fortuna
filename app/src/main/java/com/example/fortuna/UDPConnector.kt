package com.example.fortuna

import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.regex.MatchResult

class UDPConnector(private val onConnectionEstablished: () -> Unit)
{
    private val BROADCAST_PORT = 8888
    private val BROADCAST_HOST_MESSAGE = "FORTUNA_DISCOVERY_MESSAGE_HOST_PORT_"
    private val BROADCAST_DEVICE_MESSAGE = "^FORTUNA_DISCOVERY_MESSAGE_DEVICE_PORT_(\\d{1,5})$"

    private var deviceAddress = ""
    private var devicePort = ""
    private var hostPort = 0
    private lateinit var udpReceiver: UDPListener
    private lateinit var udpDeviceCheckReceiver: UDPListener
    private lateinit var udpSender: UDPSender

    private var connected = false
    public fun startTryConnect()
    {
        startDeviceMessageCheck(BROADCAST_PORT)
    }

    public fun isConnected():Boolean
    {
        return connected
    }

    private fun startDeviceMessageCheck(port:Int)
    {
        if (isUdpPortAvailable(port))
        {
            udpDeviceCheckReceiver = UDPListener(port) { message, address ->
                onReceivedMessage(message, address) }

            udpDeviceCheckReceiver.startListening()
        }
    }

    private fun extractPortNumber(message: String): Int? {
        val matchResult = isConnectionRequestMessage(message)

        if (matchResult != null) {
            val portString = matchResult.groupValues[1]
            val port = portString.toInt()
            if (port in 1..65535) {
                return port
            }
        }
        return null
    }

    private fun isConnectionRequestMessage(message: String) : kotlin.text.MatchResult?
    {
        val regex = Regex(BROADCAST_DEVICE_MESSAGE)
        val matchResult = regex.matchEntire(message)
        return matchResult
    }

    private fun onReceivedMessage(message:String, address:String)
    {
        if(isConnectionRequestMessage(message) != null) {
            val port = extractPortNumber(message)
            if (port != null) {
                deviceAddress = address.replace("/", "")
                devicePort = port.toString()
                stopDeviceMessageCheck()

                val hPort = findFirstAvailableUdpPort(1024, 65535)
                if (hPort != null) {
                    hostPort = hPort
                    startDeviceMessageCheck(hostPort)
                }
                sendHostMessage()
            }
        }
        else if (message == "Connected")
        {
            connected = true
            onConnectionEstablished()
        }
    }

    private fun stopDeviceMessageCheck()
    {
        udpDeviceCheckReceiver.stopListening()
    }

    private fun sendHostMessage() {
        val udpBroadcastSender = UDPSender(deviceAddress, devicePort.toInt())
        udpBroadcastSender.sendUdpPacket(BROADCAST_HOST_MESSAGE + hostPort.toString())
    }
    private fun findFirstAvailableUdpPort(startPort: Int, endPort: Int): Int?
    {
        for (port in startPort..endPort) {
            if (isUdpPortAvailable(port)) {
                return port
            }
        }
        return null
    }

    private fun isUdpPortAvailable(port: Int): Boolean {
        return try {
            DatagramSocket(port).use { socket ->
                socket.localPort == port
            }
        } catch (e: SocketException) {
            false
        }
    }
}
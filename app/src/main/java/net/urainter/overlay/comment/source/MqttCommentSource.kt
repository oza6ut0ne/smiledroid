package net.urainter.overlay.comment.source

import android.content.Context
import androidx.preference.PreferenceManager
import net.urainter.overlay.R
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber

class MqttCommentSource(context: Context, onCommentCallback: (rawMessage: String) -> Unit) {
    private val mqttAndroidClient: MqttAndroidClient
    private val mqttConnectOptions: MqttConnectOptions
    private val mqttActionListener: IMqttActionListener

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val serverUri = sharedPreferences.getString(context.getString(R.string.key_mqtt_url), "") ?: ""
        val mqttUsername = sharedPreferences.getString(context.getString(R.string.key_mqtt_username), null)
        val mqttPassword = sharedPreferences.getString(context.getString(R.string.key_mqtt_password), null)
        val clientId = "${context.getString(R.string.app_name)}-${System.currentTimeMillis()}"
        val subscriptionTopic = sharedPreferences.getString(context.getString(R.string.key_mqtt_topic), "") ?: ""
        val defaultQos = context.getString(R.string.default_key_mqtt_qos)
        val qos = (sharedPreferences.getString(
            context.getString(R.string.key_mqtt_qos),
            defaultQos
        ) ?: defaultQos).toIntOrNull() ?: defaultQos.toInt()
        mqttAndroidClient = MqttAndroidClient(context.applicationContext, serverUri, clientId)

        val callback = object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                Timber.i("connectComplete: reconnect = $reconnect")
                if (reconnect) {
                    subscribeTopic(subscriptionTopic, qos)
                }
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val rawMessage = String(message.payload)
                Timber.i("messageArrived: $topic, $rawMessage")
                onCommentCallback(rawMessage)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // nop.
            }

            override fun connectionLost(cause: Throwable?) {
                if (cause == null) {
                    Timber.i("connectionLost: null")
                } else {
                    Timber.w("connectionLost: $cause")
                }
            }
        }
        mqttAndroidClient.setCallback(callback)
        mqttConnectOptions = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = false
            mqttUsername?.let { userName = it }
            mqttPassword?.let { password = it.toCharArray() }
        }

        mqttActionListener = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Timber.i("connect onSuccess: $asyncActionToken")
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                subscribeTopic(subscriptionTopic, qos)
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Timber.e("connect onFailure: $asyncActionToken, $exception")
            }
        }
    }

    fun connect() {
        if (!mqttAndroidClient.isConnected) {
            mqttAndroidClient.connect(mqttConnectOptions, null, mqttActionListener)
        }
    }

    fun disconnect() {
        if (mqttAndroidClient.isConnected) {
            mqttAndroidClient.disconnect()
        }
    }

    private fun subscribeTopic(topic: String, qos: Int) {
        mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Timber.i("subscribe onSuccess: $asyncActionToken")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Timber.e("subscribe onFailure: $asyncActionToken, $exception")
            }
        })
    }
}

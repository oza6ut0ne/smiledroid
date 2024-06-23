package net.urainter.overlay.comment.source

import android.content.Context
import androidx.preference.PreferenceManager
import net.urainter.overlay.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel


class TcpListenerSource(private val onCommentCallback: (rawMessage: String) -> Unit) {
    private lateinit var selector: Selector
    private var serverSocketChannel: ServerSocketChannel? = null

    fun start(context: Context) {
        CoroutineScope(Dispatchers.IO).launch { startListener(context) }
    }

    private fun startListener(context: Context) {
        Timber.i("TcpListenerSource.start()")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultBindAddress = context.getString(R.string.default_key_tcp_bind_address)
        val bindAddress = sharedPreferences.getString(
            context.getString(R.string.key_tcp_bind_address),
            defaultBindAddress
        ) ?: defaultBindAddress
        val defaultListenPort = context.getString(R.string.default_key_tcp_listen_port)
        val listenPort = (sharedPreferences.getString(
            context.getString(R.string.key_tcp_listen_port),
            defaultListenPort
        ) ?: defaultListenPort).toIntOrNull() ?: defaultListenPort.toInt()

        try {
            selector = Selector.open()
            serverSocketChannel = ServerSocketChannel.open().apply {
                configureBlocking(false)
                setOption(StandardSocketOptions.SO_REUSEADDR, true)
                socket().bind(InetSocketAddress(bindAddress, listenPort))
                register(selector, SelectionKey.OP_ACCEPT)
            }
        } catch (e: IOException) {
            Timber.e("TcpListenerSource.start() initialize failed: $e")
            stop()
            return
        }

        try {
            while (selector.select() > 0) {
                val iter: MutableIterator<*> = selector.selectedKeys().iterator()
                while (iter.hasNext()) {
                    val key = iter.next() as SelectionKey
                    iter.remove()

                    when {
                        key.isAcceptable -> handleAccept(key)
                        key.isReadable -> handleRead(key)
                    }
                }
            }
        } catch (e: IOException) {
            Timber.e("TcpListenerSource select loop failed: $e")
        } finally {
            stop()
        }
    }

    private fun handleAccept(key: SelectionKey) {
        try {
            val socketChannel = (key.channel() as ServerSocketChannel).accept()
            socketChannel.configureBlocking(false)
            socketChannel.register(selector, SelectionKey.OP_READ, ByteArray(0))
        } catch (e: IOException) {
            Timber.e("TcpListenerSource.handleAccept() failed: $e")
        }
    }

    private fun handleRead(key: SelectionKey) {
        try {
            val socketChannel = key.channel() as SocketChannel
            val buf = ByteBuffer.allocate(1024)
            val read = socketChannel.read(buf)
            val data = (key.attachment() as ByteArray) + buf.array().sliceArray(0..<buf.position())
            if (read < 0) {
                socketChannel.close()
                val rawMessage = String(data)
                Timber.i("TcpListenerSource.handleRead() complete: $rawMessage")
                onCommentCallback(rawMessage)
                return
            }
            key.attach(data)
        } catch (e: IOException) {
            Timber.e("TcpListenerSource.handleRead() failed: $e")
        }
    }

    fun stop() {
        Timber.i("TcpListenerSource.stop()")
        try {
            selector.close()
            serverSocketChannel?.close()
        } catch (e: IOException) {
            Timber.e("TcpListenerSource.stop() failed: $e")
        }
    }
}

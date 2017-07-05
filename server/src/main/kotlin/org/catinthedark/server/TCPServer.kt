package org.catinthedark.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.AbstractChannel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import org.catinthedark.shared.serialization.Deserializer
import org.catinthedark.shared.serialization.NettyDecoder
import org.catinthedark.shared.serialization.NettyEncoder
import org.catinthedark.shared.serialization.Serializer
import org.slf4j.LoggerFactory

class TCPServer(
        private val serializer: Serializer,
        private val deserializer: Deserializer
) {
    private val PORT = 8080
    private val log = LoggerFactory.getLogger(this::class.java)

    fun run() {
        val bossGroup = NioEventLoopGroup(1)
        val workerGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .childHandler(object : ChannelInitializer<AbstractChannel>() {
                        override fun initChannel(ch: AbstractChannel) {
                            val pipe = ch.pipeline()

                            pipe.addLast("decoder", NettyDecoder(deserializer))
                            pipe.addLast("encoder", NettyEncoder(serializer))
                            pipe.addLast("handler", GameHandler())
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

            val f = b.bind(PORT).sync()

            log.info("TCP sever is up on port $PORT")
            f.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }
}
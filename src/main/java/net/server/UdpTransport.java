package net.server;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

/**
 * @author: xuzefan
 * @date: 2022/12/9 14:19
 */
public class UdpTransport {

    public Mono<? extends Connection> start(KcpServerConfig config) {
        UdpServer server = UdpServer.create()
                .port(config.getPort())
                .wiretap(config.isLog())
                .host(config.getIp())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        return server.bind();
    }

}

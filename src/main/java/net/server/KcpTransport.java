package net.server;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import net.base.KcpConfiguration;
import net.base.KcpTransportConnection;
import net.utils.AttributeKeys;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;
import reactor.netty.udp.UdpClient;
import reactor.netty.udp.UdpServer;

import java.util.Optional;

/**
 * @author: xuzefan
 * @date: 2022/11/17 11:40
 */
@Slf4j
public class KcpTransport {


    public Mono<? extends Connection> start(KcpServerConfig config) {
        return buildServer(config)
                .doOnBound(connection -> {
                })
                .bind();
    }


    private UdpServer buildServer(KcpServerConfig config) {
        UdpServer server = UdpServer.create()
                .port(config.getPort())
                .wiretap(config.isLog())
                .host(config.getIp())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return server;

    }

    public Mono<KcpTransportConnection> connect(KcpConfiguration config) {
        return buildClient(config)
                .connect()
                .map(connection -> {
                    log.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&链接成功了");
                    KcpTransportConnection transportConnection = new KcpTransportConnection(connection);
                    connection.onDispose(() -> retryConnect(config,transportConnection));
                    return transportConnection;
                });
    }


    private void retryConnect(KcpConfiguration config, KcpTransportConnection transportConnection) {
        log.info("短线重连中..............................................................");
        buildClient(config)
                .connect()
                .doOnError(config.getThrowableConsumer())
                .retry()
                .cast(Connection.class)
                .subscribe(connection -> {
                    Optional.ofNullable(transportConnection.getConnection().channel().attr(AttributeKeys.clientConnectionAttributeKey))
                            .map(Attribute::get)
                            .ifPresent(rsocketClientSession ->{
                                transportConnection.setConnection(connection);
                                transportConnection.setInbound(connection.inbound());
                                transportConnection.setOutbound(connection.outbound());
                                rsocketClientSession.initHandler();
                            });

                });
    }

    private UdpClient buildClient(KcpConfiguration config) {
        UdpClient client = UdpClient.create()
                .port(config.getPort())
                .host(config.getIp())
                .wiretap(config.isLog());
        return client;
    }
}

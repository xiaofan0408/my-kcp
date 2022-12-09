package net.server;


import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;
import net.utils.AttributeKeys;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;


/**
 * @author: xuzefan
 * @date: 2022/12/9 10:09
 */
public class KcpServerConn {

    private Connection disposableServer;

    private KcpTransportConnection kcpTransportConnection;

    private KcpServerConfig kcpServerConfig;

    public KcpServerConn(KcpServerConfig config, KcpTransportConnection server){
        kcpTransportConnection = server;
        kcpServerConfig = config;
        this.disposableServer = server.getConnection();
        subscribe(server);
    }


    private void subscribe(KcpTransportConnection connection) {
        Connection c = connection.getConnection();
        NettyInbound inbound = c.inbound();
        c.channel().attr(AttributeKeys.connectionAttributeKey).set(connection); // 设置connection

        inbound.receive().subscribe(byteBuf -> {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            KcpTransportMessage kcpTransportMessage = KcpTransportMessage.builder().message(bytes).build();
            kcpServerConfig.getOnReceive().accept(kcpTransportMessage,connection);
        });
    }


    public Mono<Void> onDispose() {
        return disposableServer.onDispose();
    }


    public KcpServerConn bind() {
        disposableServer.bind();
        return this;
    }

}

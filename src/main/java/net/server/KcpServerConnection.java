package net.server;



import io.netty.util.Attribute;
import net.base.IKcpChannelManager;
import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;
import net.base.impl.KcpChannelManager;
import net.utils.AttributeKeys;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class KcpServerConnection implements KcpServerSession {


    private Connection disposableServer;

    private KcpServerConfig config;


    private IKcpChannelManager channelManager;


    private KcpServerMessageRouter messageRouter;


    public KcpServerConnection(KcpServerConfig config, KcpTransportConnection connection) {
        this.disposableServer = connection.getConnection();
        this.config = config;
        this.channelManager = new KcpChannelManager();
        this.messageRouter = new KcpServerMessageRouter(config);
        subscribe(connection);
    }

    private void subscribe(KcpTransportConnection connection) {
        NettyInbound inbound = connection.getInbound();
        Connection c = connection.getConnection();
        Disposable disposable = Mono.fromRunnable(c::dispose)// 定时关闭
                .delaySubscription(Duration.ofSeconds(10))
                .subscribe();
        c.channel().attr(AttributeKeys.connectionAttributeKey).set(connection); // 设置connection
        c.channel().attr(AttributeKeys.closeConnection).set(disposable);   // 设置close
        connection.getConnection().onDispose(() -> { // 关闭  发送will消息
            channelManager.removeConnections(connection); // 删除链接
            Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.device_id))
                    .map(Attribute::get)
                    .ifPresent(channelManager::removeDeviceId); // 设置device Id
            connection.destory();
        });
        inbound.receive().subscribe(byteBuf -> {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            KcpTransportMessage kcpTransportMessage = KcpTransportMessage.builder().message(bytes).build();
            messageRouter.handler(kcpTransportMessage,connection);
        });
    }


    @Override
    public Mono<List<KcpTransportConnection>> getConnections() {
        return Mono.just(channelManager.getConnections());
    }

    @Override
    public Mono<Void> closeConnect(String clientId) {
        return Mono.fromRunnable(()->Optional.ofNullable(channelManager.getRemoveDeviceId(clientId))
                .ifPresent(KcpTransportConnection::dispose));
    }

    @Override
    public Mono<Void> onDispose() {
        return disposableServer.onDispose();
    }

    @Override
    public KcpServerSession bind() {
         disposableServer.bind();
         return this;
    }


    @Override
    public void dispose() {
        disposableServer.dispose();
    }


}

package net.client;



import lombok.extern.slf4j.Slf4j;
import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;
import net.utils.AttributeKeys;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;

import java.time.Duration;
import java.util.function.BiConsumer;

@Slf4j
public class KcpClientConnection implements KcpClientSession {

    private final KcpTransportConnection connection;

    private final KcpClientConfig clientConfig;

    private  KcpClientMessageRouter clientMessageRouter;


    public KcpClientConnection(KcpTransportConnection connection, KcpClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.connection = connection;
        this.clientMessageRouter = new KcpClientMessageRouter(clientConfig);

        initHandler();
    }

    public void  initHandler(){
        KcpClientConfig.Options options = clientConfig.getOptions();
        NettyInbound inbound = connection.getInbound();
        Disposable disposable=Mono.fromRunnable(() -> connection.write(KcpTransportMessage.builder().build().toByteBuf())
                .subscribe()).delaySubscription(Duration.ofSeconds(10)).repeat().subscribe();
        connection.write(KcpTransportMessage.builder().build().toByteBuf())
                .doOnError(throwable -> log.error(throwable.getMessage())).subscribe();
        connection.getConnection().channel().attr(AttributeKeys.closeConnection).set(disposable);
//        connection.getConnection().onWriteIdle(clientConfig.getHeart(), () -> connection.sendPingReq().subscribe()); // 发送心跳
//        connection.getConnection().onReadIdle(clientConfig.getHeart()*2, () -> connection.sendPingReq().subscribe()); // 发送心跳
        connection.getConnection().onDispose(()->clientConfig.getOnClose().run());
        inbound.receive().subscribe(byteBuf -> {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            KcpTransportMessage kcpTransportMessage = KcpTransportMessage.builder().message(bytes).build();
            clientMessageRouter.handler(kcpTransportMessage,connection);
        });
        connection.getConnection().channel().attr(AttributeKeys.clientConnectionAttributeKey).set(this);
    }

    @Override
    public Mono<Void> send(byte[] message) {
        KcpTransportMessage kcpTransportMessage = KcpTransportMessage.builder().message(message).build();
        return connection.write(kcpTransportMessage.toByteBuf());
    }


    @Override
    public Mono<Void> messageAcceptor(BiConsumer<String, byte[]> messageAcceptor) {
        return Mono.fromRunnable(() -> clientConfig.setMessageAcceptor(messageAcceptor));
    }

    @Override
    public void dispose() {
        connection.dispose();
    }
}

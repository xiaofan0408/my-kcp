package net.client;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;

public interface KcpClientSession extends Disposable {

    Mono<Void> send(byte[] message);

    Mono<Void> messageAcceptor(BiConsumer<String,byte[]> messageAcceptor);

    void  initHandler();

}

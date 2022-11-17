package net.server;

import net.base.KcpTransportConnection;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author: xuzefan
 * @date: 2022/11/17 11:04
 */
public interface KcpServerSession extends Disposable {

    Mono<List<KcpTransportConnection>> getConnections();

    Mono<Void> closeConnect(String clientId);

    Mono<Void> onDispose();
}

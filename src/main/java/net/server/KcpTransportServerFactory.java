package net.server;

import net.base.KcpTransportConnection;
import reactor.core.publisher.Mono;

/**
 * @author: xuzefan
 * @date: 2022/11/17 10:49
 */
public class KcpTransportServerFactory {

    private KcpServerConfig config;


    public KcpTransportServerFactory(){
    }


    public Mono<KcpServerConn> start(KcpServerConfig config) {
        this.config =config;
        return new UdpTransport().start(config)
                .map(KcpTransportConnection::new)
                .map(this::wrapper)
                .doOnError(config.throwableConsumer);

    }

    private  KcpServerConn wrapper(KcpTransportConnection connection){
        return  new KcpServerConn(config, connection);
    }
}

package net.client;

import net.base.KcpTransportConnection;
import net.server.KcpTransport;
import reactor.core.publisher.Mono;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:38
 */
public class KcpTransportClientFactory {

    private KcpClientConfig clientConfig;


    public KcpTransportClientFactory(){
    }


    public Mono<KcpClientSession> connect(KcpClientConfig config) {
        this.clientConfig=config;
        return  Mono.from( new KcpTransport().connect(config)).map(this::wrapper).doOnError(config.getThrowableConsumer());
    }


    private KcpClientSession wrapper(KcpTransportConnection connection){
        return  new KcpClientConnection(connection,clientConfig);
    }
}

package net.server;

import net.base.KcpTransportConnection;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;

/**
 * @author: xuzefan
 * @date: 2022/11/17 10:49
 */
public class KcpTransportServerFactory {

    private UnicastProcessor<KcpTransportConnection> unicastProcessor = UnicastProcessor.create();

    private KcpServerConfig config;


    public KcpTransportServerFactory(){
    }


    public Mono<KcpServerSession> start(KcpServerConfig config) {
        this.config =config;
        return  Mono.from(
                        new KcpTransport().start(config,unicastProcessor))
                .map(this::wrapper)
                .doOnError(config.getThrowableConsumer());
    }

    private KcpServerConfig copy(KcpServerConfig config) {
        KcpServerConfig serverConfig = new KcpServerConfig();
        return  serverConfig;
    }

    private  KcpServerSession wrapper(Connection server){
        return  new KcpServerConnection(unicastProcessor,server,config);
    }
}

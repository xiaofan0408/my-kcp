package net.server;


import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author: xuzefan
 * @date: 2022/11/17 10:47
 */
public class KcpTransportServer {

    private static KcpServerConfig config;

    private static KcpTransportServerFactory transportFactory;


    private  KcpTransportServer(){
    }
    public  static class TransportBuilder{

        public TransportBuilder(){
            config = new KcpServerConfig();
            transportFactory = new KcpTransportServerFactory();
        }

        public TransportBuilder(String ip,int port){
            this();
            config.setIp(ip);
            config.setPort(port);
        }

        public TransportBuilder log(boolean isLog){
            config.setLog(isLog);
            return this;
        }

        public KcpTransportServer.TransportBuilder exception(Consumer<Throwable> exceptionConsumer ){
            Optional.ofNullable(exceptionConsumer)
                    .ifPresent(config::setThrowableConsumer);
            return this;
        }

        public KcpTransportServer.TransportBuilder onConnected(Consumer<KcpTransportConnection> connectionConsumer ){
            Optional.ofNullable(connectionConsumer)
                    .ifPresent(config::setOnConnected);
            return this;
        }

        public KcpTransportServer.TransportBuilder onReceive(BiConsumer<KcpTransportMessage,KcpTransportConnection> consumer ){
            Optional.ofNullable(consumer)
                    .ifPresent(config::setOnReceive);
            return this;
        }

        public KcpTransportServer.TransportBuilder onClose(Consumer<KcpTransportConnection> consumer ){
            Optional.ofNullable(consumer)
                    .ifPresent(config::setOnClose);
            return this;
        }

        public Mono<KcpServerConn> start(){
            config.checkConfig();
            return transportFactory.start(config);
        }
    }

    public static KcpTransportServer.TransportBuilder create(String ip, int port){
        return  new KcpTransportServer.TransportBuilder(ip,port);
    }

    public static  KcpTransportServer.TransportBuilder create(){
        return  new KcpTransportServer.TransportBuilder();
    }

}

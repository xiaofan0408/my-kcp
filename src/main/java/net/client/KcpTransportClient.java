package net.client;

import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:35
 */
public class KcpTransportClient {

    private  static KcpClientConfig config;

    private static KcpTransportClientFactory transportFactory;

    private static KcpClientConfig.Options options;

    private  KcpTransportClient(){

    }
    public static class TransportBuilder{

        public TransportBuilder(){
            config = new KcpClientConfig();
            transportFactory = new KcpTransportClientFactory();
            options = config.new Options();
        }

        public TransportBuilder(String ip,int port){
            this();
            config = new KcpClientConfig();
            config.setIp(ip);
            config.setPort(port);
        }


        public TransportBuilder onClose(Runnable onClose){
            config.setOnClose(onClose);
            return this;
        }

        public TransportBuilder exception(Consumer<Throwable> exceptionConsumer ){
            config.setThrowableConsumer(exceptionConsumer);
            return this;
        }


        public  TransportBuilder  messageAcceptor(BiConsumer<String,byte[]> messageAcceptor){
            config.setMessageAcceptor(messageAcceptor);
            return this;
        }


        public TransportBuilder clientId(String  clientId){
            options.setClientIdentifier(clientId);
            return this;
        }

        public  TransportBuilder  username(String username){
            options.setUserName(username);
            options.setHasUserName(true);
            return this;
        }


        public  TransportBuilder  password(String password){
            options.setPassword(password);
            options.setHasPassword(true);
            return this;
        }

        public  TransportBuilder  willTopic(String willTopic){
            options.setWillTopic(willTopic);
            options.setHasWillFlag(true);
            return this;
        }

        public  TransportBuilder  willMessage(String willMessage){
            options.setWillMessage(willMessage);
            options.setHasWillFlag(true);
            return this;
        }


        public  TransportBuilder  log(boolean log){
            config.setLog(log);
            return this;
        }


        public Mono<KcpClientSession> connect(){
            config.setOptions(options);
            config.checkConfig();
            return transportFactory.connect(config);
        }

    }

    public static TransportBuilder  create(String ip,int port){
        return  new TransportBuilder(ip,port);
    }

    public TransportBuilder create(){
        return  new TransportBuilder();
    }
}

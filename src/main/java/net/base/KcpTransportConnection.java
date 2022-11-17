package net.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Getter
@Setter
@ToString
@Slf4j
public class KcpTransportConnection implements Disposable {

    private NettyInbound inbound;

    private NettyOutbound outbound;

    private Connection connection;

    private LongAdder longAdder = new LongAdder();

    private ConcurrentHashMap<Integer,Disposable> concurrentHashMap = new ConcurrentHashMap<>(); //

    private ConcurrentHashMap<Integer, KcpTransportMessage> qos2Message = new ConcurrentHashMap<>();


    public <T> Flux<T> receive(Class<T> tClass){
        return  inbound.receive().cast(tClass);
    }

    public KcpTransportConnection(Connection connection){
        this.connection=connection;
        this.inbound=connection.inbound();
        this.outbound=connection.outbound();
    }


    public void destory() {
        concurrentHashMap.values().forEach(Disposable::dispose);
        concurrentHashMap.clear();
        qos2Message.clear();
    }


    public Mono<Void> write(Object object){
      log.info("write:"+object);
      return outbound.sendObject(object).then().doOnError(Throwable::printStackTrace);
    }


    public Mono<Void> write(byte[] data){
        log.info("write:"+ data);
        KcpTransportMessage message = KcpTransportMessage.builder().message(data).build();
        return outbound.sendObject(message).then().doOnError(Throwable::printStackTrace);
    }


    public Mono<Void> sendPingReq(){
        return outbound.sendObject(new KcpTransportMessage("ping".getBytes(StandardCharsets.UTF_8)).toByteBuf()).then();
    }


    public Mono<Void> sendPingRes(){
        return outbound.sendObject(new KcpTransportMessage("pong".getBytes(StandardCharsets.UTF_8)).toByteBuf()).then();
    }


    public int messageId(){
        longAdder.increment();
        int value=longAdder.intValue();
        if(value==Integer.MAX_VALUE){
            longAdder.reset();
            longAdder.increment();
            return longAdder.intValue();
        }
        return value;
    }



    public  void  saveQos2Message(Integer messageId, KcpTransportMessage message){
        qos2Message.put(messageId,message);
    }


    public Optional<KcpTransportMessage>  getAndRemoveQos2Message(Integer messageId){
        KcpTransportMessage message  = qos2Message.get(messageId);
        qos2Message.remove(messageId);
        return Optional.ofNullable(message);
    }

    public  boolean  containQos2Message(Integer messageId,byte[] bytes){
       return qos2Message.containsKey(messageId);
    }



    public  void  addDisposable(Integer messageId,Disposable disposable){
        concurrentHashMap.put(messageId,disposable);
    }


    public  void  cancelDisposable(Integer messageId){
        Optional.ofNullable(concurrentHashMap.get(messageId))
                .ifPresent(dispose->dispose.dispose());
        concurrentHashMap.remove(messageId);
    }



    @Override
    public void dispose() {
        connection.dispose();
    }

    public boolean isDispose(){
        return connection.isDisposed();
    }

    public  Mono<Void> sendMessage(byte[] message){
        return this.write(new KcpTransportMessage(message));
    }

//    public   Mono<Void> sendMessageRetry(boolean isDup, MqttQoS qoS, boolean isRetain, String topic, byte[] message){
//        int id = this.messageId();
//        this.addDisposable(id, Mono.fromRunnable(() ->
//                this.write(MqttMessageApi.buildPub(isDup,qoS,isRetain,id,topic, Unpooled.wrappedBuffer(message))).subscribe())
//                .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe()); // retry
//        MqttPublishMessage publishMessage = MqttMessageApi.buildPub(isDup,qoS,isRetain,id,topic, Unpooled.wrappedBuffer(message)); // pub
//       return this.write(publishMessage);
//    }


}

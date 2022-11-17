package test;

import net.client.KcpClientSession;
import net.client.KcpTransportClient;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author: xuzefan
 * @date: 2022/11/17 15:10
 */
public class ClientTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KcpClientSession clientSession= KcpTransportClient.create("127.0.0.1",18884)
                .log(true)
                .clientId("Comsumer_1")
                .exception(throwable -> System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+throwable))
                .messageAcceptor((topic,msg)->{
                    try {
                        System.out.println(topic+":"+ new String(msg));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .connect()
                .block();
        Thread.sleep(5000);
        clientSession.send("hello world".getBytes(StandardCharsets.UTF_8)).subscribe();
        clientSession.send("hello world".getBytes(StandardCharsets.UTF_8)).subscribe();
        clientSession.send("hello world".getBytes(StandardCharsets.UTF_8)).subscribe();
        clientSession.send("hello world".getBytes(StandardCharsets.UTF_8)).subscribe();
        System.out.println(1111);
        latch.await();
    }

}

package test;

import net.base.KcpTransportConnection;
import net.server.KcpServerSession;
import net.server.KcpTransportServer;


import java.util.concurrent.CountDownLatch;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:22
 */
public class ServerTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KcpServerSession serverSession= KcpTransportServer.create("127.0.0.1",18884)
                .log(true)
                .exception(throwable -> System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+throwable))
                .onConnected(connection -> {
                    System.out.println("有连接进来" + Thread.currentThread().getName() + connection.getLongAdder());
                })
                .onReceive((kcpTransportMessage, connection) -> {
                    System.out.println("server access message: " + new String(kcpTransportMessage.getMessage()));
                })
                .onClose(connection -> {
                    System.out.println("有连接断开" + Thread.currentThread().getName() + connection.getLongAdder());
                })
                .start()
                .block();
        latch.await();
    }

}

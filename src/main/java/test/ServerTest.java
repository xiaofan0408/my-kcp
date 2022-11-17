package test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import net.base.KcpTransportConnection;
import net.server.KcpServerSession;
import net.server.KcpTransportServer;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:22
 */
public class ServerTest {

    public static void main(String[] args) throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        KcpServerSession serverSession= KcpTransportServer.create("127.0.0.1",18884)
//                .log(true)
//                .exception(throwable -> System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+throwable))
//                .onConnected(connection -> {
//                    System.out.println("有连接进来" + Thread.currentThread().getName() + connection.getLongAdder());
//                })
//                .onReceive((kcpTransportMessage, connection) -> {
//                    System.out.println("server access message: " + new String(kcpTransportMessage.getMessage()));
//                    connection.write("client hello".getBytes(StandardCharsets.UTF_8));
//                })
//                .onClose(connection -> {
//                    System.out.println("有连接断开" + Thread.currentThread().getName() + connection.getLongAdder());
//                })
//                .start()
//                .block();
//        serverSession
//                .onDispose()
//                .block();
//        latch.await();
        Connection server =UdpServer.create()
                .host("127.0.0.1")
                .port(18884)
                .handle((in, out) ->
                        out.sendObject(
                                in.receiveObject()
                                        .map(o -> {
                                            if (o instanceof DatagramPacket) {
                                                DatagramPacket p = (DatagramPacket) o;
                                                ByteBuf buf = Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8);
                                                return new DatagramPacket(buf, p.sender());
                                            }
                                            else {
                                                return Mono.error(new Exception("Unexpected type of the message: " + o));
                                            }
                                        })))
                .bindNow(Duration.ofSeconds(30));

        server.onDispose()
                .block();

    }

}

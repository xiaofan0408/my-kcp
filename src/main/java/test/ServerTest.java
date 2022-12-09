package test;


import net.server.KcpTransportServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:22
 */
public class ServerTest {

    static final Logger logger = LoggerFactory.getLogger(ServerTest.class);


    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KcpTransportServer.create("127.0.0.1",18884)
                .log(true)
                .exception(throwable -> System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+throwable))
                .onConnected(connection -> {
                    System.out.println("有连接进来" + Thread.currentThread().getName() + connection.getLongAdder());
                })
                .onReceive((kcpTransportMessage, connection) -> {
                    System.out.println("server access message: " + new String(kcpTransportMessage.getMessage()));
                    connection.write("client hello".getBytes(StandardCharsets.UTF_8));
                })
                .onClose(connection -> {
                    System.out.println("有连接断开" + Thread.currentThread().getName() + connection.getLongAdder());
                })
                .start()
                .doOnNext(kcpServerSession -> System.out.println("Server started on the address : "))
                .block()
                .bind()
                .onDispose().block();
        System.out.println(1111);

        latch.await();

//        CountDownLatch latch = new CountDownLatch(1);
//        KcpServerConfig kcpServerConfig = new KcpServerConfig();
//        kcpServerConfig.setOnConnected(connection -> {
//                    System.out.println("有连接进来" + Thread.currentThread().getName() + connection.getLongAdder());
//                });
//        kcpServerConfig.setOnReceive((kcpTransportMessage, connection) -> {
//                    System.out.println("server access message: " + new String(kcpTransportMessage.getMessage()));
//                    connection.write("client hello".getBytes(StandardCharsets.UTF_8));
//                });
//        kcpServerConfig.setOnClose(connection -> {
//                    System.out.println("有连接断开" + Thread.currentThread().getName() + connection.getLongAdder());
//                });
//
//
//        KcpServerConn udpServerTransport = UdpServer.create()
//                .host("127.0.0.1")
//                .port(18884)
//                .bind()
//                .map(KcpTransportConnection::new)
//                .map(c -> new KcpServerConn(kcpServerConfig,c))
//                .block();
//
//        System.out.println(11111);
//        udpServerTransport.bind().onDispose();
//        latch.await();
    }

}

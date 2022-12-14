package net.server;

import lombok.Data;
import net.base.KcpConfiguration;
import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author: xuzefan
 * @date: 2022/11/17 10:48
 */
@Data
public class KcpServerConfig implements KcpConfiguration {

    private String ip;

    private Integer port;

    private boolean isLog;

    Consumer<Throwable> throwableConsumer;

    Consumer<KcpTransportConnection> onConnected = (connection)->{};

    BiConsumer<KcpTransportMessage,KcpTransportConnection> onReceive = (message,connection)->{};

    Consumer<KcpTransportConnection> onClose = (connection)->{};

    public void checkConfig() {
    }
}

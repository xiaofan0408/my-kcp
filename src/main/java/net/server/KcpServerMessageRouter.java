package net.server;

import lombok.extern.slf4j.Slf4j;
import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:14
 */
@Slf4j
public class KcpServerMessageRouter {

    private KcpServerConfig config;

    public KcpServerMessageRouter(KcpServerConfig config) {
        this.config = config;
    }

    public void handler(KcpTransportMessage message, KcpTransportConnection connection) {
       this.config.onReceive.accept(message,connection);
    }
}

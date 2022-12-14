package net.client;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.base.KcpTransportConnection;
import net.base.KcpTransportMessage;

@Getter
@Slf4j
public class KcpClientMessageRouter {


    private final KcpClientConfig config;

    public KcpClientMessageRouter(KcpClientConfig config) {
        this.config=config;
    }

    public void handler(KcpTransportMessage message, KcpTransportConnection connection) {

        log.info("accept message  info{}",message);
//        handler.handler(message,connection,config);

    }

}

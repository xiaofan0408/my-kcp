package net.utils;

import io.netty.util.AttributeKey;
import lombok.experimental.UtilityClass;
import net.base.KcpTransportConnection;
import net.client.KcpClientSession;
import net.server.KcpServerSession;
import reactor.core.Disposable;

@UtilityClass
public class AttributeKeys {

    public AttributeKey<KcpClientSession> clientConnectionAttributeKey = AttributeKey.valueOf("client_operation");

    public AttributeKey<KcpServerSession> serverConnectionAttributeKey = AttributeKey.valueOf("server_operation");

    public AttributeKey<Disposable> closeConnection = AttributeKey.valueOf("close_connection");

    public AttributeKey<KcpTransportConnection> connectionAttributeKey = AttributeKey.valueOf("transport_connection");

    public AttributeKey<String> device_id = AttributeKey.valueOf("device_id");

}

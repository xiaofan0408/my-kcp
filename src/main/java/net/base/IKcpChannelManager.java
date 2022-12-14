package net.base;

import java.util.List;

public interface IKcpChannelManager {

    List<KcpTransportConnection> getConnections();

    void  addConnections(KcpTransportConnection connection);

    void removeConnections(KcpTransportConnection connection);

    void addDeviceId(String deviceId, KcpTransportConnection connection);

    void removeDeviceId(String deviceId);

    KcpTransportConnection getRemoveDeviceId(String deviceId);

    boolean checkDeviceId(String deviceId);
}

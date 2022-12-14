package net.base.impl;

import net.base.IKcpChannelManager;
import net.base.KcpTransportConnection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:04
 */
public class KcpChannelManager implements IKcpChannelManager {

    private CopyOnWriteArrayList<KcpTransportConnection> connections = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap<String,KcpTransportConnection> connectionMap = new ConcurrentHashMap<>();

    @Override
    public List<KcpTransportConnection> getConnections() {
        return connections;
    }

    @Override
    public void addConnections(KcpTransportConnection connection) {
        connections.add(connection);
    }

    @Override
    public void removeConnections(KcpTransportConnection connection) {
        connections.remove(connection);
    }

    @Override
    public void addDeviceId(String deviceId, KcpTransportConnection connection) {
        connectionMap.put(deviceId,connection);
    }

    @Override
    public void removeDeviceId(String deviceId) {
        connectionMap.remove(deviceId);
    }

    @Override
    public KcpTransportConnection getRemoveDeviceId(String deviceId) {
        return connectionMap.remove(deviceId);
    }

    @Override
    public boolean checkDeviceId(String deviceId) {
        return connectionMap.containsKey(deviceId);
    }
}

package net.base;


import java.util.function.Consumer;

public interface KcpConfiguration {
    String getIp();

    Integer getPort();

    boolean isLog();

    Consumer<Throwable> getThrowableConsumer();
}

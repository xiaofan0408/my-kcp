package net.client;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.base.KcpConfiguration;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author: xuzefan
 * @date: 2022/11/17 14:37
 */
@Setter
public class KcpClientConfig implements KcpConfiguration {

    private String ip;

    private int  port;

    private int heart = 30;

    private boolean log;


    private Options options = new Options();

    private Consumer<Throwable> throwableConsumer;

    private BiConsumer<String,byte[]> messageAcceptor;

    private Runnable onClose = ()->{};


    public void checkConfig() {
        Objects.requireNonNull(ip,"ip is not null");
        Objects.requireNonNull(port,"port is not null");
        Objects.requireNonNull(options.getClientIdentifier(),"clientIdentifier is not null");
        if(options.isHasWillFlag()){
            Objects.requireNonNull(options.getWillMessage(),"willMessage is not null");
            Objects.requireNonNull(options.getWillQos(),"willQos is not null");
            Objects.requireNonNull(options.getWillTopic(),"willTopic is not null");
        }
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public boolean isLog() {
        return log;
    }

    @Override
    public Consumer<Throwable> getThrowableConsumer() {
        return throwableConsumer;
    }

    public int getHeart() {
        return heart;
    }

    public Options getOptions() {
        return options;
    }

    public BiConsumer<String, byte[]> getMessageAcceptor() {
        return messageAcceptor;
    }

    public Runnable getOnClose() {
        return onClose;
    }

    @Getter
    @Setter
    @ToString
    public class Options{

        private  String clientIdentifier;

        private  String willTopic;

        private  String willMessage;

        private  String userName;

        private  String password;

        private  boolean hasUserName;

        private  boolean hasPassword;

        private  boolean hasWillRetain;

        private  int willQos;

        private  boolean hasWillFlag;

        private  boolean hasCleanSession;


    }
}

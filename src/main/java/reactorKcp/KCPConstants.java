package reactorKcp;

/**
 * @author: xuzefan
 * @date: 2022/11/14 11:03
 */
public class KCPConstants {

    public static final int IKCP_RTO_NDL = 30;   // no delay min rto
    public static final int IKCP_RTO_MIN = 100;  // normal min rto
    public static final int IKCP_RTO_DEF = 200;
    public static final int IKCP_RTO_MAX = 60000;
    public static final int IKCP_CMD_PUSH = 81;  // cmd: push data
    public static final int IKCP_CMD_ACK = 82;   // cmd: ack
    public static final int IKCP_CMD_WASK = 83;  // cmd: window probe (ask)
    public static final int IKCP_CMD_WINS = 84;  // cmd: window size (tell)
    public static final int IKCP_ASK_SEND = 1;   // need to send IKCP_CMD_WASK
    public static final int IKCP_ASK_TELL = 2;   // need to send IKCP_CMD_WINS
    public static final int IKCP_WND_SND = 32;
    public static final int IKCP_WND_RCV = 128;  // must >= max fragment size
    public static final int IKCP_MTU_DEF = 1400;
    public static final int IKCP_ACK_FAST = 3;
    public static final int IKCP_INTERVAL = 100;
    public static final int IKCP_OVERHEAD = 24;
    public static final int IKCP_DEADLINK = 10;
    public static final int IKCP_THRESH_INIT = 2;
    public static final int IKCP_THRESH_MIN = 2;
    public static final int IKCP_PROBE_INIT = 7000;    // 7 secs to probe window size
    public static final int IKCP_PROBE_LIMIT = 120000; // up to 120 secs to probe window


    public static final int IKCP_LOG_OUTPUT    = 1;
    public static final int IKCP_LOG_INPUT     = 2;
    public static final int IKCP_LOG_SEND      = 4;
    public static final int IKCP_LOG_RECV      = 8;
    public static final int IKCP_LOG_IN_DATA   = 16;
    public static final int IKCP_LOG_IN_ACK    = 32;
    public static final int IKCP_LOG_IN_PROBE  = 64;
    public static final int IKCP_LOG_IN_WINS   = 128;
    public static final int IKCP_LOG_OUT_DATA  = 256;
    public static final int IKCP_LOG_OUT_ACK   = 512;
    public static final int IKCP_LOG_OUT_PROBE = 1024;
    public static final int IKCP_LOG_OUT_WINS  = 2048;

}

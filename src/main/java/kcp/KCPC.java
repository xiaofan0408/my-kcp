//package kcp;
//
//import java.util.*;
//
//public abstract class KCPC {
//
//    //参考 https://github.com/hkspirt/kcp-java进行修改。
//    //更注重贴近原版kcp，作为java基础版本对照
//
//    //=====================================================================
//    // KCP BASIC
//    //=====================================================================
//    public final int IKCP_RTO_NDL = 30;   // no delay min rto
//    public final int IKCP_RTO_MIN = 100;  // normal min rto
//    public final int IKCP_RTO_DEF = 200;
//    public final int IKCP_RTO_MAX = 60000;
//    public final int IKCP_CMD_PUSH = 81;  // cmd: push data
//    public final int IKCP_CMD_ACK = 82;   // cmd: ack
//    public final int IKCP_CMD_WASK = 83;  // cmd: window probe (ask)
//    public final int IKCP_CMD_WINS = 84;  // cmd: window size (tell)
//    public final int IKCP_ASK_SEND = 1;   // need to send IKCP_CMD_WASK
//    public final int IKCP_ASK_TELL = 2;   // need to send IKCP_CMD_WINS
//    public final int IKCP_WND_SND = 32;
//    public final int IKCP_WND_RCV = 128;  // must >= max fragment size
//    public final int IKCP_MTU_DEF = 1400;
//    public final int IKCP_ACK_FAST = 3;
//    public final int IKCP_INTERVAL = 100;
//    public final int IKCP_OVERHEAD = 24;
//    public final int IKCP_DEADLINK = 10;
//    public final int IKCP_THRESH_INIT = 2;
//    public final int IKCP_THRESH_MIN = 2;
//    public final int IKCP_PROBE_INIT = 7000;    // 7 secs to probe window size
//    public final int IKCP_PROBE_LIMIT = 120000; // up to 120 secs to probe window
//
//
//    public final int IKCP_LOG_OUTPUT    = 1;
//    public final int IKCP_LOG_INPUT     = 2;
//    public final int IKCP_LOG_SEND      = 4;
//    public final int IKCP_LOG_RECV      = 8;
//    public final int IKCP_LOG_IN_DATA   = 16;
//    public final int IKCP_LOG_IN_ACK    = 32;
//    public final int IKCP_LOG_IN_PROBE  = 64;
//    public final int IKCP_LOG_IN_WINS   = 128;
//    public final int IKCP_LOG_OUT_DATA  = 256;
//    public final int IKCP_LOG_OUT_ACK   = 512;
//    public final int IKCP_LOG_OUT_PROBE = 1024;
//    public final int IKCP_LOG_OUT_WINS  = 2048;
//
//    protected abstract int output(byte[] buffer, int size); // 需具体实现
//
//    //采用小端编码： https://github.com/skywind3000/kcp/issues/53
//    /** encode 8 bits unsigned int*/
//    public static void ikcp_encode8u(byte[] p, int offset, byte c) {
//        p[0 + offset] = c;
//    }
//
//    /** decode 8 bits unsigned int*/
//    public static byte ikcp_decode8u(byte[] p, int offset) {
//        return p[0 + offset];
//    }
//
//    /** encode 16 bits unsigned int (msb) */
//    public static void ikcp_encode16u(byte[] p, int offset, int w) {
//        p[offset + 0] = (byte)(w & 0xff);
//        p[offset + 1] = (byte)((w >>> 8) & 0xff);
//    }
//
//    /** decode 16 bits unsigned int (msb) */
//    public static int ikcp_decode16u(byte[] p, int offset) {
//        int x1 = ((int)p[offset + 0]) & 0xff;
//        int x2 = ((int)p[offset + 1]) & 0xff;
//        return ((x2 << 8) | x1) & 0xffff;
//    }
//
//    /** encode 32 bits unsigned int (msb) */
//    public static void ikcp_encode32u(byte[] p, int offset, long l) {
//        p[offset + 0] = (byte)(l & 0xff);
//        p[offset + 1] = (byte)((l >>> 8) & 0xff);
//        p[offset + 2] = (byte)((l >>> 16) & 0xff);
//        p[offset + 3] = (byte)((l >>> 24) & 0xff);
//    }
//
//    /** decode 32 bits unsigned int (msb) */
//    public static long ikcp_decode32u(byte[] p, int offset) {
//        int x1 = ((int)p[offset + 0]) & 0xff;
//        int x2 = ((int)p[offset + 1]) & 0xff;
//        int x3 = ((int)p[offset + 2]) & 0xff;
//        int x4 = ((int)p[offset + 3]) & 0xff;
//        int x5 = (x1) | (x2 << 8) | (x3 << 16) | (x4 << 24);
//        return ((long)x5) & 0xffffffff;
//    }
//
//    static long _imin_(long a, long b) {
//        return a <= b ? a : b;
//    }
//
//    static long _imax_(long a, long b) {
//        return a >= b ? a : b;
//    }
//
//    static long _ibound_(long lower, long middle, long upper) {
//        return _imin_(_imax_(lower, middle), upper);
//    }
//
//    static int _itimediff(long later, long earlier) {
//        return ((int) (later - earlier));
//    }
//
//    // write log
//    void ikcp_log(int mask, String fmt, Object... args)
//    {
//        if ((mask  & this.logmask) == 0)
//            return;
//        String str = String.format(fmt, args);
//        this.writelog(str, user);
//    }
//
//    // check log mask
//    int ikcp_canlog(int mask)
//    {
//        if ((mask & this.logmask) == 0)
//            return 0;
//        return 1;
//    }
//
//    // output segment
//    int ikcp_output(byte[] data,int size)
//    {
//        if (ikcp_canlog(IKCP_LOG_OUTPUT) != 0) {
//            ikcp_log(IKCP_LOG_OUTPUT, "[RO] %ld bytes", (long)data.length);
//        }
//        if (data.length == 0)
//            return 0;
//        return this.output(data, size);
//    }
//
//    // output queue
//    void ikcp_qprint(String name, List<Segment> list)
//    {
//        System.out.printf("<%s>: [", name);
//        for (Segment seg : list) {
//            System.out.printf("(%lu %d)", (long)seg.sn, (int)(seg.ts % 10000));
//            System.out.printf(",");
//        }
//        System.out.printf("]\n");
//    }
//
//    // can be override
//    void writelog(String str, Object user){
//        System.out.println(str+", user:"+user);
//    }
//
//    private class Segment {
//
//        protected long conv = 0;
//        protected long cmd = 0;
//        protected long frg = 0;
//        protected long wnd = 0;
//        protected long ts = 0;
//        protected long sn = 0;
//        protected long una = 0;
//        protected long resendts = 0;
//        protected long rto = 0;
//        protected long fastack = 0;
//        protected long xmit = 0;
//        protected byte[] data;
//
//        protected Segment(int size) {
//            this.data = new byte[size];
//        }
//
//        /**---------------------------------------------------------------------
//        * ikcp_encode_seg
//        *
//        * encode a segment into buffer
//        * ---------------------------------------------------------------------
//        */
//        protected int encode(byte[] ptr, int offset) {
//            int offset_ = offset;
//
//            ikcp_encode32u(ptr, offset, conv);
//            offset += 4;
//            ikcp_encode8u(ptr, offset, (byte) cmd);
//            offset += 1;
//            ikcp_encode8u(ptr, offset, (byte) frg);
//            offset += 1;
//            ikcp_encode16u(ptr, offset, (int) wnd);
//            offset += 2;
//            ikcp_encode32u(ptr, offset, ts);
//            offset += 4;
//            ikcp_encode32u(ptr, offset, sn);
//            offset += 4;
//            ikcp_encode32u(ptr, offset, una);
//            offset += 4;
//            ikcp_encode32u(ptr, offset, (long) data.length);
//            offset += 4;
//
//            return offset - offset_;
//        }
//    }
//
//
//    long conv, mtu, mss, state;
//    long snd_una, snd_nxt, rcv_nxt;
//    long ts_recent, ts_lastack, ssthresh;
//    long rx_rttval, rx_srtt, rx_rto, rx_minrto;
//    long snd_wnd, rcv_wnd, rmt_wnd, cwnd, probe;
//    long current, interval, ts_flush, xmit;
////    long nrcv_buf, nsnd_buf;
////    long nrcv_que, nsnd_que;
//    long nodelay, updated;
//    long ts_probe, probe_wait;
//    long dead_link, incr;
//    Deque<Segment> snd_queue;
//    Deque<Segment> rcv_queue;
//    Deque<Segment> snd_buf;
//    Deque<Segment> rcv_buf;
//    List<Long> acklist;
//    //long ackcount = 0;    //用于计算 acklist 当前长度和可容纳长度的，java不需要
//    //long ackblock = 0;
//    public Object user;
//    byte[] buffer;
//    long fastresend;
//    long nocwnd, stream;
//    int logmask = 0;
//    //long ikcp_output = NULL;
//    //long writelog = NULL;
//
//    public KCPC(long conv, Object user) {
//        this.conv = conv;
//        this.user = user;
//        this.snd_una = 0;
//        this.snd_nxt = 0;
//        this.rcv_nxt = 0;
//        this.ts_recent = 0;
//        this.ts_lastack = 0;
//        this.ts_probe = 0;
//        this.probe_wait = 0;
//        this.snd_wnd = IKCP_WND_SND;
//        this.rcv_wnd = IKCP_WND_RCV;
//        this.rmt_wnd = IKCP_WND_RCV;
//        this.cwnd = 0;
//        this.incr = 0;
//        this.probe = 0;
//        this.mtu = IKCP_MTU_DEF;
//        this.mss = this.mtu - IKCP_OVERHEAD;
//        this.stream = 0;
//
//        this.buffer = new byte[(int) (mtu + IKCP_OVERHEAD) * 3];
//
//        this.snd_queue = new LinkedList<>();
//        this.rcv_queue = new LinkedList<>();
//        this.snd_buf = new LinkedList<>();
//        this.rcv_buf = new LinkedList<>();
////        this.nrcv_buf = 0;
////        this.nsnd_buf = 0;
////        this.nrcv_que = 0;
////        this.nsnd_que = 0;
//        this.state = 0;
//        this.acklist = new ArrayList<>(8);
//        //this.ackcount = 0;
//        //this.ackblock = 0;
//        this.rx_srtt = 0;
//        this.rx_rttval = 0;
//        this.rx_rto = IKCP_RTO_DEF;
//        this.rx_minrto = IKCP_RTO_MIN;
//        this.current = 0;
//        this.interval = IKCP_INTERVAL;
//        this.ts_flush = IKCP_INTERVAL;
//        this.nodelay = 0;
//        this.updated = 0;
//        this.logmask = 0;
//        this.ssthresh = IKCP_THRESH_INIT;
//        this.fastresend = 0;
//        this.nocwnd = 0;
//        this.xmit = 0;
//        this.dead_link = IKCP_DEADLINK;
//        //this.ikcp_output = NULL;
//        //this.writelog = NULL;
//    }
//
//    /**---------------------------------------------------------------------
//     * user/upper level recv: returns size, returns below zero for EAGAIN
//     * 将接收队列中的数据传递给上层引用
//     * ---------------------------------------------------------------------
//     */
//    public int Recv(byte[] buffer, int len) {
//
//        int ispeek = (len < 0)? 1 : 0;
//        if (len < 0)
//            len = -len;
//
//        if (0 == rcv_queue.size()) {
//            return -1;
//        }
//
//        int peekSize = PeekSize();
//        if (0 > peekSize) {
//            return -2;
//        }
//
//        if (peekSize > len) {
//            return -3;
//        }
//
//        boolean recover = false;
//
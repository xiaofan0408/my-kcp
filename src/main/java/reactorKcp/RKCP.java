//package reactorKcp;
//
//
//
//import kcp.KCPC;
//
//import java.util.*;
//
//
//import static reactorKcp.KCPConstants.*;
//
///**
// * @author: xuzefan
// * @date: 2022/11/14 10:21
// */
//public abstract class RKCP {
//
//    /*  conv: 会话编号
//        mtu: 最大传输单元
//        mss: 最大报文长度
//        state: 此会话是否有效 (0: 有效 ~0:无效)
//    */
//    private long conv, mtu, mss, state;
//    /*  snd_una: 发送的未确认数据段序号
//        snd_nxt: 发送的下一个数据段序号
//        rcv_nxt: 期望接收到的下一个数据段的序号
//    */
//    private long snd_una, snd_nxt, rcv_nxt;
//    /*  ts_recent: (弃用字段?)
//        ts_lastack: (弃用字段?)
//        ssthresh: 慢启动阈值 (slow start threshold)
//    */
//    private long ts_recent, ts_lastack, ssthresh;
//    /*  rx_rttval: 平滑网络抖动时间
//        rx_srtt: 平滑往返时间
//        rx_rto: 重传超时时间
//        rx_minrto: 最小重传超时时间
//    */
//    private long rx_rttval, rx_srtt, rx_rto, rx_minrto;
//    /*  snd_wnd: 发送窗口大小
//        rcv_wnd: 接收窗口大小
//        rmt_wnd: 远端窗口大小
//        cwnd: 拥塞窗口 (congestion window)
//        probe: 窗口探测标记位，在 flush 时发送特殊的探测包 (window probe)
//    */
//    private long snd_wnd, rcv_wnd, rmt_wnd, cwnd, probe;
//    /*  current: 当前时间 (ms)
//        interval: 内部时钟更新周期
//        ts_flush: 期望的下一次 update/flush 时间
//        xmit: 全局重传次数计数
//    */
//    private long current, interval, ts_flush, xmit;
//    /*  nrcv_buf: rcv_buf 接收缓冲区长度
//        nsnd_buf: snd_buf 发送缓冲区长度
//        nrcv_que: rcv_queue 接收队列长度
//        nsnd_que: snd_queue 发送队列长度
//    */
//    private long nrcv_buf, nsnd_buf;
//    private long nrcv_que, nsnd_que;
//    /*  nodelay: nodelay模式 (0:关闭 1:开启)
//        updated: 是否调用过 update 函数
//    */
//    private long nodelay, updated;
//    /*  ts_probe: 窗口探测标记位
//        probe_wait: 零窗口探测等待时间，默认 7000 (7秒)
//    */
//    private long ts_probe, probe_wait;
//    /*  dead_link: 死链接条件，默认为 20。
//        (单个数据段重传次数到达此值时 kcp->state 会被设置为 UINT_MAX)
//        incr: 拥塞窗口算法的一部分
//    */
//    private long dead_link, incr;
//    /* 发送队列 */
//    private Deque<Segment> snd_queue;
//    /* 接收队列 */
//    private Deque<Segment> rcv_queue;
//    /* 发送缓冲区 */
//    private Deque<Segment> snd_buf;
//    /* 接收缓冲区 */
//    private Deque<Segment> rcv_buf;
//    /* 确认列表, 包含了序号和时间戳对(pair)的数组元素*/
//    private List<Long> acklist;
//    /* 确认列表元素数量 */
////    long ackcount;
//    /* 确认列表实际分配长度 */
////    long ackblock;
//    /* 用户数据指针，传入到回调函数中 */
//    private Object user;
//    /* 临时缓冲区 */
//    private byte[] buffer;
//    /* 是否启用快速重传，0:不开启，1:开启 */
//    int fastresend;
//    /* 快速重传最大次数限制，默认为 5*/
//    int fastlimit;
//    /*  nocwnd: 控流模式，0关闭，1不关闭
//        stream: 流模式, 0包模式 1流模式
//    */
//    int nocwnd, stream;
//    /* 日志标记 */
//    int logmask;
//
//    /* 发送回调 */
//    protected abstract int output(byte[] buffer, int size); // 需具体实现
//
//    /* 日志回调 */
//    void writeLog(String str, Object user) {
//        System.out.println(str + ", user:" + user);
//    }
//
//
//    /**
//     * nodelay :是否启用 nodelay模式，0不启用；1启用。
//     * interval :协议内部工作的 interval，单位毫秒，比如 10ms或者 20ms
//     * resend :快速重传模式，默认0关闭，可以设置2（2次ACK跨越将会直接重传）
//     * nc :是否关闭流控，默认是0代表不关闭，1代表关闭。
//     * 普通模式: (0, 40, 0, 0);
//     * 极速模式: (1, 10, 2, 1);
//     */
//    int noDelay(int nodelay, int interval, int resend, int nc) {
//        if (nodelay >= 0) {
//            this.nodelay = nodelay;
//            if (nodelay != 0) {
//                this.rx_minrto = KCPConstants.IKCP_RTO_NDL;
//            } else {
//                this.rx_minrto = KCPConstants.IKCP_RTO_MIN;
//            }
//        }
//        if (interval >= 0) {
//            if (interval > 5000) interval = 5000;
//            else if (interval < 10) interval = 10;
//            this.interval = interval;
//        }
//        if (resend >= 0) {
//            this.fastresend = resend;
//        }
//        if (nc >= 0) {
//            this.nocwnd = nc;
//        }
//        return 0;
//    }
//
//    /**
//     * 发送窗口大小 sndwnd 必须大于 0，接收窗口大小 rcvwnd 必须大于 128，单位为包，而非字节。
//     *
//     * @param sndwnd
//     * @param rcvwnd
//     * @return
//     */
//    int wndSize(int sndwnd, int rcvwnd) {
//        if (sndwnd > 0) {
//            this.snd_wnd = sndwnd;
//        }
//        if (rcvwnd > 0) {   // must >= max fragment size
//            this.rcv_wnd = KCPUtils._imax_(rcvwnd, IKCP_WND_RCV);
//        }
//        return 0;
//    }
//
//    public RKCP(long conv, Object user) {
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
//    }
//
//    public int send(byte[] sendData) {
//        assert(mss > 0);
//        if (sendData.length == 0) {
//            return -1;
//        }
//        int count;
//        int offset = 0;
//        int len = buffer.length;
//        if (stream != 0) {
//            //todo 流模式
//        }
//        if (len <= mss) {
//            count = 1;
//        } else {
//            count = (int)((len + mss -1) / mss);
//        }
//        if (count >= (int)IKCP_WND_RCV) return -2;
//        if (count == 0) count = 1;
//        // 分包
//        for (int i = 0; i < count; i++) {
//            // 计算包的数据长度，并分配对应的 seg 结构
//            int size = (int)(len > mss ? mss : len);
//            Segment segment = new Segment(size);
//            if (buffer != null && len > 0) {
//                System.arraycopy(buffer, offset, segment.getData(), 0, size);
//            }
//            segment.setLen(size);
//            segment.setFrg(stream == 0 ? (count - i - 1): 0);
//            // 加到 snd_queue 的末尾，nsnd_qua 加一
//            this.snd_queue.addLast(segment);
//            this.nsnd_que++;
//            offset += size;
//            len -= size;
//        }
//
//        return 0;
//
//    }
//
//    private void Update(long current){
//        int slap;
//        this.current = current;
//        // 首次调用
//        if (this.updated == 0) {
//            this.updated = 1;
//            this.ts_flush = this.current;
//        }
//        slap = KCPUtils._itimediff(this.current,this.ts_flush);
//        if (slap >= 10000 || slap < -10000) {
//            this.ts_flush = this.current;
//            slap = 0;
//        }
//        if (slap >= 0) {
//            // 下次 flush 的时间
//            this.ts_flush += this.interval;
//            if (KCPUtils._itimediff(this.current,this.ts_flush) >= 0) {
//                this.ts_flush = this.current + this.interval;
//            }
//            flush();
//        }
//    }
//
//
//    public void flush() {
//        long current_ = current;
//        int change = 0;
//        int lost = 0;
//
//        // 'ikcp_update' haven't been called.
//        if (0 == updated) {
//            return;
//        }
//
//        //
//        Segment segment = new Segment(0);
//        segment.setConv(this.conv);
//        segment.setCmd(IKCP_CMD_ACK);
//        segment.setWnd(wndUnUsed());  // seg.wnd 是表示当前可接收窗口大小
//        segment.setUna(this.rcv_nxt);
//
//        // flush acknowledges
//        // 将acklist中的ack发送出去
//        int count = acklist.size() / 2;
//        int size = 0;
//        for (int i = 0; i < count; i++) {
//            if (size + IKCP_OVERHEAD > mtu) {
//                kcpOutPut(buffer, size);
//                size = 0;
//            }
//            // ikcp_ack_get
//            segment.setSn(acklist.get(i * 2 + 0));
//            segment.setTs(acklist.get(i * 2 + 1));
//            size += segment.encode(buffer, size);
//        }
//        acklist.clear();
//
//    }
//
//    /**
//     * 未使用的窗口
//     * @return
//     */
//    private  long wndUnUsed() {
//        if (rcv_queue.size() < rcv_wnd) {
//            return (int) (int) rcv_wnd - rcv_queue.size();
//        }
//        return 0;
//    }
//
//    private int kcpOutPut(byte[] buffer, int size){
//        if (kcpCanlog(IKCP_LOG_OUTPUT) != 0) {
//            kcpLog(IKCP_LOG_OUTPUT, "[RO] %ld bytes", (long)buffer.length);
//        }
//        if (buffer.length == 0)
//            return 0;
//        return this.output(buffer, size);
//    }
//
//    int kcpCanlog(int mask) {
//        if ((mask & this.logmask) == 0)
//            return 0;
//        return 1;
//    }
//
//    void kcpLog(int mask, String fmt, Object... args) {
//        if ((mask 
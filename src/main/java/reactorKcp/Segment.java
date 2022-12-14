package reactorKcp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: xuzefan
 * @date: 2022/11/14 10:23
 */
@Getter
@Setter
public class Segment {
    /* 队列节点，IKCPSEG 作为一个队列元素，此结构指向了队列后前后元素 */
//    struct IQUEUEHEAD node;
    /* 会话编号 */
    private long conv;
    /* 指令类型 */
    private long cmd;
    /* 分片号 (fragment)
       发送数据大于 MSS 时将被分片，0为最后一个分片.
       意味着数据可以被recv，如果是流模式，所有分片号都为0
    */
    private long frg;
    /* 窗口大小 */
    private long wnd;
    /* 时间戳 */
    private long ts;
    /* 序号 (sequence number) */
    private long sn;
    /* 未确认的序号 (unacknowledged) */
    private long una;
    /* 数据长度 */
    private long len;
    /* 重传时间 (resend timestamp) */
    private long resendts;
    /* 重传的超时时间 (retransmission timeout) */
    private long rto;
    /* 快速确认计数 (fast acknowledge) */
    private long fastack;
    /* 发送次数 (transmit) */
    private long xmit;
    /* 数据内容 */
    private byte[] data;

    public Segment(int size){
        this.data = new byte[size];
    }

    public int encode(byte[] ptr, int offset) {
        int offset_ = offset;

        /* 会话编号 (4 Bytes) */
        KCPUtils.encode32u(ptr, offset, conv);
        offset += 4;
        /* 指令类型 (1 Bytes) */
        KCPUtils.encode8u(ptr, offset, (byte) cmd);
        offset += 1;
        /* 分片号 (1 Bytes) */
        KCPUtils.encode8u(ptr, offset, (byte) frg);
        offset += 1;
        /* 窗口大小 (2 Bytes) */
        KCPUtils.encode16u(ptr, offset, (int) wnd);
        offset += 2;
        /* 时间戳 (4 Bytes) */
        KCPUtils.encode32u(ptr, offset, ts);
        offset += 4;
        /* 序号 (4 Bytes) */
        KCPUtils.encode32u(ptr, offset, sn);
        offset += 4;
        /* 未确认的序号 (4 Bytes) */
        KCPUtils.encode32u(ptr, offset, una);
        offset += 4;
        /* 数据长度 (4 Bytes) */
        KCPUtils.encode32u(ptr, offset, (long) data.length);
        offset += 4;

        return offset - offset_;
    }
}



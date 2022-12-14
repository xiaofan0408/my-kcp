package reactorKcp;

/**
 * @author: xuzefan
 * @date: 2022/11/14 10:33
 */
public class KCPUtils {

  //采用小端编码： https://github.com/skywind3000/kcp/issues/53
    /** encode 8 bits unsigned int*/
    public static void encode8u(byte[] p, int offset, byte c) {
        p[0 + offset] = c;
    }

    /** decode 8 bits unsigned int*/
    public static byte decode8u(byte[] p, int offset) {
        return p[0 + offset];
    }

    /** encode 16 bits unsigned int (msb) */
    public static void encode16u(byte[] p, int offset, int w) {
        p[offset + 0] = (byte)(w & 0xff);
        p[offset + 1] = (byte)((w >>> 8) & 0xff);
    }

    /** decode 16 bits unsigned int (msb) */
    public static int decode16u(byte[] p, int offset) {
        int x1 = ((int)p[offset + 0]) & 0xff;
        int x2 = ((int)p[offset + 1]) & 0xff;
        return ((x2 << 8) | x1) & 0xffff;
    }

    /** encode 32 bits unsigned int (msb) */
    public static void encode32u(byte[] p, int offset, long l) {
        p[offset + 0] = (byte)(l & 0xff);
        p[offset + 1] = (byte)((l >>> 8) & 0xff);
        p[offset + 2] = (byte)((l >>> 16) & 0xff);
        p[offset + 3] = (byte)((l >>> 24) & 0xff);
    }

    /** decode 32 bits unsigned int (msb) */
    public static long decode32u(byte[] p, int offset) {
        int x1 = ((int)p[offset + 0]) & 0xff;
        int x2 = ((int)p[offset + 1]) & 0xff;
        int x3 = ((int)p[offset + 2]) & 0xff;
        int x4 = ((int)p[offset + 3]) & 0xff;
        int x5 = (x1) | (x2 << 8) | (x3 << 16) | (x4 << 24);
        return ((long)x5) & 0xffffffff;
    }

    static long _imin_(long a, long b) {
        return a <= b ? a : b;
    }

    static long _imax_(long a, long b) {
        return a >= b ? a : b;
    }

    static long _ibound_(long lower, long middle, long upper) {
        return _imin_(_imax_(lower, middle), upper);
    }

    static int _itimediff(long later, long earlier) {
        return ((int) (later - earlier));
    }

}

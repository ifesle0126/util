package my.test.utils.ipv6;

public enum IPEnum {

    IPv4(4, "IPv4"),
    IPv6Standard(61, "IPv6Standard"),
    IPv6Compress(62, "IPv6Compress"),
    IllegalIP(-1, "IllegalIP");

    int v;
    String desc;

    IPEnum(int v, String desc) {
        this.v = v;
        this.desc = desc;
    }
}

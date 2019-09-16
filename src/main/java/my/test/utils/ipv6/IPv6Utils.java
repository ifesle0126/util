package my.test.utils.ipv6;

import sun.net.util.IPAddressUtil;

import java.util.regex.Pattern;

public class IPv6Utils {

    private static String IPv6Split = ":";
    private static final int Len_IPv6Standard = 39;
    private static final String S0 = "0";

    private final static char[] LOWER_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    private final static char[] UPPER_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private final static Pattern p = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    public static IPEnum classify(String IP) {
        if (IP == null || IP.length() < 1) {
            return IPEnum.IllegalIP;
        }
        if (IP.length() > Len_IPv6Standard) {
            return IPEnum.IllegalIP;
        }
        if (IPAddressUtil.isIPv6LiteralAddress(IP)) {
            if (IP.length() == Len_IPv6Standard) {
                return IPEnum.IPv6Standard;
            } else {
                return IPEnum.IPv6Compress;
            }
        } else if (IPAddressUtil.isIPv4LiteralAddress(IP)) {
            return p.matcher(IP).matches() ? IPEnum.IPv4 : IPEnum.IllegalIP;
        }
        return IPEnum.IllegalIP;
    }


    public static String StandardIPv6Upper(String IP) {
        if (IP == null || IP.length() < 1) {
            return IP;
        }
        IPEnum e = classify(IP);
        if (e == IPEnum.IPv6Standard || e == IPEnum.IPv6Compress) {
            byte[] IPBytes = IPAddressUtil.textToNumericFormatV6(IP);
            return StandardIPv6(IPBytes, UPPER_DIGITS);
        } else {
            return IP;
        }
    }

    public static String StandardIPv6Lower(String IP) {
        if (IP == null || IP.length() < 1) {
            return IP;
        }
        IPEnum e = classify(IP);
        if (e == IPEnum.IPv6Standard || e == IPEnum.IPv6Compress) {
            byte[] IPBytes = IPAddressUtil.textToNumericFormatV6(IP);
            return StandardIPv6(IPBytes, LOWER_DIGITS);
        } else {
            return IP;
        }
    }

    public static String CompressIPv6Upper(String IP) {
        if (IP == null || IP.length() < 1) {
            return IP;
        }
        IPEnum e = classify(IP);
        if (e == IPEnum.IPv6Standard || e == IPEnum.IPv6Compress) {
            byte[] IPBytes = IPAddressUtil.textToNumericFormatV6(IP);
            return CompressIPv6(IPBytes, UPPER_DIGITS);
        } else {
            return IP;
        }
    }

    private static String CompressIPv6(byte[] IPByte, char[] digits) {
        // 去掉前导0
        String[] items = new String[8];
        for (int i = 0; i < 8; i++) {
            int ii = ((IPByte[i << 1] << 8) & 0xff00) | (IPByte[(i << 1) + 1] & 0xff);
            int mag = Integer.SIZE - Integer.numberOfLeadingZeros(ii);
            char[] buf = new char[4];
            int charPos = Math.max(((mag + 3)) / 4, 1);
            int count = charPos;
            int offset = 4 - charPos;
            do {
                buf[offset + --charPos] = digits[ii & 15];
                ii >>>= 4;
            } while (ii != 0 && charPos > 0);
            items[i] = new String(buf, offset, count);
        }
        // 压缩连续的0
        int maxDiv = 0;
        int maxIndex = 0;
        int b = 0;
        while (b < items.length) {
            if (!S0.equals(items[b])) {
                b++;
                continue;
            }
            int e = b + 1;
            while (e < items.length && S0.equals(items[e])) {
                e++;
            }
            if (maxDiv < e - b) {
                maxIndex = b;
                maxDiv = e - b;
                b = e + 1;
            } else {
                b = e;
            }
        }
        if (maxDiv != 0) {
            StringBuilder sb = new StringBuilder();
            if (maxIndex == 0) {
                sb.append(IPv6Split);
            }
            int index = 0;
            while (index < items.length) {
                if (index == maxIndex) {
                    index += maxDiv;
                    sb.append(IPv6Split);
                } else {
                    sb.append(items[index]);
                    index++;
                    if (index < items.length) {
                        sb.append(IPv6Split);
                    }
                }
            }
            return sb.toString();
        } else {
            StringBuilder sj = new StringBuilder();
            for (String item : items) {
                sj.append(item).append(IPv6Split);
            }
            return sj.deleteCharAt(sj.length() - 1).toString();
        }
    }

    public static String CompressIPv6Lower(String IP) {
        if (IP == null || IP.length() < 1) {
            return IP;
        }
        IPEnum e = classify(IP);
        if (e == IPEnum.IPv6Standard || e == IPEnum.IPv6Compress) {
            byte[] IPBytes = IPAddressUtil.textToNumericFormatV6(IP);
            return CompressIPv6(IPBytes, LOWER_DIGITS);
        } else {
            return IP;
        }
    }

    private static String StandardIPv6(byte[] IPByte, char[] digits) {
        StringBuilder sj0 = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            // 10进制
            int ii = ((IPByte[i << 1] << 8) & 0xff00) | (IPByte[(i << 1) + 1] & 0xff);
            // 16进制 to String
            int mag = Integer.SIZE - Integer.numberOfLeadingZeros(ii);
            char[] buf = new char[]{'0', '0', '0', '0'};
            int charPos = Math.max(((mag + 3)) / 4, 1);
            int offset = 4 - charPos;
            do {
                buf[offset + --charPos] = digits[ii & 15];
                ii >>>= 4;
            } while (ii != 0 && charPos > 0);
            sj0.append(new String(buf)).append(IPv6Split);
        }
        return sj0.deleteCharAt(sj0.length() - 1).toString();
    }
}

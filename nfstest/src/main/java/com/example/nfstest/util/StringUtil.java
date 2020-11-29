package com.example.nfstest.util;

import android.widget.TextView;

public class StringUtil {
    private static final char[] HEX_EXCHANGE = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


    public static String toHexString(byte[] d) {
        // TODO 转换为十六进制形式的字符串
        final int e = d.length;
        final char[] ret = new char[e * 2];
        int x = 0;
        for (int i = 0; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX_EXCHANGE[0x0F & (v >> 4)];
            ret[x++] = HEX_EXCHANGE[0x0F & v];
        }
        return new String(ret);
    }


    public static String bytes2HexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString();
    }

    public static String hex2Decimal(String hex) {
        StringBuilder builder = new StringBuilder();
        if(hex.length() == 8) {
            for(int i = 0; i<4; i++) {
                String str = hex.substring(hex.length()-2 * (i+1), hex.length()-2*i);
                builder.append(str);
            }
        }
        String decimal = String.valueOf(Long.parseLong(builder.toString(), 16));
        while (decimal.length() < 10) {
            decimal = "0" + decimal;
        }

        return decimal;
    }

    public static void setLog(TextView text,String str){
        text.append(str+"\n");
        int offset=text.getLineCount()*text.getLineHeight();
        if(offset>text.getHeight()){
            text.scrollTo(0,offset-text.getHeight());
        }
    }
}

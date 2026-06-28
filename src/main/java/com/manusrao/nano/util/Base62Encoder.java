package com.manusrao.nano.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private final char[] dictionary;
    private final int[] indexMap;

    public Base62Encoder(@Value("${nano.base62.dictionary}") String dict) {
        this.dictionary = dict.toCharArray();
        this.indexMap = new int[128];
        for (int i = 0; i < 62; i++) {
            indexMap[dictionary[i]] = i;
        }
    }

    public String encode(long value) {
        if (value == 0) {
            return String.valueOf(dictionary[0]);
        }
        StringBuilder sb = new StringBuilder();
        long v = value;
        do {
            sb.append(dictionary[(int) Long.remainderUnsigned(v, 62)]);
            v = Long.divideUnsigned(v, 62);
        } while (v != 0);
        return sb.reverse().toString();
    }

    public long decode(String str) {
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            result = result * 62 + indexMap[str.charAt(i)];
        }
        return result;
    }
}

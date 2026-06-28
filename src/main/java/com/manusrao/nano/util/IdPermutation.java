package com.manusrao.nano.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class IdPermutation {

    private final long prime;
    private final long maxLimit;
    private final long xorMask;
    private final long inverse;

    public IdPermutation(
            @Value("${nano.permutation.prime}") long prime,
            @Value("${nano.permutation.max-limit}") long maxLimit,
            @Value("${nano.permutation.xor-mask}") String xorMaskStr) {
        this.prime = prime;
        this.maxLimit = maxLimit;
        this.xorMask = Long.parseUnsignedLong(xorMaskStr.substring(2), 16);
        this.inverse = BigInteger.valueOf(prime)
                .modInverse(BigInteger.valueOf(maxLimit))
                .longValue();
    }

    public long scramble(long id) {
        return ((id * prime) % maxLimit) ^ xorMask;
    }

    public long unscramble(long scrambled) {
        return ((scrambled ^ xorMask) * inverse) % maxLimit;
    }
}

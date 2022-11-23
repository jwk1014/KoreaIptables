package com.devwon.koreaiptables.model;

import java.util.stream.Stream;

public class CsvIpv4 implements Comparable<CsvIpv4> {
    private final String startIp;
    private final long startIpValue;
    private final String endIp;
    private final long endIpValue;

    public CsvIpv4(final String startIp, final String endIp) {
        this.startIp = startIp;
        this.startIpValue = Stream.of(startIp.split("\\.")).map(Long::parseLong).reduce(0L, (patitialResult, item) -> patitialResult * 256 + item);
        this.endIp = endIp;
        this.endIpValue = Stream.of(endIp.split("\\.")).map(Long::parseLong).reduce(0L, (patitialResult, item) -> patitialResult * 256 + item);
    }

    public String getStartIp() {
        return startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    private int isConnectable(CsvIpv4 another) {
        if (startIpValue == another.startIpValue && endIpValue == another.endIpValue) {
            return 0;
        }
        if (startIpValue <= another.startIpValue && another.endIpValue <= endIpValue) {
            throw new RuntimeException();
        }
        if (another.startIpValue <= startIpValue && endIpValue <= another.endIpValue) {
            throw new RuntimeException();
        }
        if (endIpValue + 1 == another.startIpValue) {
            return -1;
        }
        if (another.endIpValue + 1 == startIpValue) {
            return 1;
        }
        return compareTo(another) * 2;
    }

    public CsvIpv4 connect(CsvIpv4 another) {
        int diff = isConnectable(another);
        switch (diff) {
            case -1:
                return new CsvIpv4(startIp, another.endIp);
            case 0:
                return this;
            case 1:
                return new CsvIpv4(another.startIp, endIp);
            default:
                return null;
        }
    }

    @Override
    public int compareTo(CsvIpv4 another) {
        if (startIpValue != another.startIpValue) {
            return Long.compare(startIpValue, another.startIpValue);
        }
        return Long.compare(endIpValue, another.endIpValue);
    }
}

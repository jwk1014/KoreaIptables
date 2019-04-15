package com.devwon.koreaiptables.model;

import javax.xml.bind.annotation.XmlElement;

public class Ipv4 {
    @XmlElement(required = true)
    String sno;
    @XmlElement(required = true)
    String eno;
    @XmlElement(required = true)
    int cnt;
    @XmlElement(required = true)
    String date;

    public String getSno() {
        return sno;
    }

    public String getEno() {
        return eno;
    }

    public int getCnt() {
        return cnt;
    }

    public String getDate() {
        return date;
    }
}

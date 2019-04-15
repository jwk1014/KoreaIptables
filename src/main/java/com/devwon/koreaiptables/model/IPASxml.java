package com.devwon.koreaiptables.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "iplist")
public class IPASxml {
    @XmlElement(name = "ipv4")
    List<Ipv4> ipv4list;

    public List<Ipv4> getIplist() {
        return ipv4list;
    }
}

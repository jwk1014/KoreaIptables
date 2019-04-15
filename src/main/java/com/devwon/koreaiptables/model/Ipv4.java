package com.devwon.koreaiptables.model;

import javax.xml.bind.annotation.XmlElement;

public class Ipv4 implements Comparable<Ipv4> {
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

    public void connect(Ipv4 ipv4) {
        this.eno = ipv4.getEno();
        this.cnt = cnt + ipv4.getCnt();
    }

    public int getCnt() {
        return cnt;
    }

    public String getDate() {
        return date;
    }

    public int isConnectable(Ipv4 o) {
        String[] snoList = sno.split("\\.");
        int snoSubValue = Integer.parseInt(snoList[1]) * 256 * 256 + Integer.parseInt(snoList[2]) * 256 + Integer.parseInt(snoList[3]);
        String[] enoList = eno.split("\\.");
        int enoSubValue = Integer.parseInt(enoList[1]) * 256 * 256 + Integer.parseInt(enoList[2]) * 256 + Integer.parseInt(enoList[3]);

        String[] fromSnoList = o.getSno().split("\\.");
        int fromSnoSubValue = Integer.parseInt(fromSnoList[1]) * 256 * 256 + Integer.parseInt(fromSnoList[2]) * 256 + Integer.parseInt(fromSnoList[3]);
        String[] fromEnoList = o.getEno().split("\\.");
        int fromEnoSubValue = Integer.parseInt(fromEnoList[1]) * 256 * 256 + Integer.parseInt(fromEnoList[2]) * 256 + Integer.parseInt(fromEnoList[3]);

        if(!snoList[0].equals(fromSnoList[0])) {
            return 0;
        }
        if(!enoList[0].equals(fromEnoList[0])) {
            return 0;
        }

        if(enoSubValue + 1 == fromSnoSubValue) {
            return 1;
        }
        if(fromEnoSubValue + 1 == snoSubValue) {
            return 1;
        }

        if((snoList[0].compareTo(fromEnoList[0]) < 0 || (snoList[0].compareTo(fromEnoList[0]) == 0 && snoSubValue < fromEnoSubValue)) &&
                (enoList[0].compareTo(fromSnoList[0]) > 0 || (enoList[0].compareTo(fromSnoList[0]) == 0 && enoSubValue > fromSnoSubValue)))
            throw new RuntimeException();

        return 0;
    }

    @Override
    public int compareTo(Ipv4 o) {
        String[] snoList = sno.split("\\.");
        String newSno = String.format("%03d%03d%03d%03d", Integer.parseInt(snoList[0]), Integer.parseInt(snoList[1]), Integer.parseInt(snoList[2]), Integer.parseInt(snoList[3]));
        String[] enoList = eno.split("\\.");
        String newEno = String.format("%03d%03d%03d%03d", Integer.parseInt(enoList[0]), Integer.parseInt(enoList[1]), Integer.parseInt(enoList[2]), Integer.parseInt(enoList[3]));

        String[] fromSnoList = o.getSno().split("\\.");
        String newFromSno = String.format("%03d%03d%03d%03d", Integer.parseInt(fromSnoList[0]), Integer.parseInt(fromSnoList[1]), Integer.parseInt(fromSnoList[2]), Integer.parseInt(fromSnoList[3]));
        String[] fromEnoList = o.getEno().split("\\.");
        String newFromEno = String.format("%03d%03d%03d%03d", Integer.parseInt(fromEnoList[0]), Integer.parseInt(fromEnoList[1]), Integer.parseInt(fromEnoList[2]), Integer.parseInt(fromEnoList[3]));

        int s = newSno.compareTo(newFromSno);
        int e = newEno.compareTo(newFromEno);

        if(s <= 0 && e >= 0)
            throw new RuntimeException();

        return s > 0 ? 1 : e < 0 ? -1 : 0;
    }
}

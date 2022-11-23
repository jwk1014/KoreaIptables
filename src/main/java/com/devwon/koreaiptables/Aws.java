package com.devwon.koreaiptables;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface Aws {
    String BASE_URL = "https://ip-ranges.amazonaws.com";

    @GET("/ip-ranges.json")
    Call<IpRangeList> getIpRangeList();

    class IpRangeList {
        // syncToken
        // createDate
        private List<IpRange> prefixes;

        public List<IpRange> getPrefixes() {
            return prefixes;
        }
    }

    class IpRange {
        private String ipPrefix;
        private String region;
        private String service;
        private String networkBorderGroup;

        public String getIpPrefix() {
            return ipPrefix;
        }

        public String getRegion() {
            return region;
        }

        public String getService() {
            return service;
        }

        public String getNetworkBorderGroup() {
            return networkBorderGroup;
        }
    }
}

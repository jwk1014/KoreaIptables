package com.devwon.koreaiptables;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface GithubMeta {
    String BASE_URL = "https://api.github.com";

    @GET("/meta")
    Call<IpRangeList> getMeta();

    class IpRangeList {
        private List<String> actions;

        public List<String> getActions() {
            return actions;
        }
    }
}

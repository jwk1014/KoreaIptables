package com.devwon.koreaiptables;

import com.devwon.koreaiptables.model.IPASxml;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Krnic {
    String BASE_URL = "https://krnic.or.kr";

    @GET("/jsp/statboard/IPAS/inter/sec/interProCurrentXml.jsp")
    Call<IPASxml> getIPASxml();
}

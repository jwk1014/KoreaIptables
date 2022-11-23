package com.devwon.koreaiptables;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Main3 {

    public static void main(String[] args) throws Exception {
        final GithubMeta githubMeta = new Retrofit.Builder()
                .baseUrl(GithubMeta.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
                .build()
                .create(GithubMeta.class);

        githubMeta.getMeta().enqueue(new Callback<GithubMeta.IpRangeList>() {
            @Override
            public void onResponse(Call<GithubMeta.IpRangeList> call, Response<GithubMeta.IpRangeList> response) {
                response.body().getActions().forEach(item ->
                    System.out.println(String.format("-A INPUT -s %s -p tcp -m tcp -m multiport --dports 22,80,443 -j ACCEPT", item))
                );
            }

            @Override
            public void onFailure(Call<GithubMeta.IpRangeList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

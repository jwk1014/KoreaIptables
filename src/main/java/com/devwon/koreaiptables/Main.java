package com.devwon.koreaiptables;

import com.devwon.koreaiptables.model.IPASxml;
import com.devwon.koreaiptables.model.Ipv4;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static List<Integer> ACCEPT_PORTS = Arrays.asList(22,80,443,3000,8443);

    public static void main(String[] args) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Krnic.BASE_URL)
                .addConverterFactory(JaxbConverterFactory.create())
                .build();

        Krnic krnic = retrofit.create(Krnic.class);

        krnic.getIPASxml().enqueue(new Callback<IPASxml>() {
            @Override
            public void onResponse(Call<IPASxml> call, Response<IPASxml> response) {
                if(!response.isSuccessful()) {
                    System.out.println(response.code());
                    System.out.println(response.raw().body().toString());
                    return;
                }

                IPASxml ipasxml = response.body();

                List<Ipv4> sortedList = ipasxml.getIplist().stream().sorted().collect(Collectors.toList());
                List<Ipv4> list = new ArrayList<>();
                Ipv4 tmp = null;
                for(Ipv4 ipv4 : sortedList) {
                    if(tmp == null)
                        list.add(tmp = ipv4);
                    else {
                        if(tmp.isConnectable(ipv4) != 1)
                            list.add(tmp = ipv4);
                        else
                            tmp.connect(ipv4);
                    }
                }

                System.out.println();
                System.out.println("count : "+list.size());
                System.out.println();

                for(Ipv4 ipv4 : list) {
                    System.out.println(
                            String.format("-A INPUT -m iprange --src-range %s-%s -p tcp -m tcp -m multiport --dports %s -j ACCEPT",
                                    ipv4.getSno(),
                                    ipv4.getEno(),
                                    ACCEPT_PORTS.stream()
                                            .map(item -> item.toString())
                                            .reduce("", (result, item) ->
                                                    result + (result.length() > 0 ? "," : "") + item
                                            )
                            )
                    );
                }
            }

            @Override
            public void onFailure(Call<IPASxml> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

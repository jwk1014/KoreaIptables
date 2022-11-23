package com.devwon.koreaiptables;

import com.devwon.koreaiptables.model.IPASxml;
import com.devwon.koreaiptables.model.Ipv4;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static List<Integer> ACCEPT_PORTS = Arrays.asList(22,80,443);

    public static void main(String[] args) {
        printKrnicIpRangeList();
        printAwsIpRangeList();
    }

    public static void printKrnicIpRangeList() {
        final Krnic krnic = new Retrofit.Builder()
                .baseUrl(Krnic.BASE_URL)
                .addConverterFactory(JaxbConverterFactory.create())
                .build()
                .create(Krnic.class);

        krnic.getIPASxml().enqueue(new Callback<IPASxml>() {
            @Override
            public void onResponse(Call<IPASxml> call, Response<IPASxml> response) {
                StringBuilder sb = new StringBuilder("<Krnic>\n");
                if(!response.isSuccessful()) {
                    sb.append(response.code()).append("\n");
                    sb.append(response.raw().body().toString());
                    System.out.println(sb);
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

                for(Ipv4 ipv4 : list) {
                    sb.append(
                            String.format("-A INPUT -m iprange --src-range %s-%s -p tcp -m tcp -m multiport --dports %s -j ACCEPT\n",
                                    ipv4.getSno(),
                                    ipv4.getEno(),
                                    ACCEPT_PORTS.stream()
                                            .map(Object::toString)
                                            .reduce("", (result, item) ->
                                                    result + (result.length() > 0 ? "," : "") + item
                                            )
                            )
                    );
                }

                sb.append('\n');
                sb.append("count : ").append(list.size());
                System.out.println(sb);
            }

            @Override
            public void onFailure(Call<IPASxml> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void printAwsIpRangeList() {
        final Aws aws = new Retrofit.Builder()
                .baseUrl(Aws.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
                .build()
                .create(Aws.class);

        aws.getIpRangeList().enqueue(new Callback<Aws.IpRangeList>() {
            @Override
            public void onResponse(Call<Aws.IpRangeList> call, Response<Aws.IpRangeList> response) {
                StringBuilder sb = new StringBuilder("<Aws>\n");
                if(!response.isSuccessful()) {
                    sb.append(response.code()).append("\n");
                    sb.append(response.raw().body().toString());
                    System.out.println(sb);
                    return;
                }

                final TreeSet<Aws.IpRange> ipRangeSet = new TreeSet<>((lhs, rhs) -> {
                    String[] lhsList = lhs.getIpPrefix().split("\\.|/");
                    String[] rhsList = rhs.getIpPrefix().split("\\.|/");
                    for (int i=0;i<4;i++) {
                        if (Integer.parseInt(lhsList[i]) != Integer.parseInt(rhsList[i])) {
                            return Integer.parseInt(lhsList[i]) - Integer.parseInt(rhsList[i]);
                        }
                    }
                    return Integer.parseInt(lhsList[4]) - Integer.parseInt(rhsList[4]);
                });
                ipRangeSet.addAll(response.body().getPrefixes().stream()
                        .filter(p -> Arrays.asList("ap-northeast-2", "GLOBAL").contains(p.getRegion()))
                        .collect(Collectors.toList()));

                for (Aws.IpRange ipRange : ipRangeSet) {
                    sb.append(
                            String.format("-A INPUT -s %s -p tcp -m tcp -m multiport --dports %s -j ACCEPT\n",
                                    ipRange.getIpPrefix(),
                                    ACCEPT_PORTS.stream()
                                            .map(Object::toString)
                                            .reduce("", (result, item) ->
                                                    result + (result.length() > 0 ? "," : "") + item
                                            )
                            )
                    );
                }

                sb.append('\n');
                sb.append("count : ").append(ipRangeSet.size());
                System.out.println(sb);
            }

            @Override
            public void onFailure(Call<Aws.IpRangeList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

package com.devwon.koreaiptables;

import com.devwon.koreaiptables.model.CsvIpv4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main2 {
    static List<Integer> ACCEPT_PORTS = Arrays.asList(22,80,443);
    static List<String> COUNTRIES = Arrays.asList(Locale.KOREA.getCountry(), Locale.US.getCountry());

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("ipv4.csv"));
        br.readLine();
        String line;

        final Map<String, ArrayList<CsvIpv4>> countryMapIpv4List = COUNTRIES.stream().collect(Collectors.toMap(k -> k, k -> new ArrayList<>()));

        while((line = br.readLine()) != null) {
            String items[] = line.split(",");
            String country = items[1];
            if (!COUNTRIES.contains(country)) {
                continue;
            }
            String startIp = items[2];
            String endIp = items[3];
            countryMapIpv4List.get(country).add(new CsvIpv4(startIp, endIp));
        }

        countryMapIpv4List.values().forEach(Collections::sort);

        countryMapIpv4List.values().forEach(list -> {
            for (int i=0;i<list.size()-1;i++) {
                CsvIpv4 item = list.get(i);
                CsvIpv4 nextItem = list.get(i+1);
                CsvIpv4 newItem = item.connect(nextItem);
                if (newItem != null) {
                    list.set(i, newItem);
                    list.remove(i+1);
                    i--;
                }
            }
        });

        for(String country : countryMapIpv4List.keySet()) {
            System.out.println("<" + country + "> (" + countryMapIpv4List.get(country).size() + ")");
            for (CsvIpv4 ipv4 : countryMapIpv4List.get(country)) {
                System.out.println(String.format("-A INPUT -m iprange --src-range %s-%s -p tcp -m tcp -m multiport --dports %s -j ACCEPT",
                        ipv4.getStartIp(),
                        ipv4.getEndIp(),
                        ACCEPT_PORTS.stream()
                                .map(Object::toString)
                                .reduce("", (result, item) ->
                                        result + (result.length() > 0 ? "," : "") + item
                                )
                ));
            }
        }
    }
}

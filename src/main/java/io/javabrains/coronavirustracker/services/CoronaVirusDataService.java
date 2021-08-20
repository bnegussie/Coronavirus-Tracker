package io.javabrains.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.javabrains.coronavirustracker.models.CountryStats;
import io.javabrains.coronavirustracker.models.FormatData;

@Service
public class CoronavirusDataService {
    
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private Map<String, CountryStats> countryCovidData = new TreeMap<String, CountryStats>();

    @PostConstruct
    @Scheduled(cron = "@hourly")
    public void fetchVirusData() throws IOException, InterruptedException {

        Map<String, CountryStats> newCountryCovidData = new TreeMap<String, CountryStats>();

        // Creating a new client to fetch the data:
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri( URI.create( VIRUS_DATA_URL ) ).build();
        
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());


        // Converting string data into CSV object to easily process the data:
        StringReader csvBodyReader = new StringReader( httpResponse.body() );
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse( csvBodyReader );

        for (CSVRecord record : records) {
            String countryRegion = record.get("Country/Region");

            
            int countryTotalCovidCount = Integer.parseInt( record.get( record.size() - 1 ) );

            // Running count as of two days agao:
            int twoDaysAgoCount = Integer.parseInt( record.get( record.size() - 2 ) );

            // Yesterday's Covid count:
            int dailyCovidCount = countryTotalCovidCount - twoDaysAgoCount;


            if ( newCountryCovidData.containsKey( countryRegion ) ) {
                // This country has already been seen in the CSV so we will update out data:

                CountryStats prevCountryData = newCountryCovidData.get( countryRegion );

                prevCountryData.setTotalCovidCount(
                    prevCountryData.getTotalCovidCount() + countryTotalCovidCount
                );

                prevCountryData.setDailyCovidCount(
                    prevCountryData.getDailyCovidCount() + dailyCovidCount
                );

                newCountryCovidData.put(countryRegion, prevCountryData);

            } else {

                CountryStats cs = new CountryStats(
                    countryRegion, countryTotalCovidCount, dailyCovidCount
                );

                newCountryCovidData.put(countryRegion, cs);
            }
        }

        // Updating class level data value:
        countryCovidData = newCountryCovidData;
    }

    public Map<String, CountryStats> getCountryCovidData() {
        return countryCovidData;
    }

    public String getTotalCovidCountForAllCountries() {
        int totalReportedCases = 0;

        for (Map.Entry<String, CountryStats> key : countryCovidData.entrySet()) {
            totalReportedCases += key.getValue().getTotalCovidCount();
        }

        return FormatData.formatData( totalReportedCases );
    }

    public String getTotalDailyCovidCountForAllCountries() {
        int totalDailyCovidCount = 0;

        for (Map.Entry<String, CountryStats> key : countryCovidData.entrySet()) {
            totalDailyCovidCount += key.getValue().getDailyCovidCount();
        }

        return FormatData.formatData( totalDailyCovidCount );
    }
}

package io.javabrains.coronavirustracker.models;

public class CountryStats {
    private String country;
    private int totalCovidCount;
    // This would be the count reported as of yesterday:
    private int dailyCovidCount;

    public CountryStats(final String c, final int total, final int yesterday) {
        country = c;
        totalCovidCount = total;
        dailyCovidCount = yesterday;
    }

    public int getTotalCovidCount() {
        return totalCovidCount;
    }

    public int getDailyCovidCount() {
        return dailyCovidCount;
    }

    public String getCountry() {
        return country;
    }

    public void setTotalCovidCount(final int newTotal) {
        if (newTotal >= 0) {
            totalCovidCount = newTotal;
        }
    }

    public void setDailyCovidCount(final int yesterday) {
        if (yesterday >= 0) {
            dailyCovidCount = yesterday;
        }
    }
}

package io.javabrains.coronavirustracker.models;

import java.text.DecimalFormat;

public class FormatData {
    public static String formatData(int data) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format( data );
    }
}

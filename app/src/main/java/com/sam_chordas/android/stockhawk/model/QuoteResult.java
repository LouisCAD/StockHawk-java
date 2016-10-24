package com.sam_chordas.android.stockhawk.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import static com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS;

@JsonObject(fieldDetectionPolicy = NONPRIVATE_FIELDS)
public class QuoteResult {
    @JsonField(name = "Symbol")
    public String symbol;
    @JsonField(name = "Date")
    public Date date;//"2016-10-20",
    @JsonField(name = "Open")
    public float openValue;
    @JsonField(name = "High")
    public float highestValue;
    @JsonField(name = "Low")
    public float lowestValue;
    @JsonField(name = "Close")
    public float closeValue;
    @JsonField(name = "Volume")
    public long volume;
    @JsonField(name = "Adj_Close")
    public float adjustedClosingValue;
}

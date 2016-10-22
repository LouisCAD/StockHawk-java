package com.sam_chordas.android.stockhawk.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import static com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS;

@JsonObject(fieldDetectionPolicy = NONPRIVATE_FIELDS)
public class HistoricalQuotesDataQueryResult {
    public HistoricalQuotesDataResult results;
}

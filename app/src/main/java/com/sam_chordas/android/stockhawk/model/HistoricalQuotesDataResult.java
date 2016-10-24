package com.sam_chordas.android.stockhawk.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import static com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS;

@JsonObject(fieldDetectionPolicy = NONPRIVATE_FIELDS)
public class HistoricalQuotesDataResult {
    @JsonField(name = "quote")
    public List<QuoteResult> quotes;
}

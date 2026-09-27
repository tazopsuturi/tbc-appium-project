package com.tbc.appium.models;

import java.math.BigDecimal;

/** A catalog product as displayed in the app. */
public record Product(String name, BigDecimal price) {

    /** Parses a price label shown by the app, e.g. {@code "$ 29.99"}. */
    public static BigDecimal parsePrice(String label) {
        return new BigDecimal(label.replaceAll("[^0-9.]", ""));
    }
}

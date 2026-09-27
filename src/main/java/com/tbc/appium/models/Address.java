package com.tbc.appium.models;

/** Shipping address entered on the first checkout step. Optional fields may be empty strings. */
public record Address(String fullName, String addressLine1, String addressLine2,
                      String city, String state, String zipCode, String country) {
}

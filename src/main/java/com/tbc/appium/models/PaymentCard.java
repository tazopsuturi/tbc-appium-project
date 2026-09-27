package com.tbc.appium.models;

/** Card details entered on the payment checkout step. Expiration date is MMYY; the app adds the slash. */
public record PaymentCard(String holderName, String number, String expirationDate, String securityCode) {
}

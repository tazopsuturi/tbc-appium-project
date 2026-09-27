package com.tbc.appium.data;

import com.tbc.appium.models.Address;
import com.tbc.appium.models.PaymentCard;
import com.tbc.appium.models.User;

import java.math.BigDecimal;

public final class TestData {

    private TestData() {
    }

    public static final User STANDARD_USER = new User("bod@example.com", "10203040");
    public static final User LOCKED_OUT_USER = new User("alice@example.com", "10203040");

    public static final String BACKPACK = "Sauce Labs Backpack";
    /*
     * Known app defects (v2.3.0): opening several catalog items crashes the app in ProductCatalogFragment,
     * e.g. "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket"
     * (ArrayIndexOutOfBoundsException) and "Sauce Labs Backpack (green)" (NullPointerException).
     * Tests therefore use products verified to open correctly.
     */
    public static final String ORANGE_BACKPACK = "Sauce Labs Backpack (orange)";
    public static final BigDecimal BACKPACK_PRICE = new BigDecimal("29.99");

    public static final BigDecimal DELIVERY_FEE = new BigDecimal("5.99");

    public static final Address SHIPPING_ADDRESS = new Address(
            "Rebecca Winter", "Mandorley 112", "Entrance 1", "Truro", "Cornwall", "89750", "United Kingdom");

    public static final PaymentCard VALID_CARD = new PaymentCard(
            "Rebecca Winter", "4111111111111111", "0330", "123");

    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String PASSWORD_REQUIRED = "Enter Password";
    public static final String USER_LOCKED_OUT = "Sorry this user has been locked out.";
    public static final String LOGOUT_CONFIRMATION = "Are you sure you want to logout";
    public static final String CARD_VALUE_INVALID = "Value looks invalid.";
}

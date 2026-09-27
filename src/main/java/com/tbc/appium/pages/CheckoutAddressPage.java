package com.tbc.appium.pages;

import com.tbc.appium.models.Address;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Checkout step 1: shipping address form. */
public class CheckoutAddressPage extends BaseScreen {

    public CheckoutAddressPage() {
        super();
    }

    /** Mandatory form fields and the resource id of the validation message shown under each. */
    public enum RequiredField {
        FULL_NAME("fullNameErrorTV"),
        ADDRESS_LINE_1("address1ErrorTV"),
        CITY("cityErrorTV"),
        ZIP_CODE("zipErrorTV"),
        COUNTRY("countryErrorTV");

        private final String errorId;

        RequiredField(String errorId) {
            this.errorId = errorId;
        }
    }

    @AndroidFindBy(id = "enterShippingAddressTV")
    private WebElement subtitle;

    @AndroidFindBy(id = "fullNameET")
    private WebElement fullNameField;

    @AndroidFindBy(id = "address1ET")
    private WebElement addressLine1Field;

    @AndroidFindBy(id = "address2ET")
    private WebElement addressLine2Field;

    @AndroidFindBy(id = "cityET")
    private WebElement cityField;

    @AndroidFindBy(id = "stateET")
    private WebElement stateField;

    @AndroidFindBy(id = "zipET")
    private WebElement zipField;

    @AndroidFindBy(id = "countryET")
    private WebElement countryField;

    @AndroidFindBy(accessibility = "Saves user info for checkout")
    private WebElement toPaymentButton;

    @Override
    public boolean isDisplayed() {
        return isOpened(toPaymentButton);
    }

    public String getSubtitle() {
        return getText(subtitle);
    }

    public CheckoutAddressPage fillAddress(Address address) {
        StepLogger.step("Fill shipping address for '%s'", address.fullName());
        type(fullNameField, address.fullName());
        type(addressLine1Field, address.addressLine1());
        type(addressLine2Field, address.addressLine2());
        type(cityField, address.city());
        type(stateField, address.state());
        type(zipField, address.zipCode());
        type(countryField, address.country());
        return this;
    }

    public CheckoutPaymentPage continueToPayment() {
        tapToPayment();
        return new CheckoutPaymentPage();
    }

    /** Taps "To Payment" without expecting navigation, e.g. to trigger validation. */
    public CheckoutAddressPage tapToPayment() {
        StepLogger.step("Tap 'To Payment'");
        click(toPaymentButton);
        return this;
    }

    /** Validation message for the field, or {@code null} if none is shown. */
    public String getErrorMessage(RequiredField field) {
        List<WebElement> errors = driver.findElements(By.id(field.errorId));
        return errors.isEmpty() ? null : errors.get(0).getText().trim();
    }
}

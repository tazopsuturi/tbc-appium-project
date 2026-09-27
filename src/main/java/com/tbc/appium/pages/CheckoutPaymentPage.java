package com.tbc.appium.pages;

import com.tbc.appium.models.PaymentCard;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Checkout step 2: payment card form. */
public class CheckoutPaymentPage extends BaseScreen {

    public CheckoutPaymentPage() {
        super();
    }

    /** Card form fields and the resource id of the error indicator shown for each. */
    public enum CardField {
        HOLDER_NAME("nameErrorTV"),
        // The card number field only shows an error icon, no message
        CARD_NUMBER("cardNumberErrorIV"),
        EXPIRATION_DATE("expirationDateErrorTV"),
        SECURITY_CODE("securityCodeErrorTV");

        private final String errorId;

        CardField(String errorId) {
            this.errorId = errorId;
        }
    }

    @AndroidFindBy(id = "enterPaymentMethodTV")
    private WebElement subtitle;

    @AndroidFindBy(id = "nameET")
    private WebElement holderNameField;

    @AndroidFindBy(id = "cardNumberET")
    private WebElement cardNumberField;

    @AndroidFindBy(id = "expirationDateET")
    private WebElement expirationDateField;

    @AndroidFindBy(id = "securityCodeET")
    private WebElement securityCodeField;

    @AndroidFindBy(accessibility = "Saves payment info and launches screen to review checkout data")
    private WebElement reviewOrderButton;

    @Override
    public boolean isDisplayed() {
        return isOpened(reviewOrderButton);
    }

    public CheckoutPaymentPage fillCard(PaymentCard card) {
        StepLogger.step("Fill payment card for '%s'", card.holderName());
        type(holderNameField, card.holderName());
        type(cardNumberField, card.number());
        type(expirationDateField, card.expirationDate());
        type(securityCodeField, card.securityCode());
        return this;
    }

    public CheckoutReviewPage continueToReview() {
        tapReviewOrder();
        return new CheckoutReviewPage();
    }

    /** Taps "Review Order" without expecting navigation, e.g. to trigger validation. */
    public CheckoutPaymentPage tapReviewOrder() {
        StepLogger.step("Tap 'Review Order'");
        click(reviewOrderButton);
        return this;
    }

    public boolean isErrorShown(CardField field) {
        return !driver.findElements(By.id(field.errorId)).isEmpty();
    }

    /** Validation message for the field, or {@code null} if none is shown (or the field has no text message). */
    public String getErrorMessage(CardField field) {
        List<WebElement> errors = driver.findElements(By.id(field.errorId));
        return errors.isEmpty() ? null : errors.get(0).getText().trim();
    }
}

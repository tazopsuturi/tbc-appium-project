package com.tbc.appium.tests;

import com.tbc.appium.data.TestData;
import com.tbc.appium.models.Address;
import com.tbc.appium.models.PaymentCard;
import com.tbc.appium.models.Product;
import com.tbc.appium.pages.CartPage;
import com.tbc.appium.pages.CheckoutAddressPage;
import com.tbc.appium.pages.CheckoutAddressPage.RequiredField;
import com.tbc.appium.pages.CheckoutCompletePage;
import com.tbc.appium.pages.CheckoutPaymentPage;
import com.tbc.appium.pages.CheckoutPaymentPage.CardField;
import com.tbc.appium.pages.CheckoutReviewPage;
import com.tbc.appium.pages.LoginPage;
import com.tbc.appium.pages.ProductDetailsPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;

public class CheckoutTests extends BaseTest {

    @Test(description = "A guest must log in before checkout and is then taken to the shipping address step")
    public void checkoutRequiresLogin() {
        LoginPage loginPage = addBackpackToCart().proceedToCheckoutAsGuest();
        Assert.assertTrue(loginPage.isDisplayed(), "Guest should be asked to log in");

        CheckoutAddressPage addressPage = loginPage.loginAndContinueToCheckout(TestData.STANDARD_USER);
        Assert.assertTrue(addressPage.isDisplayed(), "Shipping address step should follow the login");
        Assert.assertEquals(addressPage.getSubtitle(), "Enter a shipping address", "Address step subtitle");
    }

    @Test(description = "Submitting an empty shipping address shows a message for every required field")
    public void emptyShippingAddressShowsValidationMessages() {
        CheckoutAddressPage addressPage = goToShippingAddressStep();

        addressPage.tapToPayment();

        Map<RequiredField, String> expected = Map.of(
                RequiredField.FULL_NAME, "Please provide your full name.",
                RequiredField.ADDRESS_LINE_1, "Please provide your address.",
                RequiredField.CITY, "Please provide your city.",
                RequiredField.ZIP_CODE, "Please provide your zip",
                // The app truncates this message on screen
                RequiredField.COUNTRY, "Please provide your");
        SoftAssert soft = new SoftAssert();
        expected.forEach((field, message) -> {
            String actual = addressPage.getErrorMessage(field);
            soft.assertNotNull(actual, field + " should show a validation message");
            if (actual != null) {
                soft.assertTrue(actual.startsWith(message), field + " message: expected '" + message + "' but was '" + actual + "'");
            }
        });
        soft.assertTrue(addressPage.isDisplayed(), "User should stay on the address step");
        soft.assertAll();
    }

    @Test(description = "Submitting an empty payment form flags every card field as invalid")
    public void emptyPaymentFormShowsValidationMessages() {
        CheckoutPaymentPage paymentPage = goToShippingAddressStep()
                .fillAddress(TestData.SHIPPING_ADDRESS)
                .continueToPayment();
        Assert.assertTrue(paymentPage.isDisplayed(), "Payment step should be shown");

        paymentPage.tapReviewOrder();

        SoftAssert soft = new SoftAssert();
        for (CardField field : CardField.values()) {
            soft.assertTrue(paymentPage.isErrorShown(field), field + " should be flagged as invalid");
        }
        soft.assertEquals(paymentPage.getErrorMessage(CardField.HOLDER_NAME), TestData.CARD_VALUE_INVALID, "Holder name message");
        soft.assertEquals(paymentPage.getErrorMessage(CardField.EXPIRATION_DATE), TestData.CARD_VALUE_INVALID, "Expiry message");
        soft.assertEquals(paymentPage.getErrorMessage(CardField.SECURITY_CODE), TestData.CARD_VALUE_INVALID, "CVV message");
        soft.assertTrue(paymentPage.isDisplayed(), "User should stay on the payment step");
        soft.assertAll();
    }

    @Test(description = "End-to-end purchase: cart -> login -> address -> payment -> review -> order confirmation")
    public void completePurchase() {
        Address address = TestData.SHIPPING_ADDRESS;
        PaymentCard card = TestData.VALID_CARD;

        ProductDetailsPage details = catalog.openProduct(TestData.BACKPACK);
        Product product = details.getProduct();
        CheckoutReviewPage review = details.addToCart()
                .openCart()
                .proceedToCheckoutAsGuest()
                .loginAndContinueToCheckout(TestData.STANDARD_USER)
                .fillAddress(address)
                .continueToPayment()
                .fillCard(card)
                .continueToReview();

        Assert.assertTrue(review.isDisplayed(), "Review step should be shown");
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(review.getItemNames(), List.of(product.name()), "Ordered items");
        soft.assertEquals(review.getItemCount(), 1, "Item count");
        soft.assertEquals(review.getDeliveryName(), address.fullName(), "Delivery name");
        soft.assertTrue(review.getDeliveryAddress().contains(address.addressLine1()), "Delivery address");
        soft.assertEquals(review.getCardHolder(), card.holderName(), "Card holder");
        soft.assertEquals(review.getTotalAmount(), product.price().add(TestData.DELIVERY_FEE), "Total = price + delivery");
        soft.assertAll();

        CheckoutCompletePage complete = review.placeOrder();
        Assert.assertTrue(complete.isDisplayed(), "Order confirmation should be shown");
        Assert.assertEquals(complete.getTitle(), "Checkout Complete", "Confirmation title");
        Assert.assertEquals(complete.getThankYouMessage(), "Thank you for your order", "Thank-you message");

        Assert.assertTrue(complete.continueShopping().isDisplayed(), "'Continue Shopping' should open the catalog");
    }

    private CartPage addBackpackToCart() {
        return catalog.openProduct(TestData.BACKPACK).addToCart().openCart();
    }

    private CheckoutAddressPage goToShippingAddressStep() {
        return addBackpackToCart()
                .proceedToCheckoutAsGuest()
                .loginAndContinueToCheckout(TestData.STANDARD_USER);
    }
}

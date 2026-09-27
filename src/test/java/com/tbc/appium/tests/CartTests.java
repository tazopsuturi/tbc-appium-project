package com.tbc.appium.tests;

import com.tbc.appium.data.TestData;
import com.tbc.appium.models.Product;
import com.tbc.appium.pages.CartPage;
import com.tbc.appium.pages.ProductDetailsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

public class CartTests extends BaseTest {

    @Test(description = "Adding a product updates the cart badge, cart contents and total")
    public void addSingleProductToCart() {
        ProductDetailsPage details = catalog.openProduct(TestData.BACKPACK);
        Product product = details.getProduct();

        details.addToCart();
        Assert.assertEquals(details.getCartBadgeCount(), 1, "Cart badge after adding one item");

        CartPage cart = details.openCart();
        Assert.assertEquals(cart.getItemNames(), List.of(product.name()), "Cart contents");
        Assert.assertEquals(cart.getTotalItemCount(), 1, "Total item count");
        Assert.assertEquals(cart.getTotalPrice(), product.price(), "Cart total");
    }

    @Test(description = "Adding several units of a product multiplies the cart total")
    public void addMultipleUnitsOfProduct() {
        ProductDetailsPage details = catalog.openProduct(TestData.BACKPACK)
                .selectColour("Blue")
                .increaseQuantity(2);
        Assert.assertEquals(details.getQuantity(), 3, "Selected quantity");
        Product product = details.getProduct();

        details.addToCart();
        Assert.assertEquals(details.getCartBadgeCount(), 3, "Cart badge after adding three units");

        CartPage cart = details.openCart();
        Assert.assertEquals(cart.getItemQuantity(0), 3, "Quantity of the item in the cart");
        Assert.assertEquals(cart.getTotalItemCount(), 3, "Total item count");
        Assert.assertEquals(cart.getTotalPrice(), product.price().multiply(BigDecimal.valueOf(3)), "Cart total");
    }

    @Test(description = "Different products are listed separately and totalled together")
    public void addDifferentProductsToCart() {
        ProductDetailsPage backpack = catalog.openProduct(TestData.BACKPACK);
        Product first = backpack.getProduct();
        backpack.addToCart();
        backpack.pressBackButton();

        ProductDetailsPage orangeBackpack = catalog.openProduct(TestData.ORANGE_BACKPACK);
        Product second = orangeBackpack.getProduct();
        orangeBackpack.addToCart();
        Assert.assertEquals(orangeBackpack.getCartBadgeCount(), 2, "Cart badge after adding two products");

        CartPage cart = orangeBackpack.openCart();
        Assert.assertEquals(cart.getItemNames().size(), 2, "Number of cart lines");
        Assert.assertTrue(cart.getItemNames().containsAll(List.of(first.name(), second.name())), "Cart should contain both products");
        Assert.assertEquals(cart.getTotalPrice(), first.price().add(second.price()), "Cart total");
    }

    @Test(description = "Removing the only item empties the cart")
    public void removeItemEmptiesCart() {
        ProductDetailsPage details = catalog.openProduct(TestData.BACKPACK).addToCart();
        CartPage cart = details.openCart();

        cart.removeFirstItem();

        Assert.assertTrue(cart.isEmptyStateDisplayed(), "Empty-cart state should be shown after removing the item");
        Assert.assertEquals(cart.getCartBadgeCount(), 0, "Cart badge should be hidden");
    }
}

package com.tbc.appium.tests;

import com.tbc.appium.data.TestData;
import com.tbc.appium.models.Product;
import com.tbc.appium.pages.CartPage;
import com.tbc.appium.pages.CatalogPage;
import com.tbc.appium.pages.CatalogPage.SortOption;
import com.tbc.appium.pages.LoginPage;
import com.tbc.appium.pages.ProductDetailsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class NavigationTests extends BaseTest {

    @Test(description = "The app starts on the product catalog")
    public void appStartsOnCatalog() {
        Assert.assertTrue(catalog.isDisplayed(), "Catalog should be the start screen");
        Assert.assertEquals(catalog.getTitle(), "Products", "Catalog title");
        Assert.assertFalse(catalog.getVisibleProducts().isEmpty(), "Catalog should list products");
        Assert.assertEquals(catalog.getCartBadgeCount(), 0, "Cart should be empty on a fresh start");
    }

    @Test(description = "Opening a product shows its details; Back returns to the catalog")
    public void openProductDetailsAndGoBack() {
        ProductDetailsPage details = catalog.openProduct(TestData.BACKPACK);

        Assert.assertTrue(details.isDisplayed(), "Product details should be shown");
        Assert.assertEquals(details.getProduct(), new Product(TestData.BACKPACK, TestData.BACKPACK_PRICE), "Product name and price");

        details.pressBackButton();
        Assert.assertTrue(catalog.isDisplayed(), "Back should return to the catalog");
    }

    @Test(description = "The side menu navigates to the login screen and back to the catalog")
    public void sideMenuNavigatesBetweenScreens() {
        LoginPage loginPage = catalog.openMenu().goToLogin();
        Assert.assertTrue(loginPage.isDisplayed(), "Login screen should be shown");
        Assert.assertEquals(loginPage.getTitle(), "Login", "Login screen title");

        CatalogPage catalogPage = loginPage.openMenu().goToCatalog();
        Assert.assertTrue(catalogPage.isDisplayed(), "Catalog should be shown");
    }

    @Test(description = "An empty cart shows its empty state; 'Go Shopping' returns to the catalog")
    public void emptyCartLeadsBackToCatalog() {
        CartPage cart = catalog.openCart();

        Assert.assertTrue(cart.isEmptyStateDisplayed(), "Empty-cart state should be shown");
        Assert.assertEquals(cart.getEmptyCartTitle(), "No Items", "Empty-cart title");

        Assert.assertTrue(cart.goShopping().isDisplayed(), "'Go Shopping' should open the catalog");
    }

    @Test(description = "Sorting by price (ascending/descending) reorders the catalog")
    public void sortingByPriceReordersCatalog() {
        List<BigDecimal> ascending = prices(catalog.sortBy(SortOption.PRICE_ASC).getVisibleProducts());
        Assert.assertEquals(ascending, ascending.stream().sorted().toList(), "Prices should be in ascending order");

        List<BigDecimal> descending = prices(catalog.sortBy(SortOption.PRICE_DESC).getVisibleProducts());
        Assert.assertEquals(descending, descending.stream().sorted(Comparator.reverseOrder()).toList(),
                "Prices should be in descending order");
        Assert.assertTrue(descending.get(0).compareTo(ascending.get(0)) > 0,
                "Most expensive product should differ from the cheapest one");
    }

    private static List<BigDecimal> prices(List<Product> products) {
        Assert.assertFalse(products.isEmpty(), "Catalog should list products");
        return products.stream().map(Product::price).toList();
    }
}

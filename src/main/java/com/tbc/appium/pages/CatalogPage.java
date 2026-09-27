package com.tbc.appium.pages;

import com.tbc.appium.models.Product;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/** Product catalog ("Products") - the app's start screen. */
public class CatalogPage extends BaseScreen {

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productTV\").text(\"Products\")")
    private WebElement title;

    @AndroidFindBy(id = "sortIV")
    private WebElement sortButton;

    @AndroidFindBy(id = "titleTV")
    private List<WebElement> productTitles;

    @AndroidFindBy(id = "priceTV")
    private List<WebElement> productPrices;

    public CatalogPage() {
        super();
    }

    public enum SortOption {
        NAME_ASC("Ascending order by name"),
        NAME_DESC("Descending order by name"),
        PRICE_ASC("Ascending order by price"),
        PRICE_DESC("Descending order by price");

        private final String accessibilityId;

        SortOption(String accessibilityId) {
            this.accessibilityId = accessibilityId;
        }
    }

    @Override
    public boolean isDisplayed() {
        return isOpened(title);
    }

    public String getTitle() {
        return getText(title);
    }

    public List<Product> getVisibleProducts() {
        waitForVisible(title);
        int count = Math.min(productTitles.size(), productPrices.size());
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add(new Product(productTitles.get(i).getText(), Product.parsePrice(productPrices.get(i).getText())));
        }
        return products;
    }

    public ProductDetailsPage openProduct(String productName) {
        StepLogger.step("Open product '%s'", productName);
        scrollToText(productName);
        By productImage = By.xpath("//*[@text=\"" + productName + "\"]/../*[@resource-id=\"com.saucelabs.mydemoapp.android:id/productIV\"]");
        click(driver.findElement(productImage));
        return new ProductDetailsPage();
    }

    public CatalogPage sortBy(SortOption option) {
        StepLogger.step("Sort catalog by %s", option);
        click(sortButton);
        click(driver.findElement(AppiumBy.accessibilityId(option.accessibilityId)));
        waitForVisible(title);
        return this;
    }
}

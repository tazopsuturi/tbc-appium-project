# TBC Appium Project – Android Mobile Test Automation

UI test automation framework for the Sauce Labs **My Demo App** (Android), built with
**Java 17+, Maven, Appium (UiAutomator2), TestNG, Page Factory and the Page Object Model**.
Every run produces an HTML report (ExtentReports) with test steps and failure screenshots.

---

## 1. Prerequisites

| Tool | Version used | Notes |
|---|---|---|
| JDK | 17+ (tested on 21) | `JAVA_HOME` set, `java -version` works |
| Maven | 3.9+ | `mvn -v` works |
| Android SDK | platform-tools + an emulator image | `adb` on `PATH`, `ANDROID_HOME` set |
| Node.js | 20+ | required by Appium |
| Appium | 2.x / 3.x (tested on 3.8.0) | `npm i -g appium` |
| UiAutomator2 driver | tested on 8.7.0 | `appium driver install uiautomator2` |

Optional sanity check: `npm i -g appium-doctor && appium-doctor --android`.

## 2. Setup

1. **Clone** the repository
   ```bash
   git clone <repo-url> && cd tbc-appium-project
   ```
2. **Download the app** – get `mda-2.3.0-27.apk` from the
   [my-demo-app-android releases](https://github.com/saucelabs/my-demo-app-android/releases) and put it in `apps/`:
   ```
   apps/mda-2.3.0-27.apk
   ```
   The APK is installed automatically at session start. If it is missing, the framework falls back to the app
   already installed on the device (`com.saucelabs.mydemoapp.android`).
3. **Start an emulator** (or connect a device) and check it is visible:
   ```bash
   adb devices        # e.g. emulator-5554  device
   ```

That's it. You don't need to start Appium: the framework starts a local Appium server on a free port and stops it
after the run. To use a server you started yourself, pass `-Dappium.server.url=http://127.0.0.1:4723`.

## 3. Running the tests

```bash
# Full suite (src/test/resources/testng.xml)
mvn clean test

# One class / one test
mvn test -Dtest=CartTests
mvn test -Dtest=CheckoutTests#completePurchase

# Override any configuration value
mvn test -Ddevice.udid=emulator-5556 -Dappium.server.url=http://127.0.0.1:4723
```

Tests can also be run from the IDE (right-click a test class, or `testng.xml`). The report is generated either way.

### Test report

After each run a new timestamped report is written to:

```
test-output/reports/TestReport_<yyyyMMdd_HHmmss>.html
```

It lists every test with its steps, grouped by test class, and for failures the stack trace plus a screenshot.
The console log ends with a clickable link to the report. Standard Surefire/TestNG reports are also written to
`target/surefire-reports/`.

## 4. Configuration

All settings live in [`src/test/resources/config.properties`](src/test/resources/config.properties). Any key can be
overridden with `-Dkey=value`.

| Key | Default | Purpose |
|---|---|---|
| `appium.server.url` | *(empty)* | Empty: start a local Appium server automatically |
| `device.name` / `device.udid` | `Android Emulator` / *(empty)* | Set `device.udid` when several devices are connected |
| `app.path` | `apps/mda-2.3.0-27.apk` | APK to install |
| `app.package` / `app.activity` | My Demo App | App to launch |
| `timeout.explicit` | `15` | Explicit wait timeout (seconds) |
| `compat.dialog.suppress` | `true` | How to dismiss the Android "16 KB page size" compatibility dialog (see below) |
| `report.dir` | `test-output/reports` | Where HTML reports are written |

## 5. Test coverage

20 test executions in 4 classes:

| Class | Scenarios |
|---|---|
| `LoginTests` | Valid login (menu then shows *Log Out*) · logout with confirmation · cancelling logout · demo account autofill · **validation messages** (data-driven): empty username, empty password, locked-out user |
| `NavigationTests` | App starts on catalog · catalog → product details → Back · side menu navigation (Catalog ↔ Login) · empty cart → *Go Shopping* · sorting by price (asc/desc) |
| `CartTests` | Add a product (badge, contents, total) · several units with colour selection (quantity × price) · two different products · removing an item empties the cart |
| `CheckoutTests` | Checkout requires login · **shipping address validation** messages · **payment form validation** · **end-to-end purchase** (cart → login → address → payment → review with totals incl. delivery → confirmation) |

## 6. Project structure

```
src/main/java/com/tbc/appium
├── config/        ConfigReader: properties file + -D overrides
├── driver/        AppiumServerManager (local server), DriverFactory (capabilities),
│                  DriverManager (ThreadLocal session), AppManager (app reset / ready-wait)
├── pages/         Page objects (Page Factory, @AndroidFindBy)
│   ├── BasePage       waits, safe actions, keyboard/scroll helpers, OS-dialog handling
│   ├── BaseScreen     shared toolbar: menu, cart, cart badge, Back
│   ├── CatalogPage, ProductDetailsPage, CartPage, LoginPage,
│   ├── CheckoutAddressPage, CheckoutPaymentPage, CheckoutReviewPage, CheckoutCompletePage
│   └── components/SideMenu   navigation drawer + logout dialog
├── models/        User, Address, PaymentCard, Product (records)
├── reporting/     ExtentReportManager, TestListener (report + screenshots), StepLogger
├── utils/         SystemDialogHandler
└── exceptions/    FrameworkException

src/test/java/com/tbc/appium
├── tests/         BaseTest (life cycle) + LoginTests, NavigationTests, CartTests, CheckoutTests
└── data/          TestData (users, products, addresses, expected messages)

src/test/resources  config.properties, testng.xml, simplelogger.properties
```

### Design notes

- **POM + Page Factory:** each screen is a class with `@AndroidFindBy` fields (ids, accessibility ids, UiAutomator
  selectors). Page methods return the next page object, so tests read as user flows
  (`catalog.openProduct(...).addToCart().openCart().proceedToCheckout()`). Tests never touch locators.
- **Test isolation:** one Appium session per test class, and before every test the app's data is cleared and the
  app relaunched (`mobile: clearApp`). Tests don't depend on each other and can run in any order.
- **Synchronisation:** explicit waits only (Page Factory lookup timeout is zero, no implicit waits, no sleeps).
  Stale elements during screen transitions are retried once.
- **Assertions:** TestNG hard assertions for flow checkpoints, `SoftAssert` where several fields of one screen are
  verified together (validation messages, order review), so one run reports every mismatch.
- **Error handling:** configuration and infrastructure problems raise `FrameworkException` with an actionable message
  (e.g. "no device connected", "Appium not installed"). Test failures are reported with a screenshot.
- **Reporting:** `TestListener` is attached in `BaseTest`, so the report is produced for Maven and IDE runs alike.

## 7. Known issues (app / environment)

- **Android "App Compatibility" dialog:** on Android 15+ emulator images with 16 KB memory pages, the OS warns
  that the app's native libraries aren't 16 KB aligned. The warning comes back after every app data reset.
  `SystemDialogHandler` dismisses it automatically when it appears.
- **App crashes on some products (v2.3.0):** opening *Sauce Labs Bike Light*, *Bolt T-Shirt*, *Fleece Jacket* or
  *Backpack (green)* from the catalog crashes the app (`ArrayIndexOutOfBoundsException` / `NullPointerException` in
  `ProductCatalogFragment`). Tests use *Sauce Labs Backpack* and *Backpack (orange)*, which open correctly.
- The cart is not emptied after an order is placed. This is app behaviour, and the tests don't assert on it.

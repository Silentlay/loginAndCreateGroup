package org.example;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import static org.openqa.selenium.By.cssSelector;

public class GroupCreationTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setupDriver() {
        // Указание пути к WebDriver
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeEach
    void setUp() {
        // Инициализация WebDriver перед каждым тестом
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        driver.manage().window().maximize();
    }

    @Test
    void loginAndCreateGroupTest() throws IOException {
        // Загрузка конфигурационного файла
        String configFilePath = "src/main/resources/config.properties";
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(configFilePath);
        properties.load(fileInputStream);

        String username = properties.getProperty("geekbrains_username");
        String password = properties.getProperty("geekbrains_password");

        // Переход на страницу логина
        driver.get("https://test-stand.gb.ru/login");

        // Ввод логина и пароля
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(cssSelector("form#login input[type='text']")));
        WebElement passwordField = driver.findElement(cssSelector("form#login input[type='password']"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);

        WebElement loginButton = driver.findElement(cssSelector("form#login button"));
        loginButton.click();

        // Проверка, что вошли
        WebElement usernameLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(username)));
        String actualUsername = usernameLink.getText().replace("\n", "").trim();
        Assertions.assertEquals(String.format("Hello, %s", username), actualUsername);

        // Ожидание кнопки создания группы и клик по ней
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-btn")));
        addButton.click();

        // Ожидание модального окна
        WebElement modalDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("form-modal-container")));
        Assertions.assertTrue(modalDialog.isDisplayed());

        // Уникальное имя группы
        String groupName = "TestGroup_" + System.currentTimeMillis();
        By groupNameField = By.xpath("//form//span[contains(text(), 'Group name')]/following-sibling::input");
        wait.until(ExpectedConditions.visibilityOfElementLocated(groupNameField)).sendKeys(groupName);

        // Нажатие кнопки сохранения группы
        driver.findElements(cssSelector("form div.submit button"))
                .stream().filter(WebElement::isDisplayed).findFirst().orElseThrow().click();

        // Xpath для поиска строки с группой в таблице
        String tableTitlesXpath = "//table[@aria-label='Tutors list']//tbody/tr/td[text()='%s']";

        // Ожидание появления новой группы (строки с названием группы)
        WebElement newGroupRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(String.format(tableTitlesXpath, groupName))));

        // Проверка, что строка с группой отображается на странице
        Assertions.assertTrue(newGroupRow.isDisplayed(), "Группа не была создана!");

        // Сделать скриншот для диагностики
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String screenshotPath = "src/test/resources/screenshot_" + System.currentTimeMillis() + ".png";
        FileUtils.copyFile(screenshot, new File(screenshotPath));
        System.out.println("Скриншот сделан: " + screenshotPath);
    }

    @AfterEach
    public void tearDown() {
        // Закрываем браузер после теста
        if (driver != null) {
            driver.quit();
        }
    }
}

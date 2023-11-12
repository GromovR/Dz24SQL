package ru.nerology.test;

import org.junit.jupiter.api.*;
import ru.nerology.data.DataHelper;
import ru.nerology.data.SQLHelper;
import ru.nerology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.nerology.data.SQLHelper.cleanAuthCodes;
import static ru.nerology.data.SQLHelper.cleanDataBase;

public class LoginBankTest {

    LoginPage loginPage;

    @AfterEach
    public void cleanDown() {
        cleanAuthCodes();
    }

    @BeforeEach
    public void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @AfterAll
    static void cleanDownAll() {
        cleanDataBase();
    }

    @Test
    @DisplayName("Should successfully login to dashboard with exist login and password")
    public void shouldLoginPage () {
        var authInfo = DataHelper.getAuthInfoForTest();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVisibleVerificationPage();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Should receive an error notification")
    public void shouldErrorNotification() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should receive a code error notification")
    public void shouldBeErrorNotificationWithRandomCode() {
        var authInfo = DataHelper.getAuthInfoForTest();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVisibleVerificationPage();
        var verificationCode = DataHelper.generateRandomVerificationCode ();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }
}
package kr.sul.hometaxautomation

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

object HomeTax {
    private const val HOME_URL = "https://www.hometax.go.kr/websquare/websquare.html?w2xPath=/ui/pp/index.xml"

    fun makeUserToLogin(driver: RemoteWebDriver) {
        driver.get(HOME_URL)

        WebDriverWait(driver, 10).until(
            ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='로그인']/.."))
        ).run {
            Thread.sleep(1000) // idk exactly why need this, but it needed to work well.
            click()
        }

        Thread.sleep(2000)
        driver.switchTo().frame("txppIframe")
        try {
            WebDriverWait(driver, 10).until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='아이디 로그인']/.."))
            ).sendKeys(Keys.ENTER)
        } catch (ignored: Exception) {}

        // wait until user to type passwords and got fully login
        By.xpath("//span[text()='로그아웃']").let {
            // Why need this is because If refresh occur due to login failed, it causes exception in WebDriverWait
            do {
                try {
                    WebDriverWait(driver, 6000).until(
                        ExpectedConditions.elementToBeClickable(it)
                    )
                } catch (ignored: Exception) { }
            } while(
                run {
                    try {
                        driver.findElement(it) == null
                    } catch (ignored: Exception) { true }
                }
            )
        }
    }
}
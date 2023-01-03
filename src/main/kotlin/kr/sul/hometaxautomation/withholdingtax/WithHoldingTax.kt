package kr.sul.hometaxautomation.withholdingtax

import kr.sul.hometaxautomation.FileUtil
import kr.sul.hometaxautomation.HomeTax
import kr.sul.hometaxautomation.SeleniumWork
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration


// 원천세
class WithHoldingTax(
    private val driver: RemoteWebDriver
): SeleniumWork {
    private val homeUrl = HomeTax.HOME_URL
    private val whereToDownload = File("${FileUtil.parentPath}/원천세")
    init {
        if (!whereToDownload.exists()) {
            whereToDownload.mkdirs()
        }
    }

    override fun run() {
        HomeTax.makeUserToLogin(driver)
        goToWithholdingTaxPage()
        waitForUserToClickInquireButton()

        val howManyPageDoesExist = driver.findElements(By.cssSelector("li.w2pageList_li_label a")).size
        for (i in 0 until howManyPageDoesExist) {
            driver.switchTo().defaultContent()
            driver.switchTo().frame("txppIframe")

            // Go to next page
            if (i > 0) {
                driver.findElements(By.cssSelector("li.w2pageList_li_label a"))[i].click()
                WebDriverWait(driver, 10).until(
                    ExpectedConditions.alertIsPresent()
                ).accept()
                WebDriverWait(driver, 10).until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("#ttirnam101DVOListDes_body_table tbody tr"))
                )
            }

            val tableRows = driver.findElements(By.cssSelector("#ttirnam101DVOListDes_body_table tbody tr"))
                .filter {
                    !it.getAttribute("class").contains("w2grid_hidedRow")  // idk why but 숨겨진 tr들이 밑에 꽤 있는 페이지가 있음
                }.filter {
                    try {
                        it.findElement(By.cssSelector("td:nth-child(14) button")) != null
                    } catch (e: Exception) {
                        false
                    }
                }
                .forEach { tableRow ->
                    driver.switchTo().defaultContent()
                    driver.switchTo().frame("txppIframe")
                    val companyName = tableRow.findElement(By.cssSelector("td:nth-child(7) > span")).text
                        .replace("주식회사", "(주)")
                        .replace(" ", "")
                    driver.switchTo().defaultContent() // txppIframe에 있는 상태에서 txppIframe으로 전환을 시도하면 exception이 발생하기 때문
                    driver.switchTo().frame("txppIframe")
                    tableRow.findElement(By.cssSelector("td:nth-child(14) button")).click()

                    ListOfInvoicePage(
                        driver,
                        companyName,
                        whereToDownload
                    ).run()
                }
        }
    }

    private fun goToWithholdingTaxPage() {
        driver.get(homeUrl)
        val navbarButton = driver.findElement(By.xpath("//span[text()='신고/납부']"))
        Actions(driver).moveToElement(navbarButton).perform() // Move cursor
        navbarButton.findElement(By.xpath("./../.."))
            .findElement(By.xpath("//a[text()='원천세']")).click()

        driver.switchTo().frame("txppIframe")
        WebDriverWait(driver, 10).until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='신고내역 조회 (접수증 · 납부서)']"))
        ).click()
    }

    private fun waitForUserToClickInquireButton() {
        // Wait for user to click inquire button (= wait for pop up an alert)
        By.cssSelector("#ttirnam101DVOListDes_body_table tbody tr").let {
            do {
                try {
                    WebDriverWait(driver, Duration.ofMillis(500)).until(
                        ExpectedConditions.alertIsPresent()
                    ).accept()
                    WebDriverWait(driver, Duration.ofMillis(200)).until(
                        ExpectedConditions.elementToBeClickable(it)
                    )
                } catch (ignored: Exception) { }
            } while(
                run {
                    try {
                        driver.findElements(it).size == 1  // 조회하지 않았을 때도 tr이 1개는 존재하기 때문
                    } catch (ignored: Exception) { true }
                }
            )
        }
    }
}
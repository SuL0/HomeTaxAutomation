package kr.sul.hometaxautomation.withholdingtax

import kr.sul.hometaxautomation.FileUtil
import kr.sul.hometaxautomation.Main
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration


// 원천세
object WithHoldingTax {
    private val driver = Main.driver
    private const val HOME_URL = Main.HOME_URL
    val WHERE_TO_DOWNLOAD = File("${FileUtil.parentPath}/원천세")
    init {
        if (!WHERE_TO_DOWNLOAD.exists()) {
            WHERE_TO_DOWNLOAD.mkdirs()
        }
    }



    private fun goToWithholdingTaxPage() {
        driver.get(HOME_URL)
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
        By.cssSelector("table.gridHeaderTableDefault tbody tr").let {
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
    fun run() {
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
                    ExpectedConditions.elementToBeClickable(By.cssSelector("table.gridHeaderTableDefault tbody tr"))
                )
            }

            val tableRows = driver.findElements(By.cssSelector("table.gridHeaderTableDefault tbody tr"))
                .filter {
                    !it.getAttribute("class").contains("w2grid_hidedRow")  // idk why but 숨겨진 tr들이 밑에 꽤 있는 페이지가 있음
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
                        companyName
                    ).run()
                }
        }
    }
}
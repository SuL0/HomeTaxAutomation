package kr.sul.hometaxautomation.withholdingtax

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File

// 납부서 목록
class ListOfInvoicePage(
    private val driver: RemoteWebDriver,
    private val companyName: String,
    private val whereToDownload: File
) {
    fun run() {
        driver.switchTo().frame("UTERNAAB40_iframe")

        By.cssSelector("#ttirnal111DVOListDes_body_tbody tr").let {
            WebDriverWait(driver, 10).until(
                ExpectedConditions.elementToBeClickable(it)
            )
            // 프린트 할 것이 복수일 수 있음
            driver.findElements(it)
                .forEachIndexed { idx, oneOfInvoiceLine ->
                    val incomeClassification = oneOfInvoiceLine.findElement(By.cssSelector("td:nth-child(6) span")).text
                        .replace("(갑)", "")

                    val button = oneOfInvoiceLine.findElement(By.cssSelector("td:nth-child(7)"))
                    if (!checkIfPrintButtonAvailable(button)) {
                        return@forEachIndexed
                    }
                    button.click()
                    if (!checkIfPrintIsAvailableForNow(idx)) {
                        return@forEachIndexed
                    }

                    PrintPage(
                        driver,
                        companyName,
                        incomeClassification,
                        whereToDownload
                    ).savePrint()
                }
            WebDriverWait(driver, 1).until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='닫기']"))
            ).sendKeys(Keys.ENTER)
        }
    }

    private fun checkIfPrintButtonAvailable(button: WebElement): Boolean {
        try {
            button.findElement(By.cssSelector("img"))
        } catch (e: NoSuchElementException) {
            return false
        }
        return true
    }
    private fun checkIfPrintIsAvailableForNow(idx: Int): Boolean {
        // 출력가능 기간 지나면 실패하고 알람 뜨기에, 기다린 후 알림 떴다면 수락하고 로그찍고 프린트 한 줄 return
        try {
            WebDriverWait(driver, 1).until(
                ExpectedConditions.alertIsPresent()
            ).run {
                println("")
                println("$companyName[${idx}번 째]")
                print(" -> ${driver.switchTo().alert().text}")
                accept()
            }
            return false
        } catch (ignored: Exception) { }
        return true
    }
}
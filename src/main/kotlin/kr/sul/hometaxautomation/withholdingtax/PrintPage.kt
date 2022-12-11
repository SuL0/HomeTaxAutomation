package kr.sul.hometaxautomation.withholdingtax

import kr.sul.hometaxautomation.FileUtil
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File

class PrintPage(
    private val driver: RemoteWebDriver,
    private val companyName: String,
    private val incomeClassification: String,
    private val whereToDownload: File
) {
    private val originalWindow = driver.windowHandle

    // delay 때문에 init{} 에 선언하지 않았음
    private fun init() {
        WebDriverWait(driver, 10)
            .until(ExpectedConditions.numberOfWindowsToBe(2))
        for (windowHandle in driver.windowHandles) {
            if (originalWindow != windowHandle) {
                driver.switchTo().window(windowHandle)
                break
            }
        }
    }

    fun savePrint() {
        init()

        val renameTo = run {
            val deadLine = WebDriverWait(driver, 10)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[starts-with(@aria-label, '납부기한')]")))
                .getAttribute("aria-label")
                .substringAfter("납부기한 ")
                .replaceFirst("20", "")  // 2023년에서의 20
                .replace("년", "")
                .replace("월", "")
                .replace("일", "")
                .replace(" ", "") // 납부기한
            return@run "${companyName}-${deadLine}납부서-${incomeClassification}"
        }
        WebDriverWait(driver, 10)
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title='저장']")))
            .click()
        WebDriverWait(driver, 10)
            .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".report_popup_view button[title='저장']")))
            .click()


        // 파일이 저장되기까지 기다린 후 이름과 경로 변경
        for (i in 1..20*5) {
            try {
                val result = FileUtil.manipulateFileDownloadedJustYet("납부서출력", renameTo, whereToDownload)
                if (result) {
                    break
                }
            } catch (ignored: Exception) {}
            Thread.sleep((1000/5).toLong())
        }

        // 프린트 창 닫기
        driver.close()
        driver.switchTo().window(originalWindow)
        driver.switchTo().frame("txppIframe")
        driver.switchTo().frame("UTERNAAB40_iframe")
    }

}
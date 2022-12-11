package kr.sul.hometaxautomation

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chromium.ChromiumDriver
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File

open class SeleniumWorkExecutor(
    private val seleniumWork: Class<out SeleniumWork>
) {
    companion object {
        private val activeDrivers = arrayListOf<RemoteWebDriver>()
        fun quitAllOfDriver() {
            activeDrivers.forEach {
                it.quit()
            }
        }
    }
    private val driver : ChromiumDriver
    private val profilePath = File("${System.getProperty("user.home")}/AppData/Local/Naver/Naver Whale/User Data").path  // 작동하려면 데이터를 가져올 브라우저를 꺼야 함 (+ User Data 뒤의 경로는 빼야 함)
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.5249.207 Whale/3.17.145.18 Safari/537.36"
    init {
        FileUtil.initialize()
        WebDriverManager.chromedriver().setup()
        FileUtil.emptyTempDownloadFolder()
        val prefs = hashMapOf<String, Any>().run {
            put("download.default_directory", FileUtil.tempDownloadPath.path)
            put("download.prompt_for_download", false)
            put("download.directory_upgrade", true)
            put("safebrowsing.enabled", true)
            this
        }
        driver = ChromeDriver(
            ChromeOptions().setExperimentalOption("prefs", prefs)
                .addArguments(
//                    "--user-data-dir=${profilePath}",  // TODO 테스트용
                    "user-agent=${userAgent}"
                )
        )
        activeDrivers.add(driver)
    }

    fun makeWork() {
        seleniumWork.getConstructor(RemoteWebDriver::class.java)
            .newInstance(driver)
            .run()
    }
}
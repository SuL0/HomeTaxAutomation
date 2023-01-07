package kr.sul.hometaxautomation.util

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

class CustomChromeDriver: ChromeDriver, CurrentDisplaying {
    constructor() : super()
    constructor(options: ChromeOptions) : super(options)

    override fun isAlive(): Boolean {
        return this.sessionId != null
    }
    init {
        registerCurrentDisplaying()
    }
}
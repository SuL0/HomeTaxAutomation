package kr.sul.hometaxautomation

import kr.sul.hometaxautomation.withholdingtax.WithHoldingTax

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            SeleniumWorkExecutor(
                WithHoldingTax::class.java
            ).makeWork()
        } catch (e: Exception) {
            println("catch exception")
            ErrorHandler(e).run {
                makeDumpFile()
                displayErrorAlert()
            }
        } finally {
            SeleniumWorkExecutor.quitAllOfDriver()
        }
    }
}
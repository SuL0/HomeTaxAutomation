package kr.sul.hometaxautomation

import kr.sul.hometaxautomation.excelviewer.ExcelViewer
import kr.sul.hometaxautomation.excelviewer.JTableRenderer
import kr.sul.hometaxautomation.globalincometax.GlobalIncomeTax
import kr.sul.hometaxautomation.withholdingtax.WithHoldingTax

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            when (MainGUI().makeUserToSelectWhatToDo()) {
                // 원천세
                MainGUI.withHoldingTaxButton.model -> {
                    SeleniumWorkExecutor(
                        WithHoldingTax::class.java
                    ).makeWork()
                }
                MainGUI.vatButton.model -> {
                }
                MainGUI.button3.model -> {
                }
                MainGUI.globalIncomeTaxButton.model -> {
                    val acceptedFile = FileAcceptorGUI.acceptExcelFile()
                    val excel = acceptedFile.second

                    val cellToColor = excel.getData(0).map outer@ {
                        it.map inner@ { cellValue ->
                            if (cellValue == null) return@inner null
                            val nonDigitRemoved = cellValue.replace(Regex("\\D"), "")
                            if (nonDigitRemoved.length == 13 || nonDigitRemoved.length == 10) {
                                return@inner nonDigitRemoved.toInt()
                            }
                            return@inner null
                        }
                    }
                    val identificationNumbers = cellToColor.flatten()

                    ExcelViewer(excel, 0, JTableRenderer(cellToColor)).show()
                    SeleniumWorkExecutor(
                        GlobalIncomeTax::class.java
                    ).makeWork(
                        identificationNumbers
                    )
                }
                MainGUI.socialInsuranceButton.model -> {
                    val acceptedFile = FileAcceptorGUI.acceptExcelFile()
                    val excel = acceptedFile.second

                    val cellToColor = excel.getData(0).map outer@ {
                        it.map inner@ { cellValue ->
                            if (cellValue == null) return@inner null
                            val nonDigitRemoved = cellValue.replace(Regex("\\D"), "")
                            if (nonDigitRemoved.length == 13) {
                                return@inner nonDigitRemoved.toInt()
                            }
                            return@inner null
                        }
                    }
                    val identificationNumbers = cellToColor.flatten()

                    ExcelViewer(excel, 0, JTableRenderer(cellToColor)).show()
                    SeleniumWorkExecutor(
                        GlobalIncomeTax::class.java
                    ).makeWork(
                        identificationNumbers
                    )
                }
                else -> throw Exception("알 수 없는 선택입니다")
            }
        } catch (e: Exception) {
            ErrorHandler(e).run {
                makeDumpFile()
                displayErrorAlert()
            }
        } finally {
            SeleniumWorkExecutor.quitAllOfDriver()
        }
    }
}
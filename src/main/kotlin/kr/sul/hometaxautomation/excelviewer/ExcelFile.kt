package kr.sul.hometaxautomation.excelviewer

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class ExcelFile(val file: File) {
    private val excel = XSSFWorkbook(FileInputStream(file))

    fun getData(sheetNum: Int=0): Array<Array<String?>> {
        val sheet = excel.getSheetAt(sheetNum)

        val numRows = getNumRows(sheetNum)
        val numCols = getNumCols(sheetNum)
        val nestedArray = Array<Array<String?>>(numRows) {
            arrayOfNulls(numCols)
        }

        val rowIterator = sheet.rowIterator()
        while(rowIterator.hasNext()) {
            val row = rowIterator.next()

            val cellIterator = row.cellIterator()
            while (cellIterator.hasNext()) {
                val next = cellIterator.next()
                nestedArray[row.rowNum][next.columnIndex] = if (next.cellType == CellType.NUMERIC) {
                    if (next.numericCellValue%1 == 0.0) {
                        next.numericCellValue.toInt().toString()
                    } else {
                        next.toString()
                    }
                } else {
                    next.toString()
                }
            }
        }
        return nestedArray
    }

    fun getNumRows(sheetNum: Int=0): Int {
        return excel.getSheetAt(sheetNum).lastRowNum + 1
    }
    fun getNumCols(sheetNum: Int=0): Int {
        var max = 0
        for (i in 0 until getNumRows(sheetNum)) {
            if (max < excel.getSheetAt(sheetNum).getRow(i).lastCellNum.toInt()) {
                max = excel.getSheetAt(sheetNum).getRow(i).lastCellNum.toInt()
            }
        }
        return max
    }
}
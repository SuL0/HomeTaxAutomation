package kr.sul.hometaxautomation.excelviewer

import java.awt.Color
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class JTableRenderer(private val cellToColor: List<List<Any?>>): DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

        if (cellToColor[row][column] != null) {
            c.background = Color(246, 174, 45)
        } else {
            c.background = Color.WHITE
        }

        return c
    }
}
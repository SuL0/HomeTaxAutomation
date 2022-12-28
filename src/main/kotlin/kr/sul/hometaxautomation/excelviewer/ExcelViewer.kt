package kr.sul.hometaxautomation.excelviewer

import kr.sul.hometaxautomation.util.StyledButtonUI
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.io.File
import javax.swing.*


class ExcelViewer(
    private val excelFile: ExcelFile,
    private val sheetNum: Int=0,
    private val renderer: JTableRenderer?=null)
{
    private lateinit var mainFrame: JDialog
    private val data = excelFile.getData(sheetNum)

    private val table = JTable(
        data,
        Array(excelFile.getNumCols(sheetNum)) {
            (it+65).toChar()  // using ascii
        }
    ).run {
        rowHeight = 20
        tableHeader.font = Font("나눔고딕", Font.BOLD ,15)
        font = Font("나눔고딕", Font.PLAIN ,15)
        setDefaultRenderer(Any::class.java, renderer)
        setDefaultEditor(Any::class.java, null)
        resizeColumnWidth(this)
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        this
    }
    private val okButton = JButton("확인").run {
        font = Font("나눔고딕", Font.BOLD ,15)
        setBounds(260, 30, 70, 30)
        background = Color(14, 185, 190)
        foreground = Color.white
        setUI(StyledButtonUI())
        addActionListener {
            mainFrame.dispose()
        }
        this
    }

    fun show() {
        mainFrame = JDialog().run mainFrame@ {
            this.isModal = true // 코드 멈추게 하기 위해 필요한 설정
            this.setSize(
                table.preferredSize.width+19,
                (table.preferredSize.height + 100 + table.preferredSize.height*0.3).toInt()
            )
            this.setLocationRelativeTo(null)
            this.title = excelFile.file.name
            this.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)
            contentPane.add(JScrollPane(table))
            contentPane.add(JPanel().run {
                layout = BorderLayout()
                border = BorderFactory.createEmptyBorder(2, 0, 1, 10)
                add(okButton, BorderLayout.EAST)
                this
            })
            this
        }
        mainFrame.isVisible = true  // frame이 닫기기 전까지 여기서 코드 멈춤
    }


    private fun resizeColumnWidth(table: JTable) {
        val columnModel = table.columnModel
        for (column in 0 until table.columnCount) {
            var width = 80 // Min width
            for (row in 0 until table.rowCount) {
                val renderer = table.getCellRenderer(row, column)
                val comp = table.prepareRenderer(renderer, row, column)
                width = (comp.preferredSize.width + 1).coerceAtLeast(width)
            }
            if (width > 300) width = 300
            columnModel.getColumn(column).preferredWidth = width
        }
    }
}
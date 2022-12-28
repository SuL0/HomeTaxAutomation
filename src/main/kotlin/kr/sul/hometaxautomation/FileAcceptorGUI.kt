package kr.sul.hometaxautomation

import kr.sul.hometaxautomation.excelviewer.ExcelFile
import kr.sul.hometaxautomation.excelviewer.ExcelViewer
import kr.sul.hometaxautomation.excelviewer.JTableRenderer
import java.awt.BorderLayout
import java.awt.Font
import java.awt.dnd.*
import java.io.File
import javax.swing.*


// 일단 엑셀을 넣고 알아서 판단한 후 Vat 또는 GlobalIncomeTax 작업을 진행하도록
@Suppress("UNCHECKED_CAST")
class FileAcceptorGUI<T> private constructor(
    private val nameOfWhatToAccept: String,
    private val dataProcessor: (File) -> T
) {
    companion object {
        fun<T> acceptFile(nameOfWhatToAccept: String, dataProcessor: (File) -> T): T {
            return FileAcceptorGUI(nameOfWhatToAccept, dataProcessor).acceptFile()
        }
        // accept excel file and extract data only from first sheet
        fun acceptExcelFile(): Pair<File, ExcelFile> {
            return FileAcceptorGUI("Excel") { file ->
                return@FileAcceptorGUI Pair(file, ExcelFile(file))
            }.acceptFile()
        }
    }
    private lateinit var frame: JDialog
    private var returnValueConveyor: T? = null
    fun acceptFile(): T {
        frame = JDialog().run {
            isModal = true // 코드를 멈추게 하기 위해 필요한 isModal = true
            setSize(450, 250)
            setLocationRelativeTo(null)
            val label = JLabel("${nameOfWhatToAccept}을 여기로 드래그", SwingConstants.CENTER).run {
                font = Font("나눔고딕", Font.BOLD ,20)
                this
            }
            val listener = object: DropTargetListener {
                override fun drop(e: DropTargetDropEvent) {
                    e.acceptDrop(DnDConstants.ACTION_COPY)
                    // Get the data formats of the dropped item
                    val flavors = e.transferable.transferDataFlavors
                    for (flavor in flavors) {
                        try {
                            // If the drop items are files
                            if (flavor.isFlavorJavaFileListType) {
                                // Get all of the dropped files
                                val files = e.transferable.getTransferData(flavor) as List<File>
                                if (files.size != 1) {
                                    JOptionPane.showMessageDialog(null, "파일을 한 개만 드래그 해주세요.  [참조값: ${files.size}]", "", JOptionPane.ERROR_MESSAGE)
                                    return
                                }
                                try {
                                    this@FileAcceptorGUI.returnValueConveyor = dataProcessor(files[0])
                                } catch (e: Exception) {
                                    ErrorHandler(e)
                                        .displaySimpleErrorAlert()
                                    return
                                }
                                frame.dispose()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    e.dropComplete(true)
                }
                override fun dragEnter(dtde: DropTargetDragEvent) {
                }
                override fun dragOver(dtde: DropTargetDragEvent) {
                }
                override fun dropActionChanged(dtde: DropTargetDragEvent) {
                }
                override fun dragExit(dte: DropTargetEvent) {
                }
            }
            DropTarget(label, listener)
            this.contentPane.add(BorderLayout.CENTER, label)
            this
        }
        frame.isVisible = true // frame이 닫히기 전까지 여기서 코드가 멈춤
        return returnValueConveyor!!
    }
}
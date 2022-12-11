package kr.sul.hometaxautomation

import java.awt.Dimension
import java.io.File
import java.io.FileWriter
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JScrollPane


class ErrorHandler(
    private val e: Exception
) {
    companion object {
        private val DUMP_FILE_PATH = File("${FileUtil.parentPath.path}/dump")
    }
    init {
        if (!DUMP_FILE_PATH.exists()) {
            DUMP_FILE_PATH.mkdirs()
        }
    }

    fun makeDumpFile() {
        try {
            val file = File("${DUMP_FILE_PATH}/${System.currentTimeMillis()}.txt")
            file.createNewFile()
            val fw = FileWriter(file)
            fw.write(e.stackTraceToString())
            fw.flush()
            fw.close()
        } catch (ignored: Exception) { }
    }

    fun displayErrorAlert() {
//        val list = JList(arrayOf(e.message, "", "", e.stackTrace))
//        val scrollPane = JScrollPane(list)
//        scrollPane.preferredSize = Dimension(800, 250)
//        JOptionPane.showMessageDialog(
//            null,
//            scrollPane,
//            "에러",
//            JOptionPane.ERROR_MESSAGE
//        )
        JOptionPane.showMessageDialog(null, e.stackTrace, "", JOptionPane.ERROR_MESSAGE)
    }
}
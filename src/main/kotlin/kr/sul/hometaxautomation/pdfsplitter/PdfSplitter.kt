package kr.sul.hometaxautomation.pdfsplitter

import kr.sul.hometaxautomation.FileUtil
import org.apache.pdfbox.multipdf.Splitter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import javax.swing.JOptionPane

object PdfSplitter {
    private val whereToDownload = File("${FileUtil.parentPath}/PDF_SPLIT")
    init {
        if (!whereToDownload.exists()) {
            whereToDownload.mkdirs()
        }
    }

    fun split(file: File) {
        val document = PDDocument.load(file)
        val splitter = Splitter()

        for (pdf in splitter.split(document)) {
            val text = PDFTextStripper().getText(pdf).split("\n")
            pdf.save("${file.parent}/${PdfForm.extractWhatNameWillBeFit(text)}")
        }
        document.close()
        JOptionPane.showMessageDialog(null, "Succeed")
    }
}
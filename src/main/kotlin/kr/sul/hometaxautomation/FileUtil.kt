package kr.sul.hometaxautomation

import java.io.File

object FileUtil {
    val parentPath = File("${System.getProperty("user.home")}/Desktop/홈택스")
    val tempDownloadPath = File("${parentPath}/temp")

    fun initialize() {
        if (!tempDownloadPath.exists()) {
            tempDownloadPath.mkdirs()
        }
    }


    fun manipulateFileDownloadedJustYet(fileNameStartWith: String, renameTo: String, moveTo: File): Boolean {
        val file = getFileDownloadedJustYet()
            ?: throw Exception("해당 파일이 temp 폴더에 존재하지 않습니다")
        if (!file.name.startsWith(fileNameStartWith))
            throw Exception("해당 파일의 이름이 $fileNameStartWith 로 시작하지 않습니다 [${file.name}]")
        // if destination is already exists, do override
        val destination = File("${moveTo}/${renameTo}.${file.extension}")
        if (destination.exists()) {
             destination.delete()
        }
        return file.renameTo(
            destination
        )
    }

    fun emptyTempDownloadFolder() {
        recursiveDelete(tempDownloadPath)
    }

    private fun getFileDownloadedJustYet(): File? {
        if (tempDownloadPath.listFiles() == null) return null
        return tempDownloadPath.listFiles()!!.maxByOrNull { it.lastModified() }
    }
    private fun recursiveDelete(directoryPath: File) {
        // clear all files in directory
        if (directoryPath.listFiles() != null) {
            for (file in directoryPath.listFiles()!!) {
                if (file.isDirectory) {
                    recursiveDelete(file)
                }
                file.delete()
            }
        }
    }
}
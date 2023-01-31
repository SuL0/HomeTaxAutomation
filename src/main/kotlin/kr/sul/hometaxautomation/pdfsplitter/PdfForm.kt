package kr.sul.hometaxautomation.pdfsplitter

enum class PdfForm {
    A {
        override fun checkIfItMatchWith(text: List<String>): Boolean {
            return text[0].contains("■국세징수법시행규칙[별지제1호서식]")
        }

        override fun nameFile(text: List<String>): String {
            val name = extractName(text)
            val deadLine = extractDeadLine(text)
            return "$name $deadLine 부가가치세.pdf"
        }

        private fun extractName(text: List<String>): String {
            return text[text.indexOfFirst { it.contains("(성명)") }+1]
                .processName()
        }

        private fun extractDeadLine(text: List<String>): String {
            return text.find { it.startsWith("납부기한") }!!
                .substringAfter("납부기한 ")
                .processDate()
        }
    },
    B {
        override fun checkIfItMatchWith(text: List<String>): Boolean {
            return text[0].contains("부가가치세 신고서 접수증")
        }
        override fun nameFile(text: List<String>): String {
            val name = extractName(text)
            val deadLine = extractDeadLine(text)
            return "$name $deadLine 부가가치세 신고서 접수증.pdf"
        }


        private fun extractName(text: List<String>): String {
            return text.find { it.contains("(성명)") }!!
                .substringAfter("(성명) ")
                .substringBefore(" 사업자")
                .processName()
        }
        private fun extractDeadLine(text: List<String>): String {
            return text.find { it.contains("접수일시") }!!
                .split(" ")[3]
                .processDate()
        }
    };


    companion object {
        fun extractWhatNameWillBeFit(text: List<String>): String {
            for (pdfForm in values()) {
                try {
                    if (pdfForm.checkIfItMatchWith(text)) {
                        return pdfForm.nameFile(text)
                    }
                } catch (ignored: Exception) {
                    println("catch")
                }
            }
            throw Exception()
        }
    }


    abstract fun checkIfItMatchWith(text: List<String>): Boolean
    abstract fun nameFile(text: List<String>): String


    fun String.processName(): String {
        return this.replace("[^A-Za-z0-9ㄱ-ㅎㅏ-ㅣ가-힣]".toRegex(), "") // 무엇인지는 모르겠지만, 안 보이는 이상한 문자가 들어있음 (여러개의 String 이 합쳐졌을 때 다른 문자를 지워버림)
            .replace("주식회사", "(주)")
            .replace("사업자등록번호", "")
            .replace(" ", "")
    }
    fun String.processDate(): String {
        return this.replace("[^0-9]".toRegex(), "")
            .replaceFirst("20", "")
    }
}
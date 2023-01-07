package kr.sul.hometaxautomation.util

import kr.sul.hometaxautomation.Main

interface CurrentDisplaying {
    fun isAlive(): Boolean
    fun registerCurrentDisplaying() {
        Main.currentDisplaying = this
    }
}
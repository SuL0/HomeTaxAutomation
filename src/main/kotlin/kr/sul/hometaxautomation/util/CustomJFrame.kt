package kr.sul.hometaxautomation.util

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame

class CustomJFrame: JFrame(), CurrentDisplaying {
    override fun setVisible(b: Boolean) {
        super.setVisible(b)
        registerCurrentDisplaying()
    }

    override fun isAlive(): Boolean {
        return this.isActive
    }

    init {
        addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                super.windowClosed(e)
                dispose()
            }
        })
    }
}
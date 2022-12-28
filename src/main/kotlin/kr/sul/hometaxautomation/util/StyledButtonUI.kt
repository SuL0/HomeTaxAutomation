package kr.sul.hometaxautomation.util

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.AbstractButton
import javax.swing.JComponent
import javax.swing.border.EmptyBorder
import javax.swing.plaf.basic.BasicButtonUI

class StyledButtonUI : BasicButtonUI() {
    override fun installUI(c: JComponent) {
        super.installUI(c)
        val button = c as AbstractButton
        button.isOpaque = false
        button.border = EmptyBorder(5, 15, 5, 15)
    }

    override fun paint(g: Graphics, c: JComponent) {
        val b = c as AbstractButton
        paintBackground(g, b, if (b.model.isPressed) 2 else 0)
        super.paint(g, c)
    }

    private fun paintBackground(g: Graphics, c: JComponent, yOffset: Int) {
        val size = c.size
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setColor(c.background.darker())
        g.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 10, 10)
        g.setColor(c.background)
        g.fillRoundRect(0, yOffset, size.width, size.height + yOffset - 5, 10, 10)
    }
}
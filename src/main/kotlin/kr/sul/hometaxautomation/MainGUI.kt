package kr.sul.hometaxautomation

import kr.sul.hometaxautomation.util.CustomJFrame
import kr.sul.hometaxautomation.util.StyledButtonUI
import java.awt.Color
import java.awt.Font
import javax.swing.ButtonGroup
import javax.swing.ButtonModel
import javax.swing.JButton
import javax.swing.JRadioButton

class MainGUI {
    private var frame: CustomJFrame? = null
    companion object {
        val withHoldingTaxButton = JRadioButton("원천세", true).run {
            setBounds(20, 20, 200, 50)
            this
        }
        val vatButton = JRadioButton("부가세", false).run {
            setBounds(20, 80, 200, 50)
            this
        }
        val button3 = JRadioButton("고지조회", false).run {
            setBounds(20, 140, 200, 50)
            this
        }
        val globalIncomeTaxButton = JRadioButton("종합소득세", false).run {
            setBounds(20, 200, 200, 50)
            this
        }
        val socialInsuranceButton = JRadioButton("4대보험", false).run {
            setBounds(20, 260, 200, 50)
            this
        }
        init {
            listOf(withHoldingTaxButton, vatButton, button3, globalIncomeTaxButton, socialInsuranceButton).forEach {
                it.font = Font("나눔고딕", Font.BOLD ,20)
            }
        }
    }


    private val buttonGroup = ButtonGroup().run {
        add(withHoldingTaxButton)
        add(vatButton)
        add(button3)
        add(globalIncomeTaxButton)
        add(socialInsuranceButton)
        this
    }
    private val enterButton = JButton("Enter").run {
        font = Font("나눔고딕", Font.BOLD ,15)
        setBounds(260, 30, 70, 30)
        background = Color(14, 185, 190)
        foreground = Color.white
        ui = StyledButtonUI()
        addActionListener {
            frame!!.dispose()
        }
        this
    }

    fun addEnterButtonListener(r: (ButtonModel) -> Unit): MainGUI {
        enterButton.addActionListener {
            Thread {
                r.invoke(buttonGroup.selection)
            }.start()
        }
        return this
    }

    fun makeUserToSelectWhatToDo() {
        frame = CustomJFrame().run {
            setSize(380, 380)
            setLocationRelativeTo(null)
            val buttonIterator = buttonGroup.elements.iterator()
            layout = null
            while(buttonIterator.hasNext()) {
                add(buttonIterator.next())
            }
            add(enterButton)
            this
        }
        frame!!.isVisible = true
    }
}
import java.awt.Color
import java.awt.Frame
import java.awt.Graphics
import java.awt.GridLayout
import java.awt.Panel
import java.awt.event.*
import java.lang.Integer.max
import java.util.Random
import javax.swing.*
import javax.swing.border.Border
import kotlin.system.exitProcess


class DrawMap(m: Map, data: List<Iris>) : JFrame(), ActionListener, KeyListener {
    private val w = m.w
    private val h = m.h
    private val map = m
    private var colors = Array(w) { IntArray(h) }
    private val dl = data

    var button = JButton("train")
    var panel = JPanel()

    init {
        addKeyListener(this)
        add(button)
        button.addActionListener(this)
        isVisible = true
        createLayout(button)
        setSize(800, 700)
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                exitProcess(0)
            }
        })
    }
    private fun createLayout(vararg arg: JComponent) {

        val gl = GroupLayout(contentPane)
        contentPane.layout = gl

        gl.autoCreateContainerGaps = true

        gl.setHorizontalGroup(
            gl.createSequentialGroup()
                .addComponent(arg[0])
        )

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addComponent(arg[0])
        )

        pack()
    }
    override fun paint(gra: Graphics) {
        var temp = 10
        var temp2 = 10
        for (i in 0 until w) {
            for (j in 0 until h) {
                var r = (map.neurons[i][j].weights[0]*255).toInt()
                var g = (map.neurons[i][j].weights[2]*255).toInt()
                var b = (map.neurons[i][j].weights[1]*255).toInt()
                var a = (map.neurons[i][j].weights[3]*255).toInt()
                val color = Color(
                    r,
                    g,
                    b,
                    a
                )
                colors[i][j] = color.rgb
                gra.color = color
                gra.fillRect(i+temp, j+temp2+75, map.wn+2, map.hn+2)
                temp2+=10
            }
            temp2 = 10
            temp += 10
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        repeat(1000){
            dl.forEach { map.train(it, 5, 1.0, this) }
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        TODO("Not yet implemented")
    }

    override fun keyPressed(e: KeyEvent) {
        if(e.keyCode == KeyEvent.VK_SPACE){

        }
    }

    override fun keyReleased(e: KeyEvent?) {
        TODO("Not yet implemented")
    }

}
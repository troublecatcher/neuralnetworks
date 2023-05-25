import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class DrawDigit(private val nn: NeuralNetwork) : JFrame(), Runnable, MouseListener, MouseMotionListener,
    KeyListener {
    private val w = 28
    private val h = 28
    private val scale = 32
    private var mousePressed = 0
    private var mx = 0
    private var my = 0
    private var colors = Array(w) { DoubleArray(h) }
    private val img = BufferedImage(w * scale + 215, h * scale, BufferedImage.TYPE_INT_RGB)
    private val pimg = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    private var frame = 0

    init {
        this.setSize(w * scale + 200 + 16, h * scale + 38)
        this.isVisible = true
        defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocation(50, 50)
        this.add(JLabel(ImageIcon(img)))
        addMouseListener(this)
        addMouseMotionListener(this)
        addKeyListener(this)
    }

    override fun run() {
        while (true) {
            this.repaint()
        }
    }

    override fun paint(g: Graphics) {
        val inputs = DoubleArray(784)
        for (i in 0 until w) {
            for (j in 0 until h) {
                if (mousePressed != 0) {
                    var dist = ((i - mx) * (i - mx) + (j - my) * (j - my)).toDouble()
                    if (dist < 1) dist = 1.0
                    dist *= dist
                    if (mousePressed == 1) colors[i][j] += 0.1 / dist else colors[i][j] -= 0.1 / dist
                    if (colors[i][j] > 1) colors[i][j] = 1.0
                    if (colors[i][j] < 0) colors[i][j] = 0.0
                }
                var color = (colors[i][j] * 255).toInt()
                color = color shl 16 or (color shl 8) or color
                pimg.setRGB(i, j, color)
                inputs[i + j * w] = colors[i][j]
            }
        }

        val outputs = nn.feed(inputs)

        val canvas = img.graphics as Graphics2D
        canvas.drawImage(pimg, 0, 0, w * scale, h * scale, this)
        canvas.color = Color.black
        canvas.fillRect(w * scale + 1, 0, 215, h * scale)
        canvas.font = Font("Ubuntu", Font.PLAIN, 48)
        for (i in 0..9) {
            canvas.color = Color(outputs[i].toFloat(), outputs[i].toFloat(), outputs[i].toFloat())
            canvas.drawString("$i: ${outputs[i]}", w * scale + 20, i * w * scale / 15 + 150)
        }
        g.drawImage(img, 8, 30, w * scale + 200, h * scale, this)
        frame++
    }

    override fun mouseClicked(e: MouseEvent) {}
    override fun mousePressed(e: MouseEvent) {
        mousePressed = 1
        if (e.button == 3) mousePressed = 2
    }

    override fun mouseReleased(e: MouseEvent) {
        mousePressed = 0
    }

    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    override fun keyTyped(e: KeyEvent) {}
    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_SPACE) {
            colors = Array(w) { DoubleArray(h) }
        }
        if(e.keyCode == KeyEvent.VK_T){
            Main.train(nn, 100, 60000, "/Users/anton/Desktop/train")
        }
    }

    override fun keyReleased(e: KeyEvent) {}
    override fun mouseDragged(e: MouseEvent) {
        mx = e.x / scale
        my = e.y / scale
    }

    override fun mouseMoved(e: MouseEvent) {
        mx = e.x / scale
        my = e.y / scale
    }
}
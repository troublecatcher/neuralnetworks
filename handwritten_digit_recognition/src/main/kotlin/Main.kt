import java.awt.image.BufferedImage
import java.io.File
import java.util.function.UnaryOperator
import javax.imageio.ImageIO
import kotlin.math.exp

val sigmoid = UnaryOperator { x: Double -> 1 / (1 + exp(-x)) }
val sigmoidDerivative = UnaryOperator { y: Double -> y * (1 - y) }

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val nn = NeuralNetwork(0.1, sigmoid, sigmoidDerivative, 784, 512, 128, 10)
//        train(nn, 1000, 60000, "/Users/anton/Desktop/train")
        test(nn)
    }

    fun train(nn: NeuralNetwork, epochs: Int, samples: Int, filepath: String) {
        val images = arrayOfNulls<BufferedImage>(samples)
        val digits = IntArray(samples)
        val imagesFiles = File(filepath).listFiles()
        for (i in 0 until samples) {
            images[i] = ImageIO.read(imagesFiles[i])
            digits[i] = (imagesFiles[i].name[10].toString()).toInt()
        }

        val inputs = Array(samples) { DoubleArray(784) }
        for (i in 0 until samples) {
            for (x in 0..27) {
                for (y in 0..27) {
                    inputs[i][x + y * 28] = (images[i]!!.getRGB(x, y) and 0xff) / 255.0
                }
            }
        }

        for (i in 1 until epochs) {
            var correct = 0
            var wrong = 0.0
            val batchSize = 100
            for (j in 0 until batchSize) {
                val imgIndex = (Math.random() * samples).toInt()
                val targets = DoubleArray(10)
                val digit = digits[imgIndex]
                targets[digit] = 1.0
                val outputs: DoubleArray = nn.feed(inputs[imgIndex])
                var maxDigit = 0
                var maxDigitWeight = -1.0
                for (k in 0..9) {
                    if (outputs[k] > maxDigitWeight) {
                        maxDigitWeight = outputs[k]
                        maxDigit = k
                    }
                }
                if (digit == maxDigit) correct++
                for (k in 0..9) {
                    wrong += (targets[k] - outputs[k]) * (targets[k] - outputs[k])
                }
                nn.backprop(targets)
            }
            println("Epoch: $i. Accuracy rate: $correct. Error: $wrong")
        }
    }
    private fun test(nn: NeuralNetwork){
        val f = DrawDigit(nn)
        Thread(f).start()
    }
}
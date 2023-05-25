import java.util.function.UnaryOperator

class NeuralNetwork(
    private val learningRate: Double,
    private val activation: UnaryOperator<Double>,
    private val derivative: UnaryOperator<Double>,
    vararg sizes: Int
) {
    private val layers: Array<Layer>

    init {
        layers = arrayOfNulls<Layer>(sizes.size) as Array<Layer>
        for (i in sizes.indices) {
            var nextSize = 0
            if (i < sizes.size - 1) nextSize = sizes[i + 1]
            layers[i] = Layer(sizes[i], nextSize)
            for (j in 0 until sizes[i]) {
                layers[i].biases[j] = Math.random() * 2 - 1
                for (k in 0 until nextSize) {
                    layers[i].weights[j][k] = Math.random() * 2 - 1
                }
            }
        }
    }

    fun feed(inputs: DoubleArray): DoubleArray {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.size)
        for (i in 1 until layers.size) {
            val currentLayer: Layer = layers[i - 1]
            val nextLayer: Layer = layers[i]
            for (j in 0 until nextLayer.size) {
                nextLayer.neurons[j] = 0.0
                for (k in 0 until currentLayer.size) {
                    nextLayer.neurons[j] += currentLayer.neurons[k] * currentLayer.weights[k][j]
                }
                nextLayer.neurons[j] += nextLayer.biases[j]
                nextLayer.neurons[j] = activation.apply(nextLayer.neurons[j])
            }
        }
        return layers[layers.size - 1].neurons
    }

    fun backprop(expected: DoubleArray) {
        var currentErrors = DoubleArray(layers[layers.size - 1].size)
        for (i in 0 until layers[layers.size - 1].size) {
            currentErrors[i] = expected[i] - layers[layers.size - 1].neurons[i]
        }
        for (k in layers.size - 2 downTo 0) {
            val previousLayer: Layer = layers[k]
            val currentLayer: Layer = layers[k + 1]
            val previousErrors = DoubleArray(previousLayer.size)
            val gradients = DoubleArray(currentLayer.size)
            for (i in 0 until currentLayer.size) {
                gradients[i] = currentErrors[i] * derivative.apply(layers[k + 1].neurons[i])
                gradients[i] *= learningRate
            }
            val deltas = Array(currentLayer.size) { DoubleArray(previousLayer.size) }
            for (i in 0 until currentLayer.size) {
                for (j in 0 until previousLayer.size) {
                    deltas[i][j] = gradients[i] * previousLayer.neurons[j]
                }
            }
            for (i in 0 until previousLayer.size) {
                previousErrors[i] = 0.0
                for (j in 0 until currentLayer.size) {
                    previousErrors[i] += previousLayer.weights[i][j] * currentErrors[j]
                }
            }
            currentErrors = DoubleArray(previousLayer.size)
            System.arraycopy(previousErrors, 0, currentErrors, 0, previousLayer.size)
            val weightsNew = Array(previousLayer.weights.count()) { DoubleArray(previousLayer.weights[0].count()) }
            for (i in 0 until currentLayer.size) {
                for (j in 0 until previousLayer.size) {
                    weightsNew[j][i] = previousLayer.weights[j][i] + deltas[i][j]
                }
            }
            previousLayer.weights = weightsNew
            for (i in 0 until currentLayer.size) {
                currentLayer.biases[i] += gradients[i]
            }
        }
    }
}
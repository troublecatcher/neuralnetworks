class Layer(var size: Int, nextSize: Int) {
    var neurons: DoubleArray
    var biases: DoubleArray
    var weights: Array<DoubleArray>

    init {
        neurons = DoubleArray(size)
        biases = DoubleArray(size)
        weights = Array(size) { DoubleArray(nextSize) }
    }
}
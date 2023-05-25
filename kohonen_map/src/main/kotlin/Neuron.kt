import kotlin.random.Random

class Neuron(
    n: Int
){
    var x = 0
    var y = 0
    var dist = 0.0
//    var value = Random.nextDouble()
    var weights = DoubleArray(n){ Random.nextDouble() }
}
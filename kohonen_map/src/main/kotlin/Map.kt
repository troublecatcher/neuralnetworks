import java.sql.Time
import java.sql.Timestamp
import kotlin.math.*

class Map(
    val n: Int,
    val w: Int,
    val h: Int
) {
    var neurons = Array(w){Array(h){Neuron(n)} }
    val wn = 10
    val hn = 10
    val sigma0 = max(w/wn, h/hn) / 2.0
    var sigma = 0.0
    var L = 0.0
    var theta = 0.0
    var r = 0.0
    var neighbors = arrayListOf<Neuron>()

    init {
        for (i in 0 until w){
            for (j in 0 until h){
                neurons[i][j].x = i
                neurons[i][j].y = j
            }
        }
    }
    fun train(data: Iris, T: Int, L0: Double, dm: DrawMap){
        val lambda = T/ln(sigma0)
        var currentWinner: Neuron = this.winner(data)
            for (t in 1 .. T){
                this.sigma = this.sigma0 * exp(-t/lambda)
                this.L = L0 * exp(-t/lambda)
                neurons.forEach { al ->
                    al.forEach {
                        if(
                            sqrt((it.x - currentWinner.x).toDouble().pow(2) + (it.y - currentWinner.y).toDouble().pow(2)) < this.sigma
                            ){this.neighbors.add(it)} }
                }
                neighbors.forEach{
                    this.r = sqrt((it.x - currentWinner.x).toDouble().pow(2) + (it.y - currentWinner.y).toDouble().pow(2))
                    this.theta = exp(-((this.r.pow(2))/(2 * this.sigma.pow(2))))
                    it.weights[0] += this.theta * this.L * (data.sepalLength - it.weights[0])
                    it.weights[1] += this.theta * this.L * (data.sepalWidth - it.weights[1])
                    it.weights[2] += this.theta * this.L * (data.petalLength - it.weights[2])
                    it.weights[3] += this.theta * this.L * (data.petalWidth - it.weights[3])

//                    it.weights[0]  = 1.0
//                    it.weights[1]  = 1.0
//                    it.weights[2]  = 1.0
//                    it.weights[3]  = 1.0
                }
            }
        dm.paint(dm.graphics)
    }
    fun winner(data: Iris): Neuron {
        var sumList = arrayListOf<Double>()
        neurons.forEach{ al ->
            al.forEach { neuron ->
                var sum = 0.0
                    sum += (data.sepalLength - neuron.weights[0]).pow(2)
                    sum += (data.sepalWidth - neuron.weights[1]).pow(2)
                    sum += (data.petalLength - neuron.weights[2]).pow(2)
                    sum += (data.petalWidth - neuron.weights[3]).pow(2)
                neuron.dist = sqrt(sum)
            }
        }
        var min = 1000.0
        var mneuron = Neuron(n)
        neurons.forEach { al ->
                al.forEach { if(it.dist < min ){
                    min = it.dist
                    mneuron = it
                }
            }
        }
        return mneuron
    }
}
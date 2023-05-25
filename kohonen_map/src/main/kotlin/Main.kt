import java.io.File

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val data = File("/Users/anton/Desktop/Iris.csv")
            .readLines()
            .drop(1)
            .map {
                val (sepalLength, sepalWidth, petalLength, petalWidth) = it.split(",").drop(1)
                Iris(sepalLength.toDouble()/10, sepalWidth.toDouble()/10, petalLength.toDouble()/10, petalWidth.toDouble()/10)
            }
        val map = Map(4, 50, 50)

//        data.forEach { map.train(it, 10, 0.6) }

        DrawMap(map, data)
    }
}
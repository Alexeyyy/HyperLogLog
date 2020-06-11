package hyperLogLog

import java.lang.Integer.max
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

/*
* Реализация алгоритма HyperLogLog, вдохновлено: https://m.habr.com/ru/post/119852/
* и https://stackoverflow.com/questions/5990713/loglog-and-hyperloglog-algorithms-for-counting-of-large-cardinalities,
* а также http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf
* */
class HyperLogLog {
    // Общее количество оценок (2^k).
    private var m: Int = 0
    // Степень 2ки, используется для расчета количества оценок.
    private var k: Int = 0
    private var kComp: Int = 0
    // Корректирующий коэффициент.
    private var alphaM: Double = 0.0
    // Массив оценок.
    private lateinit var M: Array<Int>
    // 2^32.
    private val pow_2_32 = 0xFFFFFFFF + 1

    constructor(std_error : Double) {
        m =  (1.04 / std_error).toInt()
        k = ceil(log2(m * m)).toInt()
        kComp = 32 - k
        m = 2.0.pow(k).toInt()

        alphaM = when (m) {
                    16 -> 0.673
                    32 -> 0.697
                    64 -> 0.709
                    else -> 0.7213 / (1 + 1.079 / m)
                }

        M = Array(m) { 0 }
    }

    fun count(hash: Int?) : Double? {
        if (hash != null) {
            val j = (hash ushr kComp)
            val rank = rank(hash, kComp)
            M[j] = max(M[j], rank)
            return null
        }
        // Расчет финальной оценки: https://habrastorage.org/r/w1560/storage/habraeffect/14/1d/141da8ea50bda0f765445767f488f5b7.png.
        else {
            var c = 0.0
            for (i in 0 until m) {
                c += 1 / 2.0.pow(M[i].toDouble())
            }
            var e = alphaM * m * m / c

            // Коррекция результата (http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf - 14 с.)
            if (e <= 5/2 * m) {
                var v = 0
                for (i in 0 until m) {
                    if (M[i] == 0) {
                        ++v
                    }
                }

                if (v > 0) {
                    e = m * ln(m.toDouble() / v)
                }
            } else if (e > 1 / 30 * pow_2_32) {
                e = -pow_2_32 * ln(1 - e / pow_2_32)
            }

            return e;
        }
    }

    private fun log2(x: Int) : Double {
        return ln(x.toDouble()) / ln(2.0)
    }

    /*
    * Поиск "ранга" - первого ненулевого бита слова.
    * Например 1: 1010001010000100010011011111101 - 1-ый ранг.
    *                                           ↑
    * Например 2: 101100110111001001100010000010 - 2-ой ранг.
    *                                         ↑
    * */
    private fun rank(hash : Int, max : Int) : Int {
        var r: Int = 1
        var copyHash = hash

        while ((copyHash and 1) == 0 && r <= max) {
            ++r
            copyHash = copyHash ushr 1
        }

        return r
    }
}
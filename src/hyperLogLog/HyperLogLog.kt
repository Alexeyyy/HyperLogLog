package hyperLogLog

import java.lang.Integer.max
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

class HyperLogLog {
    private var m: Int = 0
    private var k: Int = 0
    private var kComp: Int = 0
    private var alphaM: Double = 0.0
    private lateinit var M: Array<Int>
    private val pow_2_32 = 0xFFFFFFFF + 1

    constructor(std_error : Double) {
        m =  (1.04 / std_error).toInt()
        k = ceil(log2(m * m)).toInt()
        kComp = 32 - k
        m = 2.0.pow(k).toInt()

        alphaM = if (m == 16) 0.673
            else if (m == 32) 0.697
            else if (m == 64) 0.709
            else 0.7213 / (1 + 1.079 / m)

        M = Array(m) { 0 }
    }

    fun count(hash: Int?) : Double? {
        if (hash != null) {
            var j = (hash ushr kComp).toInt()
            M[j] = max(M[j], rank(hash, kComp))
            return null
        }
        else {
            var c = 0.0
            for (i in 0 until m) {
                c += 1 / 2.0.pow(M[i].toDouble())
            }
            var E = alphaM * m * m / c

            // коррекция результата.
            if (E <= 5/2 * m) {
                var V = 0
                for (i in 0 until m) {
                    if (M[i] == 0) {
                        ++V
                    }
                }

                if (V > 0) {
                    E = m * ln(m.toDouble() / V)
                }
            } else if (E > 1 / 30 * pow_2_32) {
                E = -pow_2_32 * ln(1 - E / pow_2_32)
            }

            return E;
        }
    }

    private fun log2(x: Int) : Double {
        return ln(x.toDouble()) / ln(2.0)
    }

    private fun rank(hash : Int, max : Int) : Int {
        var r: Int = 1
        var copyHash = hash

        while ((copyHash and 1) == 0 && r <= max) {
            ++r
            copyHash = copyHash ushr 1
        }

        return r
    }

    /*
    * Хеш-функция.
    * https://ru.wikipedia.org/wiki/FNV
    * */
    fun fnv1a(str: String) : Int {
        var hash = 2147483647//2166136261

        for (element in str) {
            hash = hash xor (element.toInt())
            hash += (hash shl 1) + (hash shl 4) + (hash shl 7) + (hash shl 8) + (hash shl 24)
        }
        return hash ushr 0
    }
}
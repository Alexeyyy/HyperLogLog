package helpers

import com.sun.org.apache.xpath.internal.operations.Bool
import hyperLogLog.FNV1a
import hyperLogLog.HyperLogLog
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.util.HashSet

object Distinct {
    /*
    * Читает файл и считает количество уникальных строк.
    * */
    fun estimateUniqueNumberOfStrings(fileName: String, error: Double = 0.065, compareResult: Boolean = false, verbose: Boolean = true) : Pair<Int,Int> {
        try {
            var hyperLogLog = HyperLogLog(error)
            var fnv1a = FNV1a()

            var fis = FileInputStream(fileName)
            var br = BufferedReader(InputStreamReader(fis))
            var hashSet: HashSet<String> = hashSetOf()
            var line: String?
            var index = 0

            // Считаем время выполнения алгоритма.
            val start: Long = Instant.now().toEpochMilli()

            line = br.readLine()
            while (line != null) {
                // Считаем реальную статистику, если это необходимо сделать.
                // Минус в том, что расходуем ценную память =).
                if (compareResult) {
                    if (!hashSet.contains(line)) {
                        hashSet.add(line)
                    }
                }

                // Считаем оценку числа уникальных строк.
                val hash = fnv1a.makeHash(line)
                hyperLogLog.count(hash)

                // Забираем следующую строку в память.
                line = br.readLine()
                index++
            }

            val end: Long = Instant.now().toEpochMilli()

            if (verbose) {
                index--
                println("Total number of IP addresses: $index")
                println(String.format("Completed in %d milliseconds", (end-start)));
            }

            return Pair(hyperLogLog.count(null)?.toInt() as Int, if (compareResult) hashSet.size else -1)
        }
        catch(ex: Exception) {
            println("Error occurred while reading the file. Error: ${ex.message}")
        }

        return Pair(-1, -1)
    }
}
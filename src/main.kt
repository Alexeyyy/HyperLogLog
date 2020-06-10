import hyperLogLog.HyperLogLog
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

// Статья: https://m.habr.com/ru/post/119852/
// Example: https://www.cc.gatech.edu/classes/AY2009/cs7260_fall/lecture2.pdf

fun main(args: Array<String>) {
    // Создать стрим из фаила.  https://habr.com/ru/company/luxoft/blog/270383/
    // Пример стрима https://gist.github.com/InfoSec812/f7b03ad627f6e194c793aa908febafdc

    //var res = distinctIPs("test.txt")
    generateFile("test.txt", 1000000)
    plainRead("test.txt")

    /*var hyperLogLog: HyperLogLog = HyperLogLog(0.065)
    println(hyperLogLog.fnv1a("aardvark").toString(2))*/

    /*var fnv: FNVHash = FNVHash()
    var res = fnv.hash32("aardvark")
    println(res.toString(2))*/
}

fun distinctIPs(fileName: String) : ArrayList<String> {
    //val pool: ForkJoinPool = ForkJoinPool(4)
    val start: Long = Instant.now().toEpochMilli()

    var map: ConcurrentHashMap<String, Integer> = ConcurrentHashMap()
    val path: Path = Paths.get(fileName)

    var ls: ArrayList<String> = arrayListOf()

    var res = Files.readAllLines(path)
                .parallelStream()
                .unordered()
                //.stream()
                .distinct()
                .forEach { word -> ls.add(word) }

    val end: Long = Instant.now().toEpochMilli()
    println(String.format("\tCompleted in %d milliseconds", (end-start)));

    return ls
}

fun plainRead(fileName: String) {
    val start: Long = Instant.now().toEpochMilli()

    var log : HyperLogLog = HyperLogLog(0.065)
    var index = 0

    try {
        var fis: FileInputStream = FileInputStream(fileName)
        var br: BufferedReader = BufferedReader(InputStreamReader(fis))
        var hashSet: HashSet<String> = hashSetOf()
        var line: String?

        do {
            line = br.readLine()
            if (line == null) {
                index--
                break
            }
            log.count(log.fnv1a(line))
            if (!hashSet.contains(line)) {
                hashSet.add(line)
            }
            index++
        } while(line != null)

        var count = log.count(null)
        println("Total number of IP adresses: $index")
        println("$count, error: ${(count?.minus(index))?.div((index / 100.0))} %")
        println("Real world number: ${hashSet.count()}")
    } catch (ex: Exception) {
        println("Something occurred while reading file's contents. Error: ${ex.message}. Iteration: $index")
    }

    val end: Long = Instant.now().toEpochMilli()
    println(String.format("\tCompleted in %d milliseconds", (end-start)));
}

fun generateFile(fileName: String, number: Int) {
    FileOutputStream(fileName, false).bufferedWriter().use { writer ->
        for(i in 0..number) {
            writer.appendln(generateIP())
        }
    }
}

fun generateIP() : String {
    val rand: Random = Random()
    return "${rand.nextInt(25)}.${rand.nextInt(25)}.${rand.nextInt(25)}.${rand.nextInt(25)}"
}
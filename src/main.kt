import helpers.Distinct
import helpers.IPHelper

fun main(args: Array<String>) {
    // Генерация файла.
    IPHelper.generateFile("test.txt")

    // Оценка файла.
    var estimation = Distinct.estimateUniqueNumberOfStrings("test.txt", verbose = true, compareResult = true, error = 0.065)

    // Вывод строки о результатах подсчета.
    if (estimation.first != -1) {
        println("Estimated number of unique IP addresses ${estimation.first}${ if (estimation.second != -1) "\nReal world count of unique IPs is ${estimation.second}" else ""}")
    }
}


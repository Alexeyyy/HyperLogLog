import helpers.Distinct
import helpers.IPHelper

fun main(args: Array<String>) {
    //var fileName = "E:\\ip_addresses\\ip_addresses"
    var fileName = "test.txt"

    // Генерация файла.
    IPHelper.generateFile("test.txt", numberOfLines = 10000, boundary = 5)

    // Оценка файла.
    var estimation = Distinct.estimateUniqueNumberOfStrings(fileName, verbose = true, compareResult = true, error = 0.002)

    // Вывод строки о результатах подсчета.
    if (estimation.first != -1) {
        println("Estimated number of unique IP addresses ${estimation.first}${ if (estimation.second != -1) "\nReal world count of unique IPs is ${estimation.second}" else ""}")
    }
}


package helpers

import java.io.FileOutputStream
import java.util.*

object IPHelper {
    /*
    * Функция генерирует файл с ip-адресами.
    * @fileName - наименование создаваемого файла.
    * @numberOfLines - количество IP-адресов.
    * @boundary - верхняя граница части IPv4 адреса.
    * */
    fun generateFile(fileName: String, numberOfLines: Int = 100000, boundary: Int = 25) {
        FileOutputStream(fileName, false).bufferedWriter().use { writer ->
            for(i in 0..numberOfLines) {
                writer.appendln(generateIP(boundary))
            }
        }
    }

    private fun generateIP(b: Int) : String {
        val rand: Random = Random()
        return "${rand.nextInt(b)}.${rand.nextInt(b)}.${rand.nextInt(b)}.${rand.nextInt(b)}"
    }
}
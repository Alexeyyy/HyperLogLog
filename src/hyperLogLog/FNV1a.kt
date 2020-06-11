package hyperLogLog

class FNV1a {
    /*
    * Хеш-функция. 32-битная реализация.
    * Немного теории: https://ru.wikipedia.org/wiki/FNV или https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1a_hash.
    * */
    fun makeHash(str: String) : Int {
        var hash = 2147483647//2166136261

        for (element in str) {
            hash = hash xor (element.toInt())
            hash += (hash shl 1) + (hash shl 4) + (hash shl 7) + (hash shl 8) + (hash shl 24)
        }
        return hash ushr 0
    }
}
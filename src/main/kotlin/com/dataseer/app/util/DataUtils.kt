package com.dataseer.app.util
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Utility function for file compression and decompression using GZIP (Around 1:2 compression rate for CSV files)
 * @author Blendica Vlad
 * @date 15.03.2020
 */


/**
 * Compress a file
 * @param file [ByteArray]
 * @throws [Exception]
 * @return compressed file [ByteArray]
 */
@Throws(Exception::class)
fun compressFile(file : ByteArray) : ByteArray {
    if (file.isEmpty()) return file
    val byteArrayOutputStream = ByteArrayOutputStream()
    val gzip = GZIPOutputStream(byteArrayOutputStream)
    gzip.write(file)
    gzip.close()
    byteArrayOutputStream.close()
    return byteArrayOutputStream.toByteArray()
}

/**
 * Decompress a compressed file
 * @param file [ByteArray]
 * @throws [Exception] input file must be in GZIP format
 * @return decompressed file [ByteArray]
 */
@Throws(Exception::class)
fun decompressFile(file : ByteArray): ByteArray {
    if (file.isEmpty()) return file
    val gis = GZIPInputStream(ByteArrayInputStream(file))
    val content = gis.bufferedReader().use(BufferedReader::readText)
    gis.close()
    return content.toByteArray()
}

fun main() {
    //todo to be moved in unit test
    val key = "test"
    val toBeCompressed = "aaaaaaaaaaaaaatestttttttttttttttttttasdfghjkzzzzzzxxxxxccccaaaaaaaaaaaaaat" +
            "estttttttttttttttttttasdfghjkzzzzzzxxxxxccccaaaaaaaaaaaaaatestttttttttttttttttttasdfghj" +
            "kzzzzzzxxxxxccccaaaaaaaaaaaaaatestttttttttttttttttttasdfghjkzzzzzzxxxxxccccaaaaaaaaaaaa" +
            "aatestttttttttttttttttttasdfghjkzzzzzzxxxxxccccaaaaaaaaaaaaaatestttttttttttttttttttasdf" +
            "ghjkzzzzzzxxxxxccccaaaaaaaaaaaaaatestttttttttttttttttttasdfghjkzzzzzzxxxxxccccaaaaaaaaa" +
            "aaaaatestttttttttttttttttttasdfghjkzzzzzzxxxxxccccaaaaaaaaaaaaaatesttttttttttttttttttta" +
            "sdfghjkzzzzzzxxxxxcccc"

    val data = compressFile(toBeCompressed.toByteArray())
//    val encriptedData = Crypto.encryptData(key,data)
//    val decriptedData = Crypto.decryptData(key,encriptedData)
    val result = decompressFile(data)
    if (toBeCompressed.toByteArray().contentEquals(result)) println("Merge")
}
package com.application.app.util

import com.application.app.security.Crypto
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@Throws(Exception::class)
fun compressFile(file : ByteArray) : ByteArray {
    if (file.isEmpty()) return file
    val byteArrayOutputStream = ByteArrayOutputStream()
    val gzip = GZIPOutputStream(byteArrayOutputStream)
    gzip.write(file)
    gzip.close()
    byteArrayOutputStream.close()
    //println("Compression lenght = ${byteArrayOutputStream.toByteArray().size}")
    return byteArrayOutputStream.toByteArray()
}

@Throws(Exception::class)
fun decompressFile(file : ByteArray): ByteArray {
    if (file.isEmpty()) return file
    val gis = GZIPInputStream(ByteArrayInputStream(file))
    val bf = BufferedReader(InputStreamReader(gis, "UTF-8"))
    var outStr = ""
    var line: String
    while (bf.readLine().also { line = it ?: "" } != null) {
        outStr += line
    }
    gis.close()
    bf.close()
    //println("Decompression lenght = ${outStr.toByteArray().size}")
    return outStr.toByteArray()
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
    val encriptedData = Crypto.encryptData(key,data)
    val decriptedData = Crypto.decryptData(key,encriptedData)
    val result = decompressFile(decriptedData!!)
    if (toBeCompressed.toByteArray().contentEquals(result)) println("Merge")
}
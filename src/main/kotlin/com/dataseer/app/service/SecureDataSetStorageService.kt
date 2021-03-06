package com.dataseer.app.service

import com.dataseer.app.exception.FileStorageException
import com.dataseer.app.model.DataHeader
import com.dataseer.app.model.DataSet
import com.dataseer.app.model.MimeTypes
import com.dataseer.app.repository.DataSetRepository
import com.dataseer.app.repository.query_specifications.DataSetSpecifications
import com.dataseer.app.security.Crypto
import com.dataseer.app.security.SecurityContextProvider
import com.dataseer.app.util.compressFile
import com.dataseer.app.util.decompressFile
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.streams.asSequence

/**
 * Secure DB persistence service for [DataSet] handling
 * @author Blendica Vlad
 * @date 14.03.2020
 */
@Service
class SecureDataSetStorageService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SecureDataSetStorageService::class.java)
    }

    @Autowired
    private lateinit var dataSetRepository: DataSetRepository

    @Autowired
    private lateinit var securityContextProvider: SecurityContextProvider

    /**
     * Store a [DataSet] with its corresponding [MultipartFile] into DB
     * @param file [MultipartFile]
     * @param dataSet [DataSet]
     * @return persisted dataset [DataSet]
     */
    @Transactional
    @Throws(Exception::class)
    fun storeDataSet(file: MultipartFile, dataSet: DataSet): DataSet {
        // Normalize file name
        if (file.isEmpty) {
            throw Exception("The dataset has no file")
        }
        if (!validateFileType(file)) {
            throw Exception("Only csv files are allowed")
        }
        if (dataSetRepository.count(DataSetSpecifications.ofUser(securityContextProvider.getCurrentContextUser())) >= 5) {
            throw FileStorageException("Sorry! Maximum threshold of files(5) ")
        }
        val fileName = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
            }

            val reader = BufferedReader(InputStreamReader(ByteArrayInputStream(file.bytes)))
            val headers = reader.readLine().split(",")
            IntStream.range(0, headers.size).mapToObj {
                DataHeader(
                        headerName = headers[it].replace("\"", ""),
                        isTimeIndex = it == 0,
                        dataSet = dataSet
                )
            }.asSequence().iterator().forEach {
                dataSet.headers.add(it)
            }

            //encrypt and compress the file
            dataSet.data = transformDataToDB(file.bytes)
            dataSet.fileType = file.contentType!!

            dataSetRepository.saveAndFlush(dataSet)
        } catch (ex: IOException) {
            logger.error(ex.toString())
            throw FileStorageException("Could not store file $fileName. Please try again!", ex)
        } finally {
            logger.info("Saved dataset with id: ${dataSet.id}")
        }
    }

    /**
     * Check if the file type corresponds to the allowed ones
     * @param file [MultipartFile]
     * @return [Boolean] true if valid
     */
    fun validateFileType(file: MultipartFile): Boolean {
        return when (file.contentType) {
            MimeTypes.MIME_TEXT_CSV -> {
                return true
            }
            MimeTypes.MIME_APPLICATION_VND_MSEXCEL -> {
                return true
            }
            else -> false
        }
    }

    /**
     * Retrieve a [DataSet] from DB, or empty [Optional] if not found
     * @param fileId [Long]
     * @return Optional<[DataSet]>
     */
    fun retrieveDataSet(fileId: Long): Optional<DataSet> {

        return dataSetRepository.findByIdAndCreatedBy(fileId, securityContextProvider.getCurrentContextUser())
                .also { dataSet ->
                    if (dataSet.isPresent) {
                        dataSet.get().data = transformDataFromDB(dataSet.get().data!!) //decompress and decrypt the data
                    }
                }
    }

    /**
     * Retrieve a list of [DataSet] objects from DB
     * @return List<[DataSet]>
     */
    fun retrieveDataSets(): List<DataSet> {

        return dataSetRepository.findAll(DataSetSpecifications.ofUser(securityContextProvider.getCurrentContextUser()))
                .also { dataSetList ->
                    dataSetList.forEach { dataSet ->
                        dataSet.data = transformDataFromDB(dataSet.data!!) //decompress and decrypt the data for each file
                    }
                }
    }

    /**
     * Compress and encrypt data (uses user password as secret key for the encryption/decryption process)
     * First compress and then encrypt because of high entropy of encryption
     * @param data [ByteArray]
     * @return compressed and encrypted data [ByteArray]
     */
    private fun transformDataToDB(data: ByteArray): ByteArray {

        return Crypto.encryptData(securityContextProvider.getCurrentContextUser().password!!, compressFile(data))!!
    }

    /**
     * Decrypt and decompress data
     * @param data [ByteArray]
     * @return decrypted and decompressed data [ByteArray]
     */
    private fun transformDataFromDB(data: ByteArray): ByteArray {

        return decompressFile(Crypto.decryptData(securityContextProvider.getCurrentContextUser().password!!, data)!!)
    }

    /**
     * Delete a dataset
     * @param dataSet [DataSet]
     */
    fun deleteDataSet(dataSet : DataSet) {
        dataSetRepository.delete(dataSet)
    }
}
package com.application.app.service

import com.application.app.exception.FileStorageException
import com.application.app.model.DataSet
import com.application.app.repository.DataSetRepository
import com.application.app.repository.query_specifications.DataSetSpecifications
import com.application.app.security.Crypto
import com.application.app.security.SecurityContextProvider
import com.application.app.util.compressFile
import com.application.app.util.decompressFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

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
        val fileName = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
            }
            //encrypt and compress the file
            dataSet.data = transformDataToDB(file.bytes)
            dataSet.fileType = file.contentType!!

            dataSetRepository.save(dataSet)
        } catch (ex: IOException) {
            logger.error(ex.toString())
            throw FileStorageException("Could not store file $fileName. Please try again!", ex)
        } finally {
            logger.info("Saved dataset with id: ${dataSet.id}")
        }
    }

    /**
     * Retrieve a [DataSet] from DB, or empty [Optional] if not found
     * @param fileId [Long]
     * @return Optional<[DataSet]>
     */
    fun retrieveDataSet(fileId: Long): Optional<DataSet> {

        return dataSetRepository.findByIdAndCreatedBy(fileId, securityContextProvider.getCurrentContextUser()!!)
                .also {
                    if (it.isPresent) {
                        it.get().data = transformDataFromDB(it.get().data!!) //decompress and decrypt the data
                    }
                }
    }

    /**
     * Retrieve a list of [DataSet] objects from DB,
     * @return List<[DataSet]>
     */
    fun retrieveDataSets(): List<DataSet> {

        return dataSetRepository.findAll(DataSetSpecifications.ofUser(securityContextProvider.getCurrentContextUser()!!))
                .also { it ->
                    it.forEach {
                        it.data = transformDataFromDB(it.data!!) //decompress and decrypt the data for each file
                    }
                }
    }

    /**
     * Compress and encrypt data
     * First compress and then encrypt because of high entropy of encryption
     * @param data [ByteArray]
     * @return compressed and encrypted data [ByteArray]
     */
    private fun transformDataToDB(data: ByteArray): ByteArray {

        return Crypto.encryptData(securityContextProvider.getCurrentContextUser()!!.password!!, compressFile(data))!!
    }

    /**
     * Decrypt and decompress data
     * @param data [ByteArray]
     * @return decrypted and decompressed data [ByteArray]
     */
    private fun transformDataFromDB(data: ByteArray): ByteArray {

        return decompressFile(Crypto.decryptData(securityContextProvider.getCurrentContextUser()!!.password!!, data)!!)
    }
}
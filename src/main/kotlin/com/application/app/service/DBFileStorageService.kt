package com.application.app.service

import com.application.app.exception.FileStorageException
import com.application.app.exception.MyFileNotFoundException
import com.application.app.model.DataSet
import com.application.app.repository.DataSetRepository
import com.application.app.repository.UserRepository
import com.application.app.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class DBFileStorageService {
    @Autowired
    private lateinit var dataSetRepository: DataSetRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    fun storeFile(file: MultipartFile, dataSet: DataSet): DataSet {
        // Normalize file name
        val fileName = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
            }
            dataSet.data = file.bytes
            dataSet.fileType = file.contentType!!

            val userPrincipal = UserPrincipal.getCurrentUserPrincipal()
            val user = userRepository.findById(userPrincipal.id!!)
            val userData = user.get().userData

            dataSet.userdata = userData

            dataSetRepository.save(dataSet)
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file $fileName. Please try again!", ex)
        }
    }

    fun getFile(fileId: Long): DataSet {
        return dataSetRepository.findById(fileId)
                .orElseThrow { MyFileNotFoundException("File not found with id $fileId") }
    }
}
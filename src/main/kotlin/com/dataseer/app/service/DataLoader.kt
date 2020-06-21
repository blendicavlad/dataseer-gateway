package com.dataseer.app.service

import com.dataseer.app.controller.AuthController
import com.dataseer.app.model.AuthProvider
import com.dataseer.app.model.DataSet
import com.dataseer.app.model.User
import com.dataseer.app.payload.LoginRequest
import com.dataseer.app.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.Environment
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataLoader : ApplicationRunner {

    @Autowired lateinit var passwordEncoder : PasswordEncoder

    @Autowired lateinit var env : Environment

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var secureDataSetStorageService: SecureDataSetStorageService

    @Autowired lateinit var authController: AuthController

    override fun run(args: ApplicationArguments?) {
        print("Initialize data")
        initUser()
        initDataSets()
    }

    fun initUser() {
        val user = User(
                fullName = "Andrei Testescu",
                email = "test@gmail.com",
                password = passwordEncoder.encode(env.getProperty("init.userpass")!!),
                provider = AuthProvider.local
        )
        userRepository.save(user)
        val loginRequest: LoginRequest = LoginRequest("test@gmail.com", env.getProperty("init.userpass")!!)
        authController.authenticateUser(loginRequest)
    }

    fun initDataSets() {
        var dataSet = DataSet(
                id = 1,
                name = "DS-1",
                fileName = "macrodata.csv",
                description = "Date Macro Economice US",
                fileType = "text/csv"
        )
        var fileContent = this::class.java.classLoader.getResource("mock_data/macrodata.csv")!!.readText()
        var file = MockMultipartFile("macrodata.csv","macrodata.csv","text/csv",fileContent.toByteArray())
        secureDataSetStorageService.storeDataSet(file,dataSet)
        dataSet = DataSet(
                id = 2,
                name = "DS-2",
                fileName = "airline_passengers.csv",
                description = "Pasagerii companie de aviatie",
                fileType = "text/csv"
        )
        fileContent = this::class.java.classLoader.getResource("mock_data/airline_passengers.csv")!!.readText()
        file = MockMultipartFile("airline_passengers.csv","airline_passengers.csv","text/csv",fileContent.toByteArray())
        secureDataSetStorageService.storeDataSet(file, dataSet)
        dataSet = DataSet(
                id = 3,
                name = "DS-3",
                fileName = "uspopulation.csv",
                description = "Populatie US",
                fileType = "text/csv"
        )
        fileContent = this::class.java.classLoader.getResource("mock_data/uspopulation.csv")!!.readText()
        file = MockMultipartFile("uspopulation.csv","uspopulation.csv","text/csv",fileContent.toByteArray())
        secureDataSetStorageService.storeDataSet(file, dataSet)
    }

}
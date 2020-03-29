package com.application.app.repository

import com.application.app.model.DataSet
import com.application.app.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DataSetRepository : JpaRepository<DataSet, Long>, JpaSpecificationExecutor<DataSet> {
    fun findByIdAndUserdata(id : Long, userData : UserData) : Optional<DataSet>
}
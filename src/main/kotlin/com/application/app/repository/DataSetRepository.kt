package com.application.app.repository

import com.application.app.model.DataSet
import com.application.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DataSetRepository : JpaRepository<DataSet, Long>, JpaSpecificationExecutor<DataSet> {
    fun findByIdAndCreatedBy(id : Long, user : User) : Optional<DataSet>
}
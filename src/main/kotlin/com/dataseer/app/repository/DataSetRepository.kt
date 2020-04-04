package com.dataseer.app.repository

import com.dataseer.app.model.DataSet
import com.dataseer.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Defines the [DataSet] repository
 * @author Blendica Vlad
 * @data 15.03.2020
 */
@Repository
interface DataSetRepository : JpaRepository<DataSet, Long>, JpaSpecificationExecutor<DataSet> {

    /**
     * Get a [DataSet] by ID with User security
     * @param id [Long]
     * @param user [User]
     * @return Optional<[DataSet]> DataSet
     */
    fun findByIdAndCreatedBy(id : Long, user : User) : Optional<DataSet>
}
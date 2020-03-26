package com.application.app.repository

import com.application.app.model.DataSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DataSetRepository : JpaRepository<DataSet, Long> {
}
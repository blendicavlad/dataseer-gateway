package com.application.app.repository.query_specifications

import com.application.app.model.DataSet
import com.application.app.model.UserData
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

object DataSetSpecifications {
    fun ofUserDetails(userData: UserData?): Specification<DataSet?> {
        return Specification {
            root: Root<DataSet?>,
            _: CriteriaQuery<*>?,
            criteriaBuilder: CriteriaBuilder
                -> criteriaBuilder.equal(root.get<Any>("userdata"), userData)
        }
    }
}
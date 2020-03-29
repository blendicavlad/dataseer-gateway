package com.application.app.repository.query_specifications

import com.application.app.model.DataSet
import com.application.app.model.User
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

object DataSetSpecifications {
    fun ofUser(userData: User?): Specification<DataSet?> {
        return Specification {
            root: Root<DataSet?>,
            _: CriteriaQuery<*>?,
            criteriaBuilder: CriteriaBuilder
                -> criteriaBuilder.equal(root.get<Any>("createdBy"), userData)
        }
    }
}
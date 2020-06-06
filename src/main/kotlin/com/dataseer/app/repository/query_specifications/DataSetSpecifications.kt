package com.dataseer.app.repository.query_specifications

import com.dataseer.app.model.DataSet
import com.dataseer.app.model.User
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * Custom query specifications to be injected for [DataSet] JPA queries
 * @author Blendica Vlad
 * @date 15.03.2020
 */
object DataSetSpecifications {

    val allowedFileTypes = listOf("text/csv")

    /**
     * Secure the [DataSet] queries by adding [User] constraint
     * @param user [User]
     * @return [Specification]
     */
    fun ofUser(user: User): Specification<DataSet> {
        return Specification{ root: Root<DataSet>,
                               _: CriteriaQuery<*>,
                               criteriaBuilder: CriteriaBuilder
            -> criteriaBuilder.equal(root.get<Any>("createdBy"), user)
        }
    }
}
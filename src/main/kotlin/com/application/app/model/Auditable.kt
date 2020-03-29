package com.application.app.model

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable {
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var createdBy: User? = null

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    var creationDate: Date? = null

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    var lastModifiedBy: User? = null

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    var lastModifiedDate: Date? = null

}
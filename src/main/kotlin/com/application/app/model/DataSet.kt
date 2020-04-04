package com.application.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * A DataSet consists of a file (for momment just CSV format) that will be consumed
 * by the time series analysis external service
 * @author Blendica Vlad
 * @date 14.03.2020
 */
@Entity
@Table(name = "datasets")
data class DataSet(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long? = null,

        @Column(nullable = false)
        var fileName : String,

        @Column
        var description : String,

        @Column(nullable = false)
        var fileType : String? = null,

        @JsonIgnore
        @Lob
        var data: ByteArray? = null

) : Auditable() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataSet

        if (id != other.id) return false
        if (fileName != other.fileName) return false
        if (description != other.description) return false
        if (fileType != other.fileType) return false
        if (data != null) {
            if (other.data == null) return false
            if (data!!.contentEquals(other.data!!)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + fileName.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (fileType?.hashCode() ?: 0)
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}
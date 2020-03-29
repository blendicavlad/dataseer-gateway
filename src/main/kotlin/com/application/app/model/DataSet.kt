package com.application.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

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

        var fileType : String? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name="userdata_id" , nullable = false)
        var userdata : UserData? = null,

        @JsonIgnore
        @Lob
        var data: ByteArray? = null,
        //todo de vazut cum sa fac sa se completeze astea automat
        @CreatedDate
        val createdDate : LocalDateTime? = null,

        @LastModifiedDate
        var lastModifiedDate : LocalDateTime? = null

) {

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
        if (createdDate != other.createdDate) return false
        if (lastModifiedDate != other.lastModifiedDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + fileName.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (fileType?.hashCode() ?: 0)
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + (createdDate?.hashCode() ?: 0)
        result = 31 * result + (lastModifiedDate?.hashCode() ?: 0)
        return result
    }
}
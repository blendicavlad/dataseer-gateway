package com.application.app.model

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

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name="userdata_id" , nullable = false)
        var userdata : UserData? = null,

        @Lob
        var data: ByteArray? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataSet

        if (id != other.id) return false
        if (fileName != other.fileName) return false
        if (fileType != other.fileType) return false
        if (userdata != other.userdata) return false
        if (data!!.contentEquals(other.data!!)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + fileType.hashCode()
        result = 31 * result + userdata.hashCode()
        result = 31 * result + data!!.contentHashCode()
        return result
    }
}
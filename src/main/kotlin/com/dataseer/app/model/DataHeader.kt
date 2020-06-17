package com.dataseer.app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "dataheader")
data class DataHeader(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long? = null,

        @Column(nullable = false, length = 32)
        var headerName : String? = null,

        @Column
        var isTimeIndex : Boolean = false,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name="datasets.id", nullable = false)
        var dataSet: DataSet? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataHeader) return false

        if (id != other.id) return false
        if (headerName != other.headerName) return false
        if (isTimeIndex != other.isTimeIndex) return false
        if (dataSet != other.dataSet) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (headerName?.hashCode() ?: 0)
        result = 31 * result + (isTimeIndex.hashCode() ?: 0)
        return result
    }
}
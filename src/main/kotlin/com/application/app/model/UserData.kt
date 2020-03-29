package com.application.app.model


import net.minidev.json.annotate.JsonIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "userdata")
data class UserData (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userdata_id: Long? = null,

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    val user : User? = null,

    @CreatedDate
    val createdDate : LocalDateTime? = null,

    @LastModifiedDate
    var lastModifiedDate : LocalDateTime? = null

) {
    @OneToMany(mappedBy = "userdata", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val datasets : MutableSet<DataSet> = mutableSetOf()
}
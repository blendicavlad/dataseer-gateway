package com.application.app.model

import javax.persistence.*

@Entity
@Table(name = "userdata")
data class UserData (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userdata_id: Long? = null,

    @OneToOne
    @MapsId
    val user : User? = null,

    @OneToMany(mappedBy = "userdata", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val datasets : MutableSet<DataSet> = mutableSetOf()

)
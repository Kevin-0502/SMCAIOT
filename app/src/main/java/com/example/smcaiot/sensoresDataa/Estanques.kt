package com.example.smaciot.sensoresDataa

import java.io.Serializable

data class Estanques(
    var MAC:String?=null,
    var estanque:String?=null,
    var nombre:String?=null,
    var tiempo:String?=null,
    var ubicacion:String?=null,
    var imagen:String?=null,
    var activo:Boolean?=null,
    var lecturas:Any?=null
):Serializable

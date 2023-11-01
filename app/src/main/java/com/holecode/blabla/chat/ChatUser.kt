package com.holecode.blabla.chat

import java.text.SimpleDateFormat
import java.util.Date

data class ChatUser (
    val image:String,
    val name:String,
    val message:String,
    val time:Date
){
    constructor() : this("", "", "", Date())
    fun formattedTime(): String {
        //dd/MM/yyyy HH:mm
        val dateFormat = SimpleDateFormat("HH:mm") // Customize the format as needed
        return dateFormat.format(time)
    }

}


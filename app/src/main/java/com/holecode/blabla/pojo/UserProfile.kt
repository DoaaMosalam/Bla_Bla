package com.holecode.blabla.pojo

data class UserProfile(
    val uid: String,
    val name: String,
    val status: String,
    var imageUrl: String
) {
    constructor() : this("", "", "", "")
}


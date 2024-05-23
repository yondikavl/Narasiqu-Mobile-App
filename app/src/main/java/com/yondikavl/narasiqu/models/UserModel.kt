package com.yondikavl.narasiqu.models

data class UserModel (
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)
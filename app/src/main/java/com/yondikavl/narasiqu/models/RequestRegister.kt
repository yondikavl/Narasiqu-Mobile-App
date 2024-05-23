package com.yondikavl.narasiqu.models

import com.google.gson.annotations.SerializedName

data class RequestRegister(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("password")
	val password: String? = null,
)

package com.yondikavl.narasiqu.models

import com.google.gson.annotations.SerializedName

data class ResponseUploadStory(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

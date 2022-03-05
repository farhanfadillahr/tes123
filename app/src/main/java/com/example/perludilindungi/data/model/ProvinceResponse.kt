package com.example.perludilindungi.data.model

import com.google.gson.annotations.SerializedName

data class ProvinceResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("curr_val")
	val currVal: String? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null
)

data class ResultsItem(

	@field:SerializedName("value")
	val value: String? = null,

	@field:SerializedName("key")
	val key: String? = null
)

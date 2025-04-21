package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.albuminformation.responsemodels

import com.google.gson.annotations.SerializedName


data class Album (
    @SerializedName("title"             ) var title            : String?              = null,
    @SerializedName("artist"            ) var artist           : String?              = null,
    @SerializedName("release_date"      ) var releaseDate      : String?              = null,
    @SerializedName("label"             ) var label            : String?              = null,
    @SerializedName("producer"          ) var producer         : String?              = null,
    @SerializedName("genre"             ) var genre            : ArrayList<String>    = arrayListOf(),
    @SerializedName("musicians"         ) var musicians        : ArrayList<Musicians> = arrayListOf(),
    @SerializedName("recording_details" ) var recordingDetails : RecordingDetails?    = RecordingDetails(),
    @SerializedName("background"        ) var background       : Background?          = Background(),
    @SerializedName("tracks"            ) var tracks           : ArrayList<Tracks>    = arrayListOf(),
    @SerializedName("reception"         ) var reception        : Reception?           = Reception()
)
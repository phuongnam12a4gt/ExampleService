package com.example.exampleservice.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(var title: String, var single: String, var image: Int, var resource: Int):Parcelable
{

}


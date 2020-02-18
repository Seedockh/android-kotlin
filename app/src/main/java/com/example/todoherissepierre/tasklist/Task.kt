package com.example.todoherissepierre.tasklist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Task (
    var id: String,
    var title: String,
    var description: String = ""
): Serializable, Parcelable
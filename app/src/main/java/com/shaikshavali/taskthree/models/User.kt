package com.shaikshavali.taskthree.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val images: ArrayList<String> = ArrayList(),
    var fcmToken: String = "",
    var location : String = "",
    var date : Long =0L,
    var latitude : Double =0.0,
    var longitude : Double = 0.0
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun describeContents(): Int =0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeStringList(images)
        writeString(fcmToken)
        writeString(location)
        writeLong(date)
        writeDouble(latitude)
        writeDouble(longitude)

    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}
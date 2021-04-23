package com.riski.githubuser.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username: String = "",
    var name: String = "",
    var avatar: String = "",
    var company: String = "",
    var location: String = "",
    var repository: String = "",
    var follower: String = "",
    var following: String = "",
    var totalFollower: Int = 0,
    var totalFollowing: Int = 0
) : Parcelable

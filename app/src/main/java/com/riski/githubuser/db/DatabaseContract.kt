package com.riski.githubuser.db

import android.provider.BaseColumns

class DatabaseContract {
    internal class UserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite_user"
            const val _ID = "_id"
            const val USERNAME = "username"
            const val NAME = "name"
            const val AVATAR = "avatar"
            const val COMPANY = "company"
            const val LOCATION = "location"
            const val REPOSITORY = "repository"
            const val FOLLOWER = "follower"
            const val FOLLOWING = "following"
            const val TOTAL_FOLLOWER = "total_Follower"
            const val TOTAL_FOLLOWING = "total_Following"
        }
    }
}
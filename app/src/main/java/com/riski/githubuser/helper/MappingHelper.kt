package com.riski.githubuser.helper

import android.database.Cursor
import com.riski.githubuser.db.DatabaseContract
import com.riski.githubuser.user.User

object MappingHelper {
    fun mapCursorToArrayList(userCursor: Cursor?): ArrayList<User> {
        val userList = ArrayList<User>()

        userCursor?.apply {
            while (moveToNext()) {
                val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
                val name = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.NAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR))
                val company = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COMPANY))
                val location = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.LOCATION))
                val repository = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.REPOSITORY))
                val follower = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.FOLLOWER))
                val following = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.FOLLOWING))
                val totalFollower = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns.TOTAL_FOLLOWER))
                val totalFollowing = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns.TOTAL_FOLLOWING))
                userList.add(User(username, name, avatar, company, location, repository, follower, following, totalFollower, totalFollowing))
            }
        }
        return userList
    }
}
package com.riski.consumerapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.riski.consumerapp.user.User
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel : ViewModel() {

    val listUser = MutableLiveData<ArrayList<User>>()
    private val token = "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"

    fun setUser(username: String, context: Context) {
        val listItem = ArrayList<User>()
        val url = "https://api.github.com/search/users?q=${username}"
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                try {
                    val result = String(responseBody!!)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("items")

                    for (i in 0 until list.length()) {
                        val user = list.getJSONObject(i)
                        val userItems = User()
                        userItems.username = user.getString("login")
                        userItems.avatar = user.getString("avatar_url")
                        userItems.follower = user.getString("followers_url")
                        userItems.following = user.getString("following_url")

                        countFollowers(userItems.username, userItems, context)
                        countFollowing(userItems.username, userItems, context)

                        val userUrl = user.getString("url")
                        client.get(userUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val userResult = String(responseBody!!)
                                    val userResponseObject = JSONObject(userResult)

                                    userItems.name = userResponseObject.getString("name")
                                    userItems.company = userResponseObject.getString("company")
                                    userItems.location = userResponseObject.getString("location")

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess user: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "user : ${error?.message.toString()}")
                            }
                        })

                        val repositoryUrl = user.getString("repos_url")
                        client.get(repositoryUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val repoResult = String(responseBody!!)
                                    val repoResponseObject = JSONArray(repoResult)

                                    for (index in 0 until repoResponseObject.length()) {
                                        userItems.repository = index.toString()
                                    }

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess repo: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "repo : ${error?.message.toString()}")
                            }
                        })

                        listItem.add(userItems)
                    }

                    listUser.postValue(listItem)
                } catch (e: Exception) {
                    Log.d("Exception : setUser()", "onSuccess: ${e.message.toString()}")
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("onFailure : setUser()", error?.message.toString())
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                setUser(username, context)
            }

        })
    }

    fun getUser(context: Context): LiveData<ArrayList<User>> {
        val listItem = ArrayList<User>()
        val url = "https://api.github.com/users"
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                try {
                    val result = String(responseBody!!)
                    val responseObject = JSONArray(result)

                    for (i in 0 until responseObject.length()) {
                        val user = responseObject.getJSONObject(i)
                        val userItems = User()

                        userItems.username = user.getString("login")
                        userItems.avatar = user.getString("avatar_url")

                        countFollowers(userItems.username, userItems, context)
                        countFollowing(userItems.username, userItems, context)

                        val userUrl = user.getString("url")
                        client.get(userUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val userResult = String(responseBody!!)
                                    val userResponseObject = JSONObject(userResult)

                                    userItems.name = userResponseObject.getString("name")
                                    userItems.company = userResponseObject.getString("company")
                                    userItems.location = userResponseObject.getString("location")

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess user: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "user : ${error?.message.toString()}")
                            }
                        })

                        val repositoryUrl = user.getString("repos_url")
                        client.get(repositoryUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val repoResult = String(responseBody!!)
                                    val repoResponseObject = JSONArray(repoResult)

                                    userItems.repository = repoResponseObject.length().toString()

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess repo: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "repo : ${error?.message.toString()}")
                            }
                        })

                        listItem.add(userItems)
                    }

                    listUser.postValue(listItem)
                } catch (e: Exception) {
                    Log.d("Exception : getUser()", "onSuccess: ${e.message.toString()}")
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("onFailure: getUser()", error?.message.toString())
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                getUser(context)
            }

        })

        return listUser
    }

    fun getFollowers(username: String, context: Context): LiveData<ArrayList<User>> {
        val listItem = ArrayList<User>()
        val url = "https://api.github.com/users/${username}/followers"
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                try {
                    val followerResult = String(responseBody!!)
                    val followerResponseObject = JSONArray(followerResult)

                    for (index in 0 until followerResponseObject.length()) {
                        val user = followerResponseObject.getJSONObject(index)
                        val userItems = User()

                        userItems.username = user.getString("login")
                        userItems.avatar = user.getString("avatar_url")
                        userItems.follower = user.getString("followers_url")
                        userItems.following = user.getString("following_url")

                        countFollowers(userItems.username, userItems, context)
                        countFollowing(userItems.username, userItems, context)

                        val userUrl = user.getString("url")
                        client.get(userUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val userResult = String(responseBody!!)
                                    val userResponseObject = JSONObject(userResult)

                                    userItems.name = userResponseObject.getString("name")
                                    userItems.company = userResponseObject.getString("company")
                                    userItems.location = userResponseObject.getString("location")

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess user: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "user : ${error?.message.toString()}")
                            }
                        })

                        val repositoryUrl = user.getString("repos_url")
                        client.get(repositoryUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val repoResult = String(responseBody!!)
                                    val repoResponseObject = JSONArray(repoResult)

                                    for (indx in 0 until repoResponseObject.length()) {
                                        userItems.repository = indx.toString()
                                    }

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess repo: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "repo : ${error?.message.toString()}")
                            }
                        })

                        listItem.add(userItems)
                    }

                    listUser.postValue(listItem)
                } catch (e: Exception) {
                    Log.d("Exception: getFollwrs()", "onSuccess followers: ${e.message.toString()}")
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("onFailure: getFollwrs()", error?.message.toString())
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                getFollowers(username, context)
            }

        })
        return listUser
    }

    fun getFollowing(username: String, context: Context): LiveData<ArrayList<User>> {
        val listItem = ArrayList<User>()
        val url = "https://api.github.com/users/${username}/following"
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                try {
                    val followerResult = String(responseBody!!)
                    val followerResponseObject = JSONArray(followerResult)

                    for (i in 0 until followerResponseObject.length()) {
                        val user = followerResponseObject.getJSONObject(i)
                        val userItems = User()

                        userItems.username = user.getString("login")
                        userItems.avatar = user.getString("avatar_url")
                        userItems.follower = user.getString("followers_url")
                        userItems.following = user.getString("following_url")

                        countFollowers(userItems.username, userItems, context)
                        countFollowing(userItems.username, userItems, context)

                        val userUrl = user.getString("url")
                        client.get(userUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val userResult = String(responseBody!!)
                                    val userResponseObject = JSONObject(userResult)

                                    userItems.name = userResponseObject.getString("name")
                                    userItems.company = userResponseObject.getString("company")
                                    userItems.location = userResponseObject.getString("location")

                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess user: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "user : ${error?.message.toString()}")
                            }
                        })

                        val repositoryUrl = user.getString("repos_url")
                        client.get(repositoryUrl, object : AsyncHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                                try {
                                    val repoResult = String(responseBody!!)
                                    val repoResponseObject = JSONArray(repoResult)

                                    for (index in 0 until repoResponseObject.length()) {
                                        userItems.repository = index.toString()
                                    }
                                } catch (e: Exception) {
                                    Log.d("Exception : getUser()", "onSuccess repo: ${e.message.toString()}")
                                }
                            }

                            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                                Log.d("onFailure: getUser()", "repo : ${error?.message.toString()}")
                            }
                        })

                        listItem.add(userItems)
                    }

                    listUser.postValue(listItem)

                } catch (e: Exception) {
                    Log.d("Exception: getFllwng()", "onSuccess followers: ${e.message.toString()}")
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("onFailure: getFllwng()", error?.message.toString())
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                getFollowing(username, context)
            }

        })
        return listUser
    }

    fun countFollowers(username: String, user: User, context: Context) {

        val url = "https://api.github.com/users/${username}/followers"
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                try {
                    val followerResult = String(responseBody!!)
                    val followerResponseObject = JSONArray(followerResult)

                    user.totalFollower = followerResponseObject.length()

                } catch (e: Exception) {
                    Log.d("Exception: countFollwrs", "onSuccess followers: ${e.message.toString()}")
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("onFailure: countFollwrs", error?.message.toString())
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                countFollowers(username, user, context)
            }

        })
    }

    fun countFollowing(username: String, user: User, context: Context) {

        val url = "https://api.github.com/users/${username}/following"
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                try {
                    val followingResult = String(responseBody!!)
                    val followingResponseObject = JSONArray(followingResult)

                    user.totalFollowing = followingResponseObject.length()

                } catch (e: Exception) {
                    Log.d("Exception: countFllwng", "onSuccess followers: ${e.message.toString()}")
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.d("onFailure: countFllwng", error?.message.toString())
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                countFollowing(username, user, context)
            }

        })
    }

}
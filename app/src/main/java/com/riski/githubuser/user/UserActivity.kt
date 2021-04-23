package com.riski.githubuser.user

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.riski.githubuser.MainViewModel
import com.riski.githubuser.R
import com.riski.githubuser.follow.SectionsPagerAdapter
import com.riski.githubuser.databinding.ActivityUserBinding
import com.riski.githubuser.db.DatabaseContract
import com.riski.githubuser.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.riski.githubuser.favorite.ListFavoriteAdapter
import com.riski.githubuser.helper.MappingHelper

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var favoriteAdapter: ListFavoriteAdapter
    private lateinit var uriWithId: Uri

    private var position = 0
    private var user: User? = null

    companion object {
        const val EXTRA_DATA = ""
        const val EXTRA_POSITION = "extra_position"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.app_name_user)

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        favoriteAdapter = ListFavoriteAdapter(this)

        user = intent.getParcelableExtra(EXTRA_DATA)
        if (user != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
        } else {
            user = User()
        }

        Glide.with(this)
            .load(user?.avatar)
            .into(binding.imgDetailUser)

        val follow = "${user?.totalFollower} ${resources.getString(R.string.followers)}"
        val following = "${user?.totalFollowing} ${resources.getString(R.string.following)}"
        val repo = "${user?.repository} ${resources.getString(R.string.repository)}"

        binding.tvDetailName.text = user?.name
        binding.tvDetailUsername.text = user?.username
        binding.tvDetailCompany.text = user?.company
        binding.tvDetailLocation.text = user?.location
        binding.tvDetailFollowers.text = follow
        binding.tvDetailFollowing.text = following
        binding.tvDetailRepository.text = repo

        uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user?.username)
        var isFavorite = false

        val cursor = contentResolver.query(uriWithId, null, null, null, null)
        if (cursor != null) {
            val users = MappingHelper.mapCursorToArrayList(cursor)
            isFavorite = if (users.size > 0) {
                binding.fabFavorite.setImageResource(R.drawable.ic_baseline_close_24)
                true
            } else {
                binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                false
            }
        }

        binding.fabFavorite.setOnClickListener {
            if (isFavorite) {
                contentResolver.delete(uriWithId, null, null)
                isFavorite = !isFavorite
                binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            } else {
                val values = ContentValues()
                values.put(DatabaseContract.UserColumns.USERNAME, user?.username)
                values.put(DatabaseContract.UserColumns.NAME, user?.name)
                values.put(DatabaseContract.UserColumns.AVATAR, user?.avatar)
                values.put(DatabaseContract.UserColumns.COMPANY, user?.company)
                values.put(DatabaseContract.UserColumns.LOCATION, user?.location)
                values.put(DatabaseContract.UserColumns.REPOSITORY, user?.repository)
                values.put(DatabaseContract.UserColumns.FOLLOWER, user?.follower)
                values.put(DatabaseContract.UserColumns.FOLLOWING, user?.following)
                values.put(DatabaseContract.UserColumns.TOTAL_FOLLOWER, user?.totalFollower)
                values.put(DatabaseContract.UserColumns.TOTAL_FOLLOWING, user?.totalFollowing)

                contentResolver.insert(CONTENT_URI, values)
                isFavorite = !isFavorite
                binding.fabFavorite.setImageResource(R.drawable.ic_baseline_close_24)
            }
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

}
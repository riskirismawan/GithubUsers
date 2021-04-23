package com.riski.githubuser.user

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
import com.riski.githubuser.db.UserHelper
import com.riski.githubuser.favorite.ListFavoriteAdapter
import com.riski.githubuser.helper.MappingHelper
import kotlinx.coroutines.*

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var favoriteHelper: UserHelper
    private lateinit var favoriteAdapter: ListFavoriteAdapter

    private var position = 0
    private var user: User? = null

    companion object {
        const val EXTRA_DATA = ""
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_UPDATE = 200
        const val RESULT_DELETE = 301

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
        favoriteHelper = UserHelper.getInstance(applicationContext)
        favoriteHelper.open()

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

        var isFavorite = false
        favoriteHelper.open()
        CoroutineScope(Dispatchers.IO).launch {
            val cursor = user?.username?.let { favoriteHelper.queryById(it) }
            val item = MappingHelper.mapCursorToArrayList(cursor)
            withContext(Dispatchers.Main) {
                if (item.size > 0) {
                    binding.fabFavorite.setImageResource(R.drawable.ic_baseline_close_24)
                    isFavorite = true
                } else {
                    binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                    isFavorite = false
                }
            }
        }

        binding.fabFavorite.setOnClickListener {
            if (isFavorite) {
                val result = user?.username?.let { it1 -> favoriteHelper.deleteById(it1) }
                if (result != null) {
                    if (result > 0) {
                        val intent = Intent()
                        intent.putExtra(EXTRA_POSITION, position)
                        setResult(RESULT_DELETE, intent)
                        isFavorite = !isFavorite
                        binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                    } else {
                        Toast.makeText(this@UserActivity, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }
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

                val result = favoriteHelper.insert(values)
                if (result > 0) {
                    isFavorite = !isFavorite
                    binding.fabFavorite.setImageResource(R.drawable.ic_baseline_close_24)
                } else {
                    Toast.makeText(this@UserActivity, "Gagal menambah data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

}
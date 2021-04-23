package com.riski.githubuser.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.riski.githubuser.R
import com.riski.githubuser.databinding.ActivityFavoriteBinding
import com.riski.githubuser.db.UserHelper
import com.riski.githubuser.helper.MappingHelper
import com.riski.githubuser.user.User
import com.riski.githubuser.user.UserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: ListFavoriteAdapter

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ListFavoriteAdapter(this)

        supportActionBar?.title = resources.getString(R.string.favorite)

        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.adapter = adapter
        binding.rvFavorite.setHasFixedSize(true)

        if (savedInstanceState == null) {
            loadFavoriteAsync(adapter)
        } else {
            val list = savedInstanceState.getParcelableArrayList<User>(EXTRA_STATE)
            if (list != null) {
                adapter.mData = list
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvFavorite.visibility = View.INVISIBLE
        }
        else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.rvFavorite.visibility = View.VISIBLE
        }
    }

    private fun loadFavoriteAsync(adapter: ListFavoriteAdapter) {
        GlobalScope.launch(Dispatchers.Main) {
            showLoading(true)
            val favoriteHelper = UserHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val diferredUsers = async(Dispatchers.IO) {
                val cursor = favoriteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            showLoading(false)
            val users = diferredUsers.await()
            if (users.size > 0) {
                adapter.setData(users)
            } else {
                adapter.setData(ArrayList())
                Snackbar.make(binding.rvFavorite, resources.getString(R.string.no_data), Snackbar.LENGTH_SHORT).show()
            }
            favoriteHelper.close()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.mData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                UserActivity.REQUEST_UPDATE ->
                    when (resultCode) {
                        UserActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(UserActivity.EXTRA_POSITION, 0)
                            adapter.removeItem(position)
                        }
                    }
            }
        }
    }
}
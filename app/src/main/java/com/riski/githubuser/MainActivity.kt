package com.riski.githubuser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.riski.githubuser.databinding.ActivityMainBinding
import com.riski.githubuser.favorite.FavoriteActivity
import com.riski.githubuser.reminder.ReminderActivity
import com.riski.githubuser.user.User
import com.riski.githubuser.user.UserActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: ListUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.app_name)

        adapter = ListUserAdapter()

        if (savedInstanceState == null) {
            showRecyclerList()
        } else {
            val list = savedInstanceState.getParcelableArrayList<User>(EXTRA_STATE)
            if (list != null) {
                adapter.setData(list)
            }
        }

    }

    private fun showRecyclerList() {
        binding.rvGithubUser.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = ListUserAdapter()
        binding.rvGithubUser.adapter = listUserAdapter
        binding.rvGithubUser.setHasFixedSize(true)

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.getUser(this).observe(this, {
            if ( it != null) {
                listUserAdapter.setData(it)
                showLoading(false)
            }
        })

        listUserAdapter.setOnClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(data: User) {
        val userDetailIntent = Intent(this, UserActivity::class.java)
        userDetailIntent.putExtra(UserActivity.EXTRA_DATA, data)

        startActivity(userDetailIntent)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvGithubUser.visibility = View.INVISIBLE
        }
        else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.rvGithubUser.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.mData)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                showLoading(true)
                mainViewModel.setUser(p0!!, this@MainActivity)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    showLoading(true)
                    mainViewModel.getUser(this@MainActivity)
                } else {
                    return false
                }
                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.favorite -> {
                val mIntent = Intent(this, FavoriteActivity::class.java)
                startActivity(mIntent)
            }
            R.id.reminder -> {
                val mIntent = Intent(this, ReminderActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
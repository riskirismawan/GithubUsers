package com.riski.consumerapp.follow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.riski.consumerapp.MainViewModel
import com.riski.consumerapp.R
import com.riski.consumerapp.databinding.FragmentFollowBinding
import com.riski.consumerapp.ListUserAdapter
import com.riski.consumerapp.user.User
import com.riski.consumerapp.user.UserActivity

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private lateinit var mainViewModel: MainViewModel

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(index: Int) = FollowFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, index)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)

        index?.let { showRecyclerList(it) }
    }

    private fun showRecyclerList(index: Int) {
        val extraData: TextView? = activity?.findViewById(R.id.tv_detail_username)

        binding.rvFollow.layoutManager = LinearLayoutManager(this.context)
        val listFollowAdapter = ListFollowAdapter()
        binding.rvFollow.adapter = listFollowAdapter

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        when (index) {
            1 -> {
                mainViewModel.getFollowers(extraData?.text.toString(), this.requireContext())
                        .observe(this.viewLifecycleOwner, {
                            if (it != null) {
                                listFollowAdapter.setData(it)
                                if (it.isNullOrEmpty()) {
                                    val info = "${getString(R.string.no)} ${getString(R.string.followers)}"
                                    binding.tvInfo.text = info
                                    binding.tvInfo.visibility = View.VISIBLE
                                }
                                showLoading(false)
                            }
                        })
            }
            2 -> {
                mainViewModel.getFollowing(extraData?.text.toString(), this.requireContext())
                        .observe(this.viewLifecycleOwner, {
                    if ( it != null) {
                        listFollowAdapter.setData(it)
                        if (it.isNullOrEmpty()) {
                            val info = "${getString(R.string.no)} ${getString(R.string.following)}"
                            binding.tvInfo.text = info
                            binding.tvInfo.visibility = View.VISIBLE
                        }
                        showLoading(false)
                    }
                })
            }
        }

        listFollowAdapter.setOnClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(data: User) {
        val userDetailIntent = Intent(this.context, UserActivity::class.java)
        userDetailIntent.putExtra(UserActivity.EXTRA_DATA, data)

        startActivity(userDetailIntent)
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.INVISIBLE
    }
}
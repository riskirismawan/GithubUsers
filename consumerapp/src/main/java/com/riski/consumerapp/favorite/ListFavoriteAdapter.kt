package com.riski.consumerapp.favorite

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.riski.consumerapp.R
import com.riski.consumerapp.ListUserAdapter
import com.riski.consumerapp.databinding.ItemFavoriteBinding
import com.riski.consumerapp.user.User
import com.riski.consumerapp.user.UserActivity

class ListFavoriteAdapter(private val activity: Activity) : RecyclerView.Adapter<ListFavoriteAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: ListUserAdapter.OnItemClickCallback
    var mData = ArrayList<User>()
        set(mData) {
            if (mData.size > 0) {
                this.mData.clear()
            }
            this.mData.addAll(mData)
            notifyDataSetChanged()
        }

    fun setData(item: ArrayList<User>) {
        this.mData.clear()
        this.mData.addAll(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickCallback.onClicked(mData[holder.adapterPosition])
        }
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemFavoriteBinding.bind(itemView)

        fun bind(user: User) {
            with (itemView) {
                Glide.with(context)
                        .load(user.avatar)
                        .apply(RequestOptions().override(55, 55))
                        .into(binding.imgUser)

                val followers = "${resources.getString(R.string.followers)} : ${user.totalFollower}"
                val following = "${resources.getString(R.string.following)} : ${user.totalFollowing}"

                binding.tvName.text = user.name
                binding.tvFollower.text = followers
                binding.tvFollowing.text = following
                binding.cvItemFavorite.setOnClickListener(CustomOnItemClickListener(adapterPosition, object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View?, position: Int) {
                        val intent = Intent(activity, UserActivity::class.java)
                        intent.putExtra(UserActivity.EXTRA_POSITION, position)
                        intent.putExtra(UserActivity.EXTRA_DATA, user)
                        activity.startActivity(intent)
                    }
                }))

            }
        }
    }
}
package com.riski.consumerapp.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.riski.consumerapp.R
import com.riski.consumerapp.databinding.ItemViewBinding
import com.riski.consumerapp.ListUserAdapter
import com.riski.consumerapp.user.User

class ListFollowAdapter : RecyclerView.Adapter<ListFollowAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: ListUserAdapter.OnItemClickCallback
    private var mData = ArrayList<User>()

    fun setData(item: ArrayList<User>) {
        mData.clear()
        mData.addAll(item)
        notifyDataSetChanged()
    }

    fun setOnClickCallback(onItemClickCallback: ListUserAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
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

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemViewBinding.bind(itemView)

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
            }
        }
    }
}
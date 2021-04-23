package com.riski.githubuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.riski.githubuser.databinding.ItemViewBinding
import com.riski.githubuser.user.User

class ListUserAdapter(): RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    var mData = ArrayList<User>()
        set(mData) {
            if (mData.size > 0) {
                this.mData.clear()
            }
            this.mData.addAll(mData)
            notifyDataSetChanged()
        }

    fun setData(item: ArrayList<User>) {
        mData.clear()
        mData.addAll(item)
        notifyDataSetChanged()
    }

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback) {
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

    interface OnItemClickCallback {
        fun onClicked(data: User)
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
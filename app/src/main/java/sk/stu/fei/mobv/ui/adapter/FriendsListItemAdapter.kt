package sk.stu.fei.mobv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stu.fei.mobv.databinding.FriendsListItemBinding
import sk.stu.fei.mobv.domain.Friend


class FriendsListItemAdapter(
    private val friendsListItemEventListener: FriendsListItemEventListener
) : ListAdapter<Friend, FriendsListItemAdapter.FriendsListItemViewHolder>(DiffCallback) {

    class FriendsListItemViewHolder(var binding: FriendsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friendsListItemEventListener: FriendsListItemEventListener, friend: Friend) {
            binding.friend = friend
            binding.friendsListItemEventListener = friendsListItemEventListener
        }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListItemViewHolder {
        return FriendsListItemViewHolder(
            FriendsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FriendsListItemViewHolder, position: Int) {
        val friend: Friend = getItem(position)
        holder.bind(friendsListItemEventListener, friend)
    }
}

class FriendsListItemEventListener(val onItemClickListener: (barId: Long?) -> Unit) {
    fun onItemClick(barId: Long?) = onItemClickListener(barId)

}
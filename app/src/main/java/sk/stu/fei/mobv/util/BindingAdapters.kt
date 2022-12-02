package sk.stu.fei.mobv.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.domain.Friend
import sk.stu.fei.mobv.ui.adapter.BarsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.FriendsListItemAdapter


@BindingAdapter("barListData")
fun setBarList(recyclerView: RecyclerView, barListData: List<Bar>?) {
    val adapter = recyclerView.adapter as BarsListItemAdapter
    adapter.submitList(barListData)
}
@BindingAdapter("friendListData")
fun setFriendList(recyclerView: RecyclerView, friendListData: List<Friend>?) {
    val adapter = recyclerView.adapter as FriendsListItemAdapter
    adapter.submitList(friendListData)
}

package com.letsbuildthatapp.foodlocker.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentNewMessageBinding
import com.letsbuildthatapp.foodlocker.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageFragment : Fragment() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private lateinit var binding: FragmentNewMessageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity?.intent?.putExtra("ROOT", "NEW_MESSAGE_FRAGMENT")
        activity?.title = "New Chat"

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_new_message, container, false)

        fetchUsers()

        return binding.root
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    // intent.putExtra(USER_KEY,  userItem.user.username)
                    activity?.intent?.putExtra(USER_KEY, userItem.user)

                    view.findNavController().navigate((R.id.action_newMessageFragment_to_chatLogFragment))

                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username
        if (user.profileImageUrl != "")
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

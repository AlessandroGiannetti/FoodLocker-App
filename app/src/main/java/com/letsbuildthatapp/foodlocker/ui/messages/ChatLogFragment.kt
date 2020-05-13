package com.letsbuildthatapp.foodlocker.ui.messages

import LatestMessagesFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentChatLogBinding
import com.letsbuildthatapp.foodlocker.models.ChatMessage
import com.letsbuildthatapp.foodlocker.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_log.*

private const val TAG = "CHAT LOG FRAGMENT"

class ChatLogFragment : Fragment() {
    private lateinit var binding: FragmentChatLogBinding

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.intent?.putExtra("ROOT", "CHAT_LOG_FRAGMENT")

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_chat_log, container, false)

        binding.recyclerviewChatLog.adapter = adapter


        toUser = activity?.intent?.getParcelableExtra<User>(NewMessageFragment.USER_KEY)

        val activity = activity
        if (activity is MessagesActivity)
            activity.setTitleToolbar(toUser!!.username)

        listenForMessages()

        binding.sendButtonChatLog.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }

        return binding.root
    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesFragment.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                try {
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                } catch (e: Exception) {
                    Log.d(TAG, "$e")
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = binding.edittextChatLog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = activity?.intent?.getParcelableExtra<User>(NewMessageFragment.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

//    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = toId?.let { ChatMessage(reference.key!!, text, fromId, it, System.currentTimeMillis() / 1000) }

        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    binding.edittextChatLog.text.clear()
                    binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
                }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentLatestMessagesBinding
import com.letsbuildthatapp.foodlocker.models.ChatMessage
import com.letsbuildthatapp.foodlocker.models.User
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.ui.messages.LatestMessageRow
import com.letsbuildthatapp.foodlocker.ui.messages.LatestMessagesViewModel
import com.letsbuildthatapp.foodlocker.ui.messages.NewMessageFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder


class LatestMessagesFragment : Fragment() {

    private lateinit var binding: FragmentLatestMessagesBinding
    private lateinit var viewModel: LatestMessagesViewModel
    private val adapter = GroupAdapter<ViewHolder>()
    val latestMessagesMap = HashMap<String, ChatMessage>()

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter.clear()
        activity?.title = "Chat"

        activity?.intent?.putExtra("ROOT", "LATEST_MESSAGE_FRAGMENT")

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_latest_messages, container, false)

        viewModel =
                ViewModelProvider(this).get(LatestMessagesViewModel::class.java)

        binding.recyclerviewLatestMessages.adapter = adapter
        binding.recyclerviewLatestMessages.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))

        binding.newMessage.setOnClickListener {
            view?.findNavController()?.navigate((R.id.action_latestMessagesFragment_to_newMessageFragment))
        }

        // set item click listener on your adapter
        adapter.setOnItemClickListener { item, _ ->

            val row = item as LatestMessageRow
            activity?.intent?.putExtra(NewMessageFragment.USER_KEY, row.chatPartnerUser)
            view?.findNavController()?.navigate((R.id.action_latestMessagesFragment_to_chatLogFragment))
        }

        listenForLatestMessages()
        currentUser = FirebaseDBMng.userDBinfo.value

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideKeyboard()
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        adapter.notifyItemChanged(0)
        // !! MAYBE BUG?? previous vers: latestMessagesMap.values.forEach{ adapter.add(LatestMessageRow(it))}
        val chats = latestMessagesMap.toList().sortedByDescending { (_, value) -> value.timestamp }
        chats.forEach {
            adapter.add(LatestMessageRow(it.second))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    // hide keyboard
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
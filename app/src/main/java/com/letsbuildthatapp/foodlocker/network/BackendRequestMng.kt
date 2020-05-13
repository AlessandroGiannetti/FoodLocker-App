package com.letsbuildthatapp.foodlocker.network

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.User
import com.letsbuildthatapp.foodlocker.models.UserProperty
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity
import com.letsbuildthatapp.foodlocker.ui.home.WritePostActivity
import com.letsbuildthatapp.foodlocker.ui.progress.WriteWeightActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

private const val defaultImgUri = "https://firebasestorage.googleapis.com/v0/b/foodlocker-5791d.appspot.com/o/images%2F57f9a92c-d1a8-4dd0-b265-5125f865e6aa?alt=media&token=d720da22-7164-48aa-8dfc-ef5bc7d1221b"

private const val TAG = "BACKEND REQUEST MANAGEMENT"

object FirebaseDBMng {
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _userDBinfo = MutableLiveData<User?>()
    val userDBinfo: LiveData<User?>
        get() = _userDBinfo

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var dbReference: DatabaseReference

    lateinit var followings: MutableList<UserProperty>

    /**
     * FIREBASE API MANAGEMENT
     */

    // initialization of the current user's observer on the firebase real database #called on LoggedActivity#
    fun initFirebaseDB() {
        dbReference = FirebaseDatabase.getInstance().getReference("/users/${auth.currentUser?.uid}")
        dbReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.getValue(User::class.java)?.let { _userDBinfo.value = it }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun getTokenID(): String {
        var token: String? = ""
        auth.currentUser?.getIdToken(true)
                ?.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful)
                        return@OnCompleteListener

                    // Get new Instance ID token
                    token = task.result?.token
                    Log.e("token", token.toString())
                })
        return token.toString()
    }

    // setting extra user information (second stage after the sign in)
    fun setExtraInfo(dateOfBirthday: String, gender: String, height_m: Int, weight_kg: Int, target_weight_kg: Int, water: Int, weakly_hour_sport: Int, daily_kcal: Int) {

        val refDB = FirebaseDatabase.getInstance().getReference("/users/${auth.currentUser?.uid}")

        refDB.child("dateOfBirthday").setValue(dateOfBirthday)
        refDB.child("gender").setValue(gender)
        refDB.child("height_m").setValue(height_m)
        refDB.child("weight_kg").setValue(weight_kg)
        refDB.child("target_weight_kg").setValue(target_weight_kg)
        refDB.child("water").setValue(water)
        refDB.child("weakly_hour_sport").setValue(weakly_hour_sport)
        refDB.child("daily_kcal").setValue(daily_kcal)
        refDB.child("complete").setValue(true)
    }


    // downloading and loading the user's profile image into CircleImageView
    fun showCircularPhoto(uid: String, imageViewProfilePost: CircleImageView) {
        var userUid = uid
        if (uid == "personal")
            userUid = auth.currentUser?.uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.getValue(User::class.java)?.let {
                    Picasso.get().load(it.profileImageUrl).placeholder(R.drawable.loading_animation).error(R.drawable.ic_broken_image).into(imageViewProfilePost)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    fun saveUserOnFirebaseDatabase(view: View?, username: String, photoUri: Uri?, social: Boolean) {
        val uri = photoUri?.toString() ?: defaultImgUri
        val currentUser = auth.currentUser
        val dbReference = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}")

        dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.getValue(User::class.java).let {
                    when {
                        it == null -> {
                            if (!social && photoUri != null) {
                                val filename = UUID.randomUUID().toString()
                                val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

                                // asynch request for save the img
                                ref.putFile(photoUri).addOnSuccessListener {
                                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                                    ref.downloadUrl.addOnSuccessListener { uriImgSaved ->
                                        Log.d(TAG, "File Location: $uri")

                                        val user = User(currentUser?.uid!!, username, uriImgSaved.toString(), "", "", 0, 0, 0, 0, 0, 0, false)
                                        dbReference.setValue(user)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "user saved on firebase database")
                                                    saveUserOnBackend(currentUser.uid, username, uri.toString())
                                                    Toast.makeText(view?.context, "Recorded correctly, we're almost there.", Toast.LENGTH_SHORT).show()
                                                    if (social)
                                                        view?.findNavController()?.navigate((R.id.action_loginFragment_to_extraInfoRegistration))
                                                    else // only if the user perform the sign in manually
                                                        view?.findNavController()?.navigate((R.id.action_registrationFragment_to_extraInfoRegistration))
                                                }
                                    }
                                }
                                        .addOnFailureListener {
                                            Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                                        }
                            } else {
                                val user = User(currentUser?.uid!!, username, uri, "", "", 0, 0, 0, 0, 0, 0, false)
                                dbReference.setValue(user)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "user saved on firebase database")
                                            saveUserOnBackend(currentUser.uid, username, uri)
                                            Toast.makeText(view?.context, "Recorded correctly, we're almost there.", Toast.LENGTH_SHORT).show()
                                            if (social)
                                                view?.findNavController()?.navigate((R.id.action_loginFragment_to_extraInfoRegistration))
                                            else // only if the user perform the sign in manually
                                                view?.findNavController()?.navigate((R.id.action_registrationFragment_to_extraInfoRegistration))
                                        }
                            }
                        }
                        it.complete -> { // if the user is correctly registered (only for social access)
                            Toast.makeText(view?.context, "Successfully logged in", Toast.LENGTH_SHORT).show()
                            view?.findNavController()?.navigate((R.id.action_loginFragment_to_loggedActivity))
                        }
                        else -> {
                            Toast.makeText(view?.context, "Recorded correctly, we're almost there.", Toast.LENGTH_SHORT).show()
                            if (social)
                                view?.findNavController()?.navigate((R.id.action_loginFragment_to_extraInfoRegistration))
                            else // only if the user perform the sign in manually
                                view?.findNavController()?.navigate((R.id.action_registrationFragment_to_extraInfoRegistration))
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }


    fun resetUserInfo() {
        _userDBinfo.value = null
    }

    /**
     * HEROKU API MANAGEMENT
     */

    private fun saveUserOnBackend(uuid: String, username: String, uri: String) {
        coroutineScope.launch {
            val setUserDeferred = UserApi.retrofitServiceSetUser.setUser(uuid, username, uri)
            try {
                val result = setUserDeferred.await()
                Log.e(TAG, "$result")
            } catch (e: Exception) {
                Log.e(TAG, "$e")
            }
        }
    }

    fun savePostOnBackend(uri: Uri?, postActivity: WritePostActivity, content: String?): Boolean {
        var operationDone = true
        if (uri == null) {
            // saving post without image
            coroutineScope.launch {
                val getPropertiesDeferred = PostApi.retrofitServiceSetPost.setPost(auth.currentUser?.uid.toString(), content!!, null, 0)
                try {

                    val result = getPropertiesDeferred.await()
                    Log.e(TAG, "$result")
                    val myIntent = Intent(postActivity, LoggedActivity::class.java)
                    postActivity.startActivity(myIntent)

                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                    operationDone = false
                }
            }
        } else {
            // save post and image
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(uri).addOnSuccessListener {
                Log.e(TAG, "Successfully uploaded post image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.e(TAG, "Post image Location: $uri")
                    coroutineScope.launch {
                        val getPropertiesDeferred = PostApi.retrofitServiceSetPost.setPost(auth.currentUser?.uid.toString(), content!!, uri.toString(), 0)
                        try {
                            val result = getPropertiesDeferred.await()
                            Log.e(TAG, "$result")
                            val myIntent = Intent(postActivity, LoggedActivity::class.java)
                            postActivity.startActivity(myIntent)

                        } catch (e: Exception) {
                            Log.e(TAG, "$e")
                            operationDone = false
                        }
                    }
                }
            }
                    .addOnFailureListener {
                        Log.e(TAG, "Failed to upload image to storage: ${it.message}")
                        operationDone = false
                    }
        }
        return operationDone
    }

    fun saveWeightOnBackend(uri: Uri?, weightActivity: WriteWeightActivity, weight: String?): Boolean {
        var operationDone = true
        if (uri == null) {
            coroutineScope.launch {
                val setUserWeightDeferred = WeightApi.retrofitServiceSetWeight.setWeight(auth.currentUser?.uid.toString(), weight!!, null)
                try {
                    setUserWeightDeferred.await()
                    val myIntent = Intent(weightActivity, LoggedActivity::class.java)
                    myIntent.putExtra("frgToLoadFromActivity", "PROGRESS_FRAGMENT")
                    weightActivity.startActivity(myIntent)
                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                    operationDone = false
                }
            }

        } else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(uri).addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded post image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.d(TAG, "Post image Location: $uri")
                    coroutineScope.launch {
                        val setUserWeightDeferred = WeightApi.retrofitServiceSetWeight.setWeight(auth.currentUser?.uid.toString(), weight!!, uri.toString())
                        try {
                            setUserWeightDeferred.await()
                            val myIntent = Intent(weightActivity, LoggedActivity::class.java)
                            myIntent.putExtra("frgToLoadFromActivity", "PROGRESS_FRAGMENT")
                            weightActivity.startActivity(myIntent)
                        } catch (e: Exception) {
                            Log.e(TAG, "$e")
                            operationDone = false
                        }
                    }
                }
            }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                        operationDone = false
                    }
        }
        return operationDone
    }

    fun getFollowingsUser() {
        coroutineScope.launch {
            val getPropertiesDeferred = UserApi.retrofitServiceGetFollowed.getFollowed(auth.currentUser?.uid.toString())
            try {
                val result = getPropertiesDeferred.await()
                Log.e(TAG, "$result")
                followings = result.toMutableList()
            } catch (e: Exception) {
                Log.e(TAG, "error: $e")
            }
        }
    }


}
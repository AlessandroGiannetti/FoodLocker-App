package com.letsbuildthatapp.foodlocker.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentLoginBinding
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng

private const val TAG = "LOGIN FRAGMENT"

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private val RC_SIGN_IN: Int = 1001

    //Facebook Callback manager
    private var callbackManager: CallbackManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        callbackManager = CallbackManager.Factory.create()

        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_login, container, false)

        viewModel =
                ViewModelProvider(this).get(LoginViewModel::class.java)

        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        // specify the fragment view as the lifecycle owner of the binding
        //this is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        initGoogleOptions()

        binding.loginButtonGoogle.setOnClickListener {
            startIntentLoginGoogle()
        }
        binding.facebookButton.setOnClickListener {
            binding.loginButtonFacebook.performClick()
        }
        binding.loginButtonFacebook.setOnClickListener {
            performFacebookLogin()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed $e")
                Toast.makeText(this.context, "Google sign in failed ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        callbackManager!!.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun performFacebookLogin() {
        binding.loginButtonFacebook.setReadPermissions("email")
        binding.loginButtonFacebook.fragment = this

        // Callback registration
        binding.loginButtonFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                firebaseAuthWithFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                Log.d(TAG, "signInWithCredential:success $exception")
            }
        })
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        FirebaseDBMng.auth.signInWithCredential(credential).addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            // Sign in success, update UI with the signed-in user's information
            Log.e(TAG, "signInWithCredential:success" + it.result)
            val currentUser = FirebaseDBMng.auth.currentUser
            FirebaseDBMng.saveUserOnFirebaseDatabase(view, currentUser?.displayName.toString(), currentUser?.photoUrl, true)

        }.addOnFailureListener {
            Log.e(TAG, "Facebook login in Failed: ${it.message}")
            Toast.makeText(this.context, "Facebook login Failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun startIntentLoginGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseDBMng.auth.signInWithCredential(credential).addOnCompleteListener() {
            if (!it.isSuccessful) return@addOnCompleteListener
            Log.e(TAG, "signInWithCredential:success" + it.result)
            val currentUser = FirebaseDBMng.auth.currentUser
            FirebaseDBMng.saveUserOnFirebaseDatabase(view, currentUser?.displayName.toString(), currentUser?.photoUrl, true)

        }.addOnFailureListener {
            Log.e(TAG, "Google log in  Failed: ${it.message}")
            Toast.makeText(this.context, "Google login Failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initGoogleOptions() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
    }

}


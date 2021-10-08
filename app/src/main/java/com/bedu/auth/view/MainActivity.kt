package com.bedu.auth.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bedu.auth.R
import com.bedu.auth.databinding.ActivityOptionsBinding
import com.bedu.auth.utils.Utility
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase

class MainActivity : Activity() {

    private lateinit var binding: ActivityOptionsBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        handleClick()
    }

    private fun handleClick() {

        binding.btnCrash.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }
        binding.btnSetInfo.setOnClickListener {
            Toast.makeText(this, "SetInfo", Toast.LENGTH_SHORT).show()
            // Set a key to a string.
            FirebaseCrashlytics.getInstance().setCustomKey("str_key", "hello")

// Set a key to a boolean.
            FirebaseCrashlytics.getInstance().setCustomKey("bool_key", true)

// Set a key to an int.
            FirebaseCrashlytics.getInstance().setCustomKey("int_key", 1)

// Set a key to an long.
            FirebaseCrashlytics.getInstance().setCustomKey("int_key", 1L)

// Set a key to a float.
            FirebaseCrashlytics.getInstance().setCustomKey("float_key", 1.0f)

// Set a key to a double.
            FirebaseCrashlytics.getInstance().setCustomKey("double_key", 1.0)

            FirebaseCrashlytics.getInstance().setCustomKeys(
                CustomKeysAndValues.Builder()
                .putString("string key", "string value")
                .putString("string key 2", "string  value 2")
                .putBoolean("boolean key", true)
                .putBoolean("boolean key 2", false)
                .putFloat("float key", 1.01f)
                .putFloat("float key 2", 2.02f)
                .build())

            FirebaseCrashlytics.getInstance().log("Higgs-Boson detected! Bailing out")

            FirebaseCrashlytics.getInstance().setUserId("12345")




        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Utility.displaySnackBar(binding.root, "Google sign in failed", this, R.color.red)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user, null)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null, task.exception)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, exception: Exception?) {
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}
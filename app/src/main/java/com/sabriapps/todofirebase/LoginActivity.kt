package com.sabriapps.todofirebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.AnonymousBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1352

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            proceedToMainActivity()
        } else {
            openLoginPage()

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                proceedToMainActivity()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                openLoginPage()
            }

        }
    }


    private fun openLoginPage() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    Arrays.asList(
                        EmailBuilder().build(),
                        AnonymousBuilder().build()
                    )
                )
                .build(),
            RC_SIGN_IN
        )
    }

    private fun proceedToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
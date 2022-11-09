package kesam.learning.projemanag.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import kesam.learning.projemanag.R
import kesam.learning.projemanag.databinding.ActivitySingInBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.User
import kesam.learning.projemanag.viewmodels.FirestoreViewModel

class SignInActivity : BaseActivity() {
    private var binding: ActivitySingInBinding? = null

    var firestoreViewModel : FirestoreViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java);

        // Click event to the Sign-In button
        binding?.btnSignInUser?.setOnClickListener {
            signInRegisteredUserV2()
        }


    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }


        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email: String = binding?.etEmailSignIn?.text.toString().trim{it <= ' '}
        val password: String = binding?.etPasswordSignIn?.text.toString().trim{it <= ' '}

        if (validateForm(email, password)){
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // no needed anymore
                    // hideProgressDialog()
                    if (task.isSuccessful) {

                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FirestoreClass().signInUser(this@SignInActivity)

                        /*
                        Toast.makeText(
                            this@SignInActivity,
                            "You have successfully signed in.",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)
                         */

                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            "Authentication failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }
    }

    private fun signInRegisteredUserV2(){
        val email: String = binding?.etEmailSignIn?.text.toString().trim{it <= ' '}
        val password: String = binding?.etPasswordSignIn?.text.toString().trim{it <= ' '}

        if (validateForm(email, password)){
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            firestoreViewModel?.signInUser(email,password, { user ->
                signInSuccess(user)
            },{
                Toast.makeText(
                    this@SignInActivity,
                    "Authentication failed",
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            })
        }
    }
    /**
     * A function to validate the entries of a user.
     */
    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }

    /**
     * A function to get the user details from the firestore database after authentication.
     */
    fun signInSuccess(user: User) {

        hideProgressDialog()

        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

}
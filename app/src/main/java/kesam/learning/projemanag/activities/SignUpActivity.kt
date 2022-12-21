package kesam.learning.projemanag.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kesam.learning.projemanag.R
import kesam.learning.projemanag.databinding.ActivitySignUpBinding
import kesam.learning.projemanag.firebase.FirestoreClass
import kesam.learning.projemanag.models.User


class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        // Click event to the Sign-Up button and call the registerUser function
        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }

    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarSignUpActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    // Register a new user using firebase and we will see the entry in Firebase console.
    // Before doing this you need to perform some steps in the Firebase Console.
    // 1. Go to your project detail.
    // 2. Click on the "Authentication" tab which is on the left side in the navigation bar under the "Develop" section.
    // 3. In the Authentication Page, you will see the tab named “Sign-in method”. Click on it.
    // 4. In the sign-in providers, enable the “Email/Password”.
    // 5. Finally, Now you will be able to Register a new user using the Firebase.
    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim{it <= ' '}
        val email: String = binding?.etEmailSignUp?.text.toString().trim{it <= ' '}
        val password: String = binding?.etPasswordSignUp?.text.toString().trim{it <= ' '}

        if (validateForm(name, email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // No needed anymore, now is in userRegisteredSuccess()
                        // hideProgressDialog()

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!
                            val user = User(firebaseUser.uid, name, registeredEmail)

                            // No needed anymore
                            /*
                            Toast.makeText(
                                this@SignUpActivity,
                                "$name you have successfully registered with email id $registeredEmail.",
                                Toast.LENGTH_SHORT
                            ).show()



                            /**
                             * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
                             * and send him to Intro Screen for Sign-In
                             */
                            FirebaseAuth.getInstance().signOut()

                            // Finish the Sign-Up Screen
                            finish()
                            */

                            // call the registerUser function of FirestoreClass to make an entry in the database.
                            FirestoreClass().registerUser(this@SignUpActivity, user)

                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        // Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()
    }

}
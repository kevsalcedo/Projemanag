package kesam.learning.projemanag.firebase

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import kesam.learning.projemanag.utils.Constants

typealias KUser = kesam.learning.projemanag.models.User

class FirestoreRepository() {

    // Create a instance of Firebase Firestore
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val authInstance = FirebaseAuth.getInstance()

    fun registerUser(userInfo: User, successCallback: () -> Unit, errorHandler: (e: java.lang.Exception) -> Unit) {
        firestoreInstance.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserId())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                    successCallback()
            }
            .addOnFailureListener { e ->
                     errorHandler(e)
            }
    }

    fun signInUser(successCallback: (KUser) -> Unit, errorHandler: (e: java.lang.Exception) -> Unit) {
        firestoreInstance.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserId())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .get()
            .addOnSuccessListener { document ->
                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(kesam.learning.projemanag.models.User::class.java)

                if(loggedInUser != null)
                    successCallback(loggedInUser)

            }
            .addOnFailureListener { e ->
                errorHandler(e)
            }
    }

    fun signInUsingAuth(email: String, password: String, onCompleteListener: (Task<AuthResult>) -> Unit) {
        // Sign-In using FirebaseAuth
        authInstance.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onCompleteListener)
    }

    private fun getCurrentUserId() : String {
// Now is in the val: currentUser
        //return FirebaseAuth.getInstance().currentUser!!.uid

        // An Instance of currentUser using FirebaseAuth
        val currentUser = authInstance.currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
}
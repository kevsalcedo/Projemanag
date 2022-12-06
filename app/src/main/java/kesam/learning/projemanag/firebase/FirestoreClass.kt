package kesam.learning.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kesam.learning.projemanag.activities.*
import kesam.learning.projemanag.models.Board
import kesam.learning.projemanag.models.User
import kesam.learning.projemanag.utils.Constants

/**
 * A custom class where we will add the operation performed for the firestore database.
 */
class FirestoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
            Log.e(activity.javaClass.simpleName, "Board created successfully.")
            Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
            activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .get()
            .addOnSuccessListener { document ->
                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // Here call a function of base activity for transferring the result to it.
                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

                /*
                if(loggedInUser != null)
                // Here call a function of base activity for transferring the result to it.
                activity.signInSuccess(loggedInUser)
                 */

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    "SignInUser",
                    "Error writing document",
                    e
                )
            }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // Now is in the val: currentUser
        //return FirebaseAuth.getInstance().currentUser!!.uid

        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "Error when updating the profile!", Toast.LENGTH_SHORT).show()
            }
    }
}

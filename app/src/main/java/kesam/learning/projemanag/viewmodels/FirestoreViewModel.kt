package kesam.learning.projemanag.viewmodels

import androidx.lifecycle.ViewModel
import kesam.learning.projemanag.firebase.FirestoreRepository
import kesam.learning.projemanag.firebase.KUser

class FirestoreViewModel : ViewModel() {


    val firestoreRepository = FirestoreRepository()

    fun signInUser(email: String, password: String, successCallback: (KUser) -> Unit,  errorHandler: ()-> Unit){
        firestoreRepository.signInUsingAuth(email,password) { task ->
            if (task.isSuccessful){
                firestoreRepository.signInUser({
                                               successCallback(it)
                },{
                    errorHandler()
                })
            } else {
                errorHandler()
            }
        }
    }

}
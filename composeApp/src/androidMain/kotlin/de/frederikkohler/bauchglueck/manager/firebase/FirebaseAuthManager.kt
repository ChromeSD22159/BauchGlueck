package de.frederikkohler.bauchglueck.manager.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseManager {
    companion object {
        val shared = FirebaseManager()
    }

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()

    val appName = "de.frederikkohler.bauchGlueck"
}




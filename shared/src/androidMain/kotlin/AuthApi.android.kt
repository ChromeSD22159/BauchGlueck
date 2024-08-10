import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import model.UserProfile
import model.countdownTimer.CountdownTimer

actual interface AuthApi {
    actual val auth: FirebaseAuth
    actual suspend fun signUp(email: String,password: String): Result<FirebaseUser?>
    actual suspend fun signIn(email: String,password: String): Result<FirebaseUser>
    actual suspend fun signOut(): Result<Unit>
    actual suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit>
    actual suspend fun fetchUserProfile(uid: String): Result<UserProfile?>
    actual suspend fun uploadAndSaveProfileImage(image: Any): Result<Exception?>
    actual suspend fun downloadProfileImage(imageURL: String): Result<Any?>
    actual suspend fun fetchTimers(): Result<List<CountdownTimer>>
}

/*
if (image is Bitmap) {
        // Your Android-specific image upload logic using the Bitmap
        // ... (code similar to what you provided earlier)

        // Example (assuming you have a function to update the profile image URL)
        val user = auth.currentUser

        if (user == null) {
            onComplete(Result.failure(Exception("User not logged in")))
            return
        }

        val imageName = "${user.uid}_profile_image.jpg"
        val storageRef = storage.reference.child("profile_images/$imageName")

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val imageData = baos.toByteArray()

        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Update user profile (you'll likely need a separate function for this)
                updateUserProfileImageURL(user.uid, uri.toString())
                onComplete(Result.success(Unit))
            }.addOnFailureListener { e ->
                onComplete(Result.failure(e))
            }
        }.addOnFailureListener { e ->
            onComplete(Result.failure(e))
        }

    } else {
        onComplete(Result.failure(Exception("Invalid image type")))
    }
 */

/*
actual override suspend fun downloadProfileImage(imageURL: String, onComplete: (Result<Any?, Error>) -> Void) {
    // Your Android-specific image download logic, returning a Bitmap?
    val storageRef = storage.reference.child(imageURL)

    val oneMegabyte: Long = 1024 * 1024
    storageRef.getBytes(oneMegabyte)
        .addOnSuccessListener { data ->
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            onComplete(Result.success(bitmap)) // Return the Bitmap if successful
        }
        .addOnFailureListener { e ->
            onComplete(Result.failure(e))
        }
}
 */
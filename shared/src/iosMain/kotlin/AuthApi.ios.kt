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
// Inside your iOS implementation of FirebaseAuthRepository
func uploadAndSaveProfileImage(image: Any, completionHandler: @escaping (Result<KotlinUnit, Error>) -> Void) {
    guard let uiImage = image as? UIImage else {
        completionHandler(.failure(NSError(domain: "ImageError", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid image type"])))
        return
    }

    // ... your iOS-specific image upload logic using uiImage ...
}

func downloadProfileImage(imageURL: String, completionHandler: @escaping (Result<Any?, Error>) -> Void) {
    // ... your iOS-specific image download logic, returning a UIImage? ...
}
 */
package ui.screens.authScreens.addRecipe

import android.graphics.Bitmap
import data.remote.StrapiApiClient
import data.remote.model.ApiUploadImageResponse
import data.remote.model.RecipeUpload
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.onError
import util.onSuccess
import java.io.ByteArrayOutputStream

class AddRecipeViewModel : ViewModel() {
    private val remoteClient = StrapiApiClient()
    private var _selectedImage : MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val selectedImage: StateFlow<Bitmap?> = _selectedImage.asStateFlow()

    private var _isUploading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private var _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setSelectedImage(bitmap: Bitmap) {
        _selectedImage.value = bitmap
    }

    fun clearSelectedImage() {
        _selectedImage.value = null
    }

    fun uploadImage(
        mainImage: (ApiUploadImageResponse?) -> Unit
    ) {
        viewModelScope.launch {
            if(
                selectedImage.value != null
            ) {
                _isUploading.value = true
                selectedImage.value?.let { bitmap ->
                    val byteArray = ByteArrayOutputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.toByteArray()
                    }

                    remoteClient.uploadImage("", byteArray)
                        .onError {
                            mainImage(null)
                            _isUploading.value = false

                            setErrorMessage(it.name)
                        }
                        .onSuccess {
                            mainImage(it.first())
                        }
                }
            } else {
                setErrorMessage("Bitte wählen Sie ein Bild aus")
            }
        }
    }

    fun uploadRecipe(recipe: RecipeUpload) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        viewModelScope.launch {

            val res = remoteClient.uploadRecipe(recipe.copy(userId = userId))

            if(
                recipe.name.length > 3 &&
                recipe.description.length > 3 &&
                recipe.preparation.length > 3 &&
                recipe.preparationTimeInMinutes.isNotEmpty() &&
                recipe.mainImage.id != 0
            ) {
                res.onError {
                    _isUploading.value = false

                    setErrorMessage(it.name)
                }.onSuccess {
                    _isUploading.value = false

                    // save to local db
                }
            } else {
                setErrorMessage("Bitte füllen Sie alle Felder aus")
            }
        }
    }

    fun setErrorMessage(msg: String) {
        viewModelScope.launch {
            _error.value = msg
            delay(3000)
            _error.value = null
        }
    }
}
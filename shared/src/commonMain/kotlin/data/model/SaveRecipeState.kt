package data.model

enum class SaveRecipeState {
    NotStarted,
    UploadingImage,
    UploadingRecipe,
    AiCorrection,
    Failed,
    Done
}
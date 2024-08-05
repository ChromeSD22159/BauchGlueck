package work

import RecipeRepositoryImpl
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import database.AppDatabase
import network.ApiServiceImpl

class SyncDataWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = RecipeRepositoryImpl(
            recipeCategoryDao = AppDatabase.getDatabase(applicationContext).recipeCategoryDao(),
            measurementUnitDao = AppDatabase.getDatabase(applicationContext).measurementUnitDao(),
            ingredientDao = AppDatabase.getDatabase(applicationContext).ingredientDao(),
            recipeDao = AppDatabase.getDatabase(applicationContext).recipeDao(),
            apiService = ApiServiceImpl()
        )

        return try {
            repository.syncData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
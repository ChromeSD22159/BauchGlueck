import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import database.AppDatabase
import network.ApiServiceImpl
import java.util.concurrent.TimeUnit

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

fun scheduleRecipeSync(context: Context) {
    val syncRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(1, TimeUnit.HOURS)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "RecipeSyncWork",
        ExistingPeriodicWorkPolicy.KEEP,
        syncRequest
    )
}
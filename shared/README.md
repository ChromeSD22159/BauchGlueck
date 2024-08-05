# Recipe App

Die Recipe App ist eine plattformübergreifende Anwendung, die Rezepte verwaltet und sowohl auf Android als auch iOS synchronisiert. Sie nutzt Ktor für die API-Kommunikation und Room für die lokale Datenhaltung auf Android. Die Synchronisation wird durch WorkManager auf Android und Hintergrundaufgaben auf iOS durchgeführt.

## Übersicht

1. [Synchronisation](#1-synchronisation)
    - [Beim Öffnen der App](#11-synchronisation-beim-öffnen-der-app)
    - [Beim Schließen der App](#12-synchronisation-beim-schließen-der-app)
2. [Datenmodell mit Room](#2-datenmodell-mit-room)
3. [Synchronisationslogik mit WorkManager](#3-synchronisationslogik-mit-workmanager)
4. [Shared-Komponenten](#4-shared-komponenten)
    - [ApiService](#shared-apiservice)
    - [ApiServiceImpl](#shared-apiserviceimpl)
    - [RecipeRepository](#shared-reciperepository)
5. [Android-Spezifische Komponenten](#android-spezifische-komponenten)

## 1. Synchronisation

### 1.1 Synchronisation beim Öffnen der App

- **Android**: Die Synchronisation wird in der `onCreate` Methode der `MainActivity` durchgeführt.
  ```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      // Starte die Synchronisation
      CoroutineScope(Dispatchers.IO).launch {
          syncData()
      }
  }
### 1.2 Synchronisation beim Schließen der App

- **Android**: Die Synchronisation wird in der `onDestroy` Methode der `MainActivity` durchgeführt. Diese Methode wird aufgerufen, wenn die Aktivität zerstört wird, z. B. wenn die App geschlossen wird oder in den Hintergrund geht. Hier wird die Methode `syncData()` aufgerufen, um die Daten mit dem Server zu synchronisieren.
```kotlin
fun onDestroy() {
  super.onDestroy()
  CoroutineScope(Dispatchers.IO).launch {
      syncData()
  }
}
```

## 2. Datenmodell mit Room

Room ist eine persistente Speicherlösung für Android, die eine SQLite-Datenbank abstrahiert und den Zugriff auf lokale Daten vereinfacht. Es bietet eine strukturierte Möglichkeit, Daten zu speichern und zu verwalten. Hier wird beschrieben, wie das Datenmodell für die Rezeptverwaltung aufgebaut ist und wie Room verwendet wird.

### Entitie`s

- Die `Recipe`-Entity stellt das Rezeptdatenmodell dar, das in der SQLite-Datenbank gespeichert wird. Sie enthält alle relevanten Informationen eines Rezepts.
- Die `RecipeCategory`-Entity repräsentiert die Kategorie eines Rezepts. Diese Information wird lokal gespeichert und ermöglicht das Kategorisieren von Rezepten.
- Die `MeasurementUnit`-Entity speichert die Maßeinheit für Zutaten.
- Die `IngredientForm`-Entity speichert die Form, in der eine Zutat vorliegt (z. B. frisch, getrocknet).
- Die `Ingredient`-Entity beschreibt die Zutaten eines Rezepts, einschließlich der Menge, des Namens und der Maßeinheit.

###  Dao`s
- Der `RecipeDao` stellt die Datenzugriffsoperationen für die Recipe-Entity bereit. Er ermöglicht das Abrufen, Einfügen, Aktualisieren und Löschen von Rezepten.
- Der `RecipeCategoryDao` stellt die Datenzugriffsoperationen für die RecipeCategory-Entity bereit. Er ermöglicht das Abrufen, Einfügen, Aktualisieren und Löschen von Rezeptkategorien.
- Der `MeasurementUnitDao` stellt die Datenzugriffsoperationen für die MeasurementUnit-Entity bereit. Er ermöglicht das Abrufen, Einfügen, Aktualisieren und Löschen von Maßeinheiten.
- Der `IngredientFormDao` stellt die Datenzugriffsoperationen für die IngredientForm-Entity bereit. Er ermöglicht das Abrufen, Einfügen, Aktualisieren und Löschen von Zutatenformen.

### AppDatabase 
AppDatabase ist die zentrale Klasse für den Zugriff auf die Room-Datenbank. Sie definiert die Datenbankversion und enthält abstrakte Methoden zum Abrufen der DAOs.

```kotlin
@Database(
    entities = [Recipe::class, RecipeCategory::class, MeasurementUnit::class, IngredientForm::class, Ingredient::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeCategoryDao(): RecipeCategoryDao
    abstract fun measurementUnitDao(): MeasurementUnitDao
    abstract fun ingredientFormDao(): IngredientFormDao
}
```

## 3. Synchronisationslogik mit WorkManager

WorkManager ist eine Bibliothek von Android Jetpack, die die Planung von zuverlässigen und garantierten asynchronen Aufgaben im Hintergrund ermöglicht. Es ist besonders nützlich für Aufgaben, die auch dann ausgeführt werden sollen, wenn die App geschlossen oder das Gerät neu gestartet wurde. Hier wird beschrieben, wie die Synchronisationslogik mithilfe von WorkManager implementiert wird.

### Implementierung der Synchronisationslogik

#### SyncDataWorker

`SyncDataWorker` ist eine Implementierung von `Worker`, die verwendet wird, um Hintergrundaufgaben wie die Synchronisation von Daten mit einem Server durchzuführen. Es wird verwendet, um regelmäßige Synchronisationen oder einmalige Aufgaben zu planen.

```kotlin
class SyncDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository: RecipeRepository = RecipeRepositoryImpl(
        ApiServiceImpl(),
        AppDatabase.getInstance(context).recipeDao(),
        AppDatabase.getInstance(context).recipeCategoryDao(),
        AppDatabase.getInstance(context).measurementUnitDao()
    )

    override suspend fun doWork(): Result {
        return try {
            repository.syncData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

#### scheduleRecipeSync
Die Methode `scheduleRecipeSync` plant eine wiederkehrende Synchronisationsaufgabe mithilfe von WorkManager. Die Aufgabe wird hier so konfiguriert, dass sie einmal pro Stunde ausgeführt wird.
```kotlin
fun scheduleRecipeSync(context: Context) {
   val syncRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(1, TimeUnit.HOURS)
      .build()
   WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      "RecipeSyncWork",
      ExistingPeriodicWorkPolicy.KEEP,
      syncRequest
   )
}
```

## 4. Integration der API-Services und das Zusammenspiel der Komponenten

Dieser Abschnitt beschreibt, wie die verschiedenen Komponenten der App zusammenarbeiten, um Daten von der API abzurufen und lokal zu speichern. Es wird erklärt, wie die API-Services implementiert sind und wie sie mit Room und WorkManager interagieren.

### 4.1 Shared: ApiService
`ApiService` ist ein Interface, das die API-Endpunkte definiert. Es beschreibt, wie die App mit dem Backend-Server kommuniziert, z.B. durch HTTP-Requests.

```kotlin
interface ApiService {
    @GET("recipes")
    suspend fun getRecipes(): List<Recipe>

    @POST("recipe")
    suspend fun addRecipe(@Body recipe: Recipe): Recipe

    @PUT("recipe/{id}")
    suspend fun updateRecipe(@Path("id") id: Int, @Body recipe: Recipe): Recipe

    @DELETE("recipe/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int)
}
```

### 4.2 Shared: ApiServiceImpl
`ApiServiceImpl` ist eine Implementierung des ApiService-Interfaces. Hier wird die tatsächliche Logik für die Kommunikation mit dem Backend implementiert, oft unter Verwendung von Retrofit oder einer ähnlichen HTTP-Bibliothek.

### 4.3 Shared: RecipeRepository
`RecipeRepository` ist ein Interface, das die Methoden zur Verwaltung von Rezeptdaten definiert. Es agiert als Abstraktionsschicht zwischen der Datenquelle (API oder lokale Datenbank) und der Anwendungslogik.

### 4.4 androidMain: RecipeRepositoryImpl
`RecipeRepositoryImpl` ist die konkrete Implementierung des RecipeRepository-Interfaces. Es verwendet ApiService und AppDatabase für die Datenverwaltung und Implementierung der Logik zum Abrufen, Hinzufügen, Aktualisieren und Löschen von Rezepten.

### 4.5 androidMain: AppDatabase
`AppDatabase` ist die Room-Datenbankklasse, die die lokalen Datenbanktabellen definiert und DAOs bereitstellt. Es ist die zentrale Klasse für den Zugriff auf die lokale Datenbank.

### 4.6 androidMain: SyncDataWorker
`SyncDataWorker` ist eine Implementierung von Worker, die verwendet wird, um Hintergrundaufgaben wie die Synchronisation von Daten mit einem Server durchzuführen. Es wird verwendet, um regelmäßige Synchronisationen oder einmalige Aufgaben zu planen.
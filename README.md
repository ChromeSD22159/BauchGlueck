# BauchGlück

**Nützliche Tools zur unterstützt von Menschen, nach ihrer Magenbypass-Operation.**

BauchGlück unterstützt Menschen nach einer Magenbypass-Operation. Die App bietet Countdown-Timer zur Erinnerung an Ess- und Trinkzeiten, dokumentiert die Wasseraufnahme und den Gewichtsverlust, verwaltet Medikamente und Erinnerungen ähnlich einer To-Do-App und ermöglicht die Mahlzeitenplanung. Eine Community-Funktion erlaubt den Austausch von Rezepten.

## App Design
<p>
  <img src="./images/screen_home.png" width="200">
  <img src="./images/screen_home1.png" width="200">
  <img src="./images/screen_mealplan.png" width="200">
    <img src="./images/screen_recipe.png" width="200">
  <img src="./images/screen_recipe_categories.png" width="200">
  <img src="./images/screen_shoppinglist.png" width="200">
</p>

## Features
- [ ] Verwaltung und Erstellung von CountdownTimer mit Notification
- [ ] Eingabe und Anzeige des Gewichtsverlaufs
- [ ] Eingabe und Anzeige von Wasseraufnahme
- [ ] Verwaltung und Erstellung von CountdownTimer mit Notification
- [ ] Verwaltung und Erstellung von Rezepten
- [ ] Verwaltung und Erstellung von Medication mit Einnameverlauf
- [ ] Verwaltung und Planen von Ernährungsplan
- [ ] Erstellung von Einkaufslisten
- [ ] Einstellungen & Rechtliches

## Technischer Aufbau

#### Projektaufbau
Die Projektstruktur basiert auf dem MVVM (Model-View-ViewModel) Muster auf der Client-Seite und einem Repository-Ansatz im Backend, das in Kotlin geschrieben ist. Das Backend bedient eine API und nutzt verschiedene Services für die Datenverarbeitung und Speicherung.

- app/ enthält die Android-App (MVVM).
- server/ enthält den Server-Code, geschrieben in Kotlin, der für die API und Datenverarbeitung verantwortlich ist.


#### Datenspeicherung
Die Datenspeicherung erfolgt über eine Kombination aus:

- Room Database für die lokale Speicherung in der App.
- Firebase für Authentifizierung und Cloud-Speicherung.
- Strapi Backend speichert Daten in einer relationalen Datenbank.

#### API Calls
Es werden APIs für Rezeptmanagement und Benutzerdaten verwendet. Die wichtigsten API-Endpunkte im Backend sind:

- /api/water-intake/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}
- /api/recipes/getUpdatedRecipesEntries?timeStamp={timestamp}
- /api/weight/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}
- /api/medication/getUpdatedMedicationEntries?timeStamp={timestamp}&userId={userID}
- /api/timer/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}
- /api/mealPlan/getUpdatedMealPlanDayEntries?timeStamp={timestamp}&userId={userID}
- /api/recipes/generateRecipe?category={RecipeKind}
- /api/meal/getUpdatedMealEntries?timeStamp={timestamp}
- /api/water-intake/updateRemoteData
- /api/weight/updateRemoteData
- /api/medication/syncDeviceMedicationData
- /api/timer/updateRemoteData
- /api/send-schedule-notification
- /api/upload/
- /api/appStatistics

#### Dependencies
Es ist eine gute Praxis, die verwendeten Dependencies aufzulisten. Das gibt den Nutzern deines Projekts eine klare Vorstellung davon, was im Projekt verwendet wird.
- Room 2.3.0 – Für lokale Datenbankverwaltung.
- Ktor Client – Für Netzwerkoperationen.
- Coil - Für Ayncrone Image loading und Cashing.
- Koin - für Dependency Injection
- Firebase – Für Authentifizierung und Cloud-Speicherung.


## Backend (Strapi)
The backend for this project, built with Strapi, is available in the following repository:

Repository: https://github.com/ChromeSD22159/BauchGueckStrapiBackend
Please refer to the repository's README for instructions on how to set up and run the Strapi backend server.

Important: Make sure the `API_HOST` in your local.properties configuration in your Kotlin code matches the actual URL of your running Strapi instance.

## Configuration

To ensure the application runs correctly, you need to configure both the `API_KEY` and `API_HOST` in your `local.properties` file.

### Steps for Configuration:

1. **Obtain the API Key:**
  - Log in to your Strapi dashboard.
  - Navigate to the API Token settings and copy the required API Key.

2. **Set the API Key in `local.properties`:**
  - Open the `local.properties` file in your project directory.
  - Add or update the following entry:

    ```properties
    API_KEY=<YOUR_API_KEY>
    ```

  - Replace `<YOUR_API_KEY>` with the API Key you obtained from Strapi.

3. **Configure the API Host:**
  - In the same `local.properties` file, add or update the `API_HOST` entry to match the URL of your running Strapi instance:

    ```properties
    API_HOST=<YOUR_API_HOST>
    ```

  - Replace `<YOUR_API_HOST>` with the actual URL of your Strapi instance (e.g., `http://localhost:1337`).

4. **Save and Restart:**
  - Save the `local.properties` file.
  - Restart the application to apply the changes.

These configurations ensure that the application can correctly interact with your Strapi API.
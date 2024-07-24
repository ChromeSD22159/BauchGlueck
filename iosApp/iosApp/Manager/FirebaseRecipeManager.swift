//
//  FirebaseRecipeManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import FirebaseFirestore
import FirebaseFirestoreSwift
import SwiftUI

class FirebaseRecipeManager: ObservableObject {
    
    static var shared = FirebaseRecipeManager()
    
    private let db = Firestore.firestore()
    private let recipesCollection = "recipes"

    @Published var recipes: [Recipe] = []
    
    init() {
        loadRecipes()
    }
    
    // UserDefaults für lokale Speicherung
    private let userDefaults = UserDefaults.standard
    private let localRecipesKey = "localRecipes"

    // MARK: - Firebase-offline Interaktionen
    func addRecipe(recipe: Recipe) {
        guard let userID = FirebaseAuthManager.shared.user?.uid else { return }
        
        var newRecipe = recipe
        newRecipe.id = UUID().uuidString
        newRecipe.user_id = userID
        db.disableNetwork()
        
        do {
            try db.collection(recipesCollection).document(newRecipe.id).setData(from: newRecipe)
            recipes.append(newRecipe)
            
            print("Rezept gespeichert: \(recipes)")
            
            print("RezeptListe: \(recipes.count)")
            
            objectWillChange.send()
        } catch {
            print("Fehler beim Hinzufügen des Rezepts: \(error)")
        }
    }

    func updateRecipe(recipe: Recipe) {
        db.disableNetwork()
        
        do {
            try db.collection(recipesCollection).document(recipe.id).setData(from: recipe)
            if let index = self.recipes.firstIndex(where: { $0.id == recipe.id }) {
                self.recipes[index] = recipe
                objectWillChange.send()
            }
        } catch {
            print("Fehler beim Aktualisieren des Rezepts: \(error)")
        }
    }
    
    func deleteRecipe(recipe: Recipe) {
        db.disableNetwork()
        db.collection(recipesCollection).document(recipe.id).delete() { error in
            if let error = error {
                print("Fehler beim Löschen des Rezepts: \(error)")
            } else {
                self.recipes.removeAll(where: { $0.id == recipe.id })
                self.objectWillChange.send()
            }
        }
    }

    func loadRecipes() {
        db.collection(recipesCollection).addSnapshotListener { querySnapshot, error in
            if let error = error {
                print("Fehler beim Abrufen von Rezepten: \(error.localizedDescription)")
                // Fehlerbehandlung (z. B. Alert anzeigen)
                return
            }

            guard let documents = querySnapshot?.documents else { return }

            self.recipes = documents.compactMap { document -> Recipe? in
                let data = document.data()
                
                // Ingredients decoding
                var ingredients: [Ingredients] = []
                if let ingredientsData = data["ingredients"] as? [[String: Any]] {
                    for ingredientDict in ingredientsData {
                        if
                            let val = ingredientDict["val"] as? String,
                            let name = ingredientDict["name"] as? String,
                            let unitRawValue = ingredientDict["unit"] as? String,
                            let unit = MeasurementUnit(rawValue: unitRawValue)
                        {
                            let formRawValue = ingredientDict["form"] as? String
                            let form = formRawValue != nil ? IngredientForm(rawValue: formRawValue!) : nil
                            
                            let ingredient = Ingredients(val: val, name: name, form: form, unit: unit)
                            ingredients.append(ingredient)
                        } else {
                            // Handle missing or invalid ingredient data (e.g., log an error)
                            print("Ungültige oder fehlende Zutatendaten: \(ingredientDict)")
                        }
                    }
                } else {
                    // Handle the case where "ingredients" is not an array or is missing
                    print("Keine Zutaten gefunden oder ungültiges Format")
                }
                
                
                let id = data["id"] as? String ?? ""
                let userId = data["user_id"] as? String ?? ""
                let title = data["title"] as? String ?? "Unbekanntes Rezept" // Standardwert
                let recipeCategoryRaw = data["recipeCategory"] as? String ?? ""
                let recipeCategory = RecipeCategories(rawValue: recipeCategoryRaw) ?? .none
                let portionSize = data["portion_size"] as? String ?? "" // Leerer String, falls ungültig
                let preparationTime = data["preparation_time"] as? String ?? "" // Leerer String, falls ungültig
                let cookingTime = data["cooking_time"] as? String ?? "" // Leerer String, falls ungültig
                let preparation = data["preparation"] as? String ?? "" // Leerer String, falls ungültig
                let rating = data["ratings"] as? Int ?? 0
                let notes = data["notes"] as? String ?? "" // Leerer String, falls ungültig
                let image = data["image"] as? String ?? "" // Leerer String, falls ungültig
                let isPrivate = data["is_private"] as? Bool ?? false // Standardmäßig "false"
                let lastUpdated = data["lastUpdated"] as? Date ?? Date()
                
                /*
                 // Check if all required fields are present and valid
                 guard
                     
                 else {
                     print("Unvollständige oder ungültige Rezeptdaten: \(data)")
                     return nil // Ignoriere ungültige Rezepte
                 }
                 */

                
                    
                var recipe = Recipe(
                    id: id,
                    user_id: userId,
                    title: title,
                    recipeCategory: recipeCategory,
                    portion_size: portionSize,
                    preparation_time: preparationTime,
                    cooking_time: cookingTime,
                    ingredients: ingredients,
                    preparation: preparation,
                    rating: rating,
                    notes: notes,
                    image: image,
                    is_private: isPrivate,
                    last_updated: lastUpdated
                )
                return recipe
            }
        }
    }
    
    // MARK: - Firebase-online Interaktionen
    func synchronizeWithFirebase() {
        db.collection(recipesCollection).getDocuments { (querySnapshot, error) in
            if let error = error {
                print("Fehler beim Abrufen von Rezepten aus Firebase: \(error)")
                return
            }

            guard let documents = querySnapshot?.documents else { return }

            // Firebase-Rezepte abrufen und in ein Dictionary umwandeln
            let firebaseRecipes = documents.compactMap { document -> Recipe? in
                try? document.data(as: Recipe.self)
            }
            var firebaseRecipesDict = [String: Recipe]()
            for recipe in firebaseRecipes {
                firebaseRecipesDict[recipe.id] = recipe
            }

            // Lokale Rezepte in ein Dictionary umwandeln
            var localRecipesDict = [String: Recipe]()
            for recipe in self.recipes {
                localRecipesDict[recipe.id] = recipe
            }

            // Änderungen synchronisieren
            for (id, firebaseRecipe) in firebaseRecipesDict {
                if let localRecipe = localRecipesDict[id] {
                    // Rezept existiert sowohl lokal als auch in Firebase
                    if firebaseRecipe.last_updated > localRecipe.last_updated {
                        // Firebase-Version ist neuer, aktualisiere das lokale Rezept
                        self.recipes[self.recipes.firstIndex(where: { $0.id == id })!] = firebaseRecipe
                    } else if firebaseRecipe.last_updated < localRecipe.last_updated {
                        // Lokale Version ist neuer, aktualisiere das Firebase-Rezept
                        try? self.db.collection(self.recipesCollection).document(id).setData(from: localRecipe)
                    }
                    // Ansonsten sind die Rezepte identisch, keine Aktion erforderlich
                } else {
                    // Rezept existiert nur in Firebase, füge es lokal hinzu
                    self.recipes.append(firebaseRecipe)
                }
            }

            // Überprüfen, ob lokale Rezepte fehlen in Firebase
            for (id, localRecipe) in localRecipesDict {
                if firebaseRecipesDict[id] == nil {
                    // Rezept existiert nur lokal, füge es in Firebase hinzu
                    try? self.db.collection(self.recipesCollection).document(id).setData(from: localRecipe)
                }
            }
        }
    }
}

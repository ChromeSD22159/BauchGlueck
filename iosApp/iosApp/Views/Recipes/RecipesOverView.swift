//
//  RecipesOverView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct RecipesOverView: View {
    @EnvironmentObject var theme: Theme
    @StateObject var recipeManager = FirebaseRecipeManager.shared
    @EnvironmentObject var arvm: AddRecipeViewModel
    @Environment(\.scenePhase) var scenePhase
    
    var allRecipes: [Recipe] {
        var combinedRecipes = DummyRecipes().recipes + recipeManager.recipes

        // Remove duplicates (optional, depending on your data and requirements)
        combinedRecipes = combinedRecipes.reduce(into: [Recipe]()) { result, recipe in
            if !result.contains(where: { $0.id == recipe.id }) {
                result.append(recipe)
            }
        }

        return combinedRecipes
    }
    
    var body: some View {
        ZStack {
            theme.color(.background).ignoresSafeArea()
            
            theme.backgroundImageWithOutImage(
                background: .backgroundVariant,
                backgroundOpacity: 1
            )
      
            ScrollView{
                
                Text("Recipes: \(allRecipes.count)")
                    .font(.seat(size: .largeTitle))
                    .foregroundStyle(.white)
                
                VStack(alignment: .leading, spacing: 16) {
                    ForEach(allRecipes) { recipe in
                        RecipeCard(recipe: recipe)
                    }
                }
                .padding(.horizontal, 16)
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    HStack {
                        RoundedHeaderButton(icon: "arrow.triangle.2.circlepath") {
                            //FirestoreTimerManager.shared.synronize()
                        }
                        
                        ContextMenu()
                    }
                }
            }
        }
    }
    
    @ViewBuilder func RoundedHeaderButton(icon: String, action: @escaping () -> Void) -> some View {
        Button(action: { action() }) {
            ZStack {
                theme.gradient(array: [theme.color(.primary), theme.color(.primaryVariant)])
                .frame(width: 33, height: 33)
                .clipShape(Circle())
                
                Image(systemName: icon)
                    .font(Font.custom("SF Pro", size: 16))
                    .foregroundColor(.white)
                    .padding(8)
                
            }
        }
    }
    
    @ViewBuilder func ContextMenu() -> some View {
        Menu(content: {
            Button {
                arvm.isAddRecipeSheet.toggle()
            } label: {
                Label("Add Recipe", systemImage: "fork.knife.circle.fill")
            }
        }, label: {
            ZStack {
                theme.gradient(array: [theme.color(.primary), theme.color(.primaryVariant)])
                    .frame(width: 33, height: 33)
                    .clipShape(Circle())
                
                Image(systemName: "plus")
                    .font(Font.custom("SF Pro", size: 16))
                    .foregroundColor(.white)
                    .padding(8)
            }
        })
    }
}


struct RecipeCard: View {
    let recipe: Recipe
    @State var sheet = false
    var body: some View {
        VStack(alignment: .leading) {
            
            AsyncImage(url: URL(string: recipe.image)) { phase in
                if let image = phase.image {
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(maxWidth: .infinity, maxHeight: 150)
                        .clipped()
                } else if phase.error != nil {
                    Color.red // Replace with your error placeholder view
                } else {
                    ProgressView()
                }
            }

            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(recipe.title)
                        .font(.headline)

                    HStack {
                        Text("\(recipe.preparation_time) \(recipe.cooking_time)")
                            .font(.caption)
                        Text(recipe.portion_size)
                            .font(.caption)
                        Text("\(recipe.rating)/5")
                            .font(.caption)
                    }
                }
                .padding(.horizontal)

                Spacer()

                // Action buttons (replace with your actual icons)
                HStack {
                    Button(action: { /* Handle heart action */ }) {
                        Image(systemName: "heart")
                    }
                    Button(action: { /* Handle share action */ }) {
                        Image(systemName: "square.and.arrow.up")
                    }
                    Button(action: { /* Handle cart action */ }) {
                        Image(systemName: "cart")
                    }
                }
                .padding(.trailing)
            }
            .padding(.bottom)
        }
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(15)
        .onTapGesture {
            sheet.toggle()
        }
        .sheet(isPresented: $sheet, onDismiss: {}, content: {
            RecipeCardDetailView(recipe: recipe)
                .presentationDragIndicator(.visible)
        })
    }
}

struct RecipeCardDetailView: View {
    let recipe: Recipe
    @EnvironmentObject var theme: Theme // Assuming you have a Theme class for colors, etc.

    var body: some View {
        ZStack {
            theme.color(.background).ignoresSafeArea()
               
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                
                    ZStack {
                        AsyncImage(url: URL(string: recipe.image)) { phase in
                            if let image = phase.image {
                                image
                                    .resizable()
                                    .scaledToFill()
                            } else if phase.error != nil {
                                Color.gray // Placeholder bei Fehler
                            } else {
                                ProgressView() // Ladeanzeige
                            }
                        }
                        .frame(height: 300) // Bildhöhe anpassen
                        .clipped()
                        
                        VStack {
                            HStack{
                                
                                Spacer()
                                
                                Image(systemName: "flame")
                                    .font(.title)
                                
                                Image(systemName: "bookmark")
                                    .font(.title)
                                
                                Image(systemName: "square.and.arrow.up")
                                    .font(.title)
                            }
                            .padding()
                            
                            Spacer()
                        }
                    }

                    // Rezeptinformationen
                    VStack(alignment: .leading, spacing: 25) {
                        HStack(spacing: 4) {
                            Text(recipe.title)
                                .font(.title)
                                .padding(.top, 8)
                            
                            Spacer()
                            
                            Text(recipe.recipeCategory.rawValue)
                                .font(.caption2)
                                .padding(.vertical, 2)
                                .padding(.horizontal, 6)
                                .background(
                                    RoundedRectangle(cornerRadius: 10)
                                        .fill(theme.color(.secondary))
                                )
                        }
                        
                        //icons
                        HStack {
                            VStack(alignment: .center, spacing: 5) {
                                Image(systemName: "flame")
                                    .font(.title)
                                
                                Text(recipe.preparation_time)
                                    .font(.caption)
                            }
                            
                            Spacer()
                            
                            VStack(alignment: .center, spacing: 5) {
                                Image(systemName: "timer")
                                    .font(.title)
                                
                                Text(recipe.cooking_time)
                                    .font(.caption)
                            }
                            
                            Spacer()
                            
                            VStack(alignment: .center, spacing: 5) {
                                Image(systemName: "person.2")
                                    .font(.title)
                                
                                Text(recipe.portion_size)
                                    .font(.caption)
                            }
                           
                        }
                        .foregroundColor(theme.color(.secondary))
                           
                        VStack(alignment: .leading, spacing: 10){
                            Text("Rreparation")
                                .font(.title3)
                            
                            Text(recipe.preparation)
                                .font(.body)
                        }
                        
                        //Ingredients
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Ingredients")
                                .font(.title3)
                            
                            VStack(alignment: .leading, spacing: 5) {
                                ForEach(recipe.ingredients, id: \.name) { ingredients in
                                    HStack(spacing: 20) {
                                        Text(ingredients.name)
                                        Spacer()
                                        Text("\(ingredients.val) \(ingredients.unit)")
                                    }
                                }
                            }
                        }
                        
                        //Notes
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Notes")
                                .font(.title3)
                            
                            Text(recipe.notes)
                                .font(.body)
                        }
                        
                        // Rates
                        HStack {
                            HStack {
                                ForEach(1...recipe.rating, id: \.self) {_ in
                                    Image(systemName: "star.fill")
                                        .font(.caption)
                                        .foregroundColor(theme.color(.secondary))
                                }
                            }
     
                            Spacer()
                            
                            Text(recipe.recipeCategory.rawValue)
                                .font(.caption)
                                .foregroundColor(theme.color(.secondary))
                        }
                    }
                    .padding()
                }
            }
        }
    }
}


#Preview {
    RecipesOverView()
        .environmentObject(Theme())
        .environmentObject(FirebaseAuthManager())
}

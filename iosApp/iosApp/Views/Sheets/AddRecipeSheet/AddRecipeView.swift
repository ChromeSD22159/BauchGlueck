//
//  AddRecipeView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct AddRecipeView: View {
    @EnvironmentObject var theme: Theme
    @ObservedObject var arvm: AddRecipeViewModel
    
    var body: some View {
        SheetView(sheetName: "Add your recipe", closeBinding: $arvm.isAddRecipeSheet) {
            VStack(spacing: 16) {
                Picker("", selection: $arvm.selectedSegmentedOption) {
                    ForEach(AddRecipeSegmentedOtions.allCases, id: \.self) { option in
                        Text(option.rawValue).tag(option)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
                
                ScrollView(.vertical, showsIndicators: false) {
                    VStack(spacing: 16) {
                        if arvm.selectedSegmentedOption == .infomation {
                            VStack(spacing: 16) {
                                Inputs(
                                    title: "Title",
                                    placeholder: "Title",
                                    text: $arvm.recipe.title,
                                    height: 20
                                )
                                
                                Inputs(
                                    title: "Portion Size",
                                    placeholder: "Portion Size",
                                    text: $arvm.recipe.portion_size,
                                    height: 20
                                )
                                
                                Inputs(
                                    title: "Preparation Time",
                                    placeholder: "0",
                                    text: $arvm.recipe.preparation_time,
                                    height: 20
                                )
                                
                                Inputs(
                                    title: "Cooking Time",
                                    placeholder: "0",
                                    text: $arvm.recipe.cooking_time,
                                    height: 20
                                )
                                
                                HStack(spacing: 5) {
                                    Text("Kategorie")
                                        .font(.seat(size: .footnote))
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                    
                                    Picker("Kategorie", selection: $arvm.recipe.recipeCategory) {
                                        ForEach(RecipeCategories.allCases, id: \.self) { option in
                                            Text(option.rawValue).tag(option)
                                        }
                                    }.pickerStyle(MenuPickerStyle())
                                    .tint(theme.color(.textRegular))
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                                       .fill(Color(uiColor: .secondarySystemBackground))
                                )
                                
                                HStack(spacing: 5) {
                                    Toggle("IsPrivate", isOn: $arvm.recipe.is_private)
                                        .font(.seat(size: .footnote))
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                                       .fill(Color(uiColor: .secondarySystemBackground))
                                )
                            }
                        }
                        
                        if arvm.selectedSegmentedOption == .preparation {
                            VStack(spacing: 16) {
                                Inputs(
                                    title: "Preparation",
                                    placeholder: "Preparation",
                                    text: $arvm.recipe.preparation,
                                    height: 100,
                                    orientation: .vertical
                                )
                            }
                        }
                        
                        if arvm.selectedSegmentedOption == .notes {
                            VStack(spacing: 16) {
                                Inputs(
                                    title: "Notes",
                                    placeholder: "Notes",
                                    text: $arvm.recipe.notes,
                                    height: 100,
                                    orientation: .vertical
                                )
                            }
                        }
                        
                        if arvm.selectedSegmentedOption == .image {
                            HStack(spacing: 16) {

                                if arvm.recipeImage.size != .zero {
                                    ZStack{
                                        Image(uiImage: arvm.recipeImage)
                                                .resizable()
                                                .cornerRadius(50)
                                                .padding(.all, 4)
                                                .frame(width: 100, height: 100)
                                                .background(Theme().gradient(.primary))
                                                .aspectRatio(contentMode: .fill)
                                                .padding(8)
                                    }
                                    .cornerRadius(50)
                                    .padding(.all, 4)
                                    .frame(width: 100, height: 100)
                                    .background(Theme().gradient(.primary))
                                    .aspectRatio(contentMode: .fill)
                                    .clipShape(Circle())
                                    .padding(8)
                                } else {
                                    ZStack{
                                        Image(.placeholderrecipe)
                                            .resizable()
                                            .cornerRadius(50)
                                            .padding(.all, 4)
                                            .frame(width: 100, height: 100)
                                            .background(Theme().gradient(.primary))
                                            .aspectRatio(contentMode: .fill)
                                            .padding(8)
                                    }
                                    .cornerRadius(50)
                                    .padding(.all, 4)
                                    .frame(width: 100, height: 100)
                                    .background(Theme().gradient(.primary))
                                    .aspectRatio(contentMode: .fill)
                                    .clipShape(Circle())
                                    .padding(8)
                                }
                                
                                
                                Button(action: {
                                    arvm.showImageSheet.toggle()
                                }, label: {
                                    Text("Change photo")
                                            .font(.headline)
                                            .frame(maxWidth: .infinity)
                                            .frame(height: 50)
                                            .background(Theme().gradient(.primary))
                                            .cornerRadius(16)
                                            .foregroundColor(.white)
                                })
                            }
                            .sheet(isPresented: $arvm.showImageSheet, onDismiss: {}) {
                                ImagePicker(sourceType: .photoLibrary, selectedImage: $arvm.recipeImage)
                            }
                        }
                        
                        if arvm.selectedSegmentedOption == .ingredients {
                            ForEach($arvm.recipe.ingredients.indices, id: \.self) { index in
                                
                                let ingredientBinding = Binding<Ingredients>(
                                    get: { arvm.recipe.ingredients[index] },
                                    set: { arvm.recipe.ingredients[index] = $0 }
                                )
                                
                                IngredientInputRow(ingredient: ingredientBinding)
                                    .padding()
                                    .background(
                                        RoundedRectangle(cornerRadius: theme.cornerRadius)
                                            .fill(Color(uiColor: .secondarySystemBackground))
                                    )
                            }
                            
                            Button {
                                let inngredient = Ingredients(val: "", name: "", unit: .gram)
                                
                                arvm.recipe.ingredients.append(inngredient)
                            } label: {
                                HStack {
                                    Text("New ingredient")
                                        .foregroundStyle(theme.color(.textRegular))
                                        .font(.seat(size: .footnote))
                                        .frame(maxWidth: .infinity, alignment: .center)
                                        .padding()
                                }
                                .background(
                                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                                        .fill(theme.gradient(.primary))
                                )
                            }
                            
                            
                        }
                    }
                }
            }
        } closeFunc: {
            // closeClock
            arvm.resetRecipeUIImage()
        } addFunc: {
            // ActionBlock
         
            arvm.addRecipe()
            
            arvm.resetRecipeUIImage()
        }
    }
    
    @ViewBuilder func Inputs(
        title: String,
        placeholder: String,
        text: Binding<String>,
        height: CGFloat,
        keyboard: UIKeyboardType = .default,
        orientation: Axis = .horizontal
    ) -> some View {
        
        if orientation == .horizontal {
            HStack(spacing: 5) {
                Text("\(title):")
                    .font(.seat(size: .footnote))
                    .foregroundStyle(theme.color(.textRegular))
                    .frame(maxWidth: .infinity, alignment: .leading)
                
                TextField(placeholder, text: text, axis: .vertical)
                    .frame(maxWidth: .infinity)
                    .frame(minHeight: height, alignment: .topLeading)
                    .keyboardType(keyboard)
                    
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: theme.cornerRadius)
                   .fill(Color(uiColor: .secondarySystemBackground))
            )
        }
        
        if orientation == .vertical {
            VStack(spacing: 5) {
                Text("\(title):")
                    .font(.seat(size: .footnote))
                    .foregroundStyle(theme.color(.textRegular))
                    .frame(maxWidth: .infinity, alignment: .leading)
                
                TextField(placeholder, text: text, axis: .vertical)
                    .frame(maxWidth: .infinity)
                    .frame(minHeight: height, alignment: .topLeading)
                    .keyboardType(keyboard)
                    
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: theme.cornerRadius)
                   .fill(Color(uiColor: .secondarySystemBackground))
            )
        }
        
    }

    @ViewBuilder func IngredientInputRow(
        ingredient: Binding<Ingredients>
    ) -> some View {
      HStack(spacing: 5) {
          TextField("Zutat", text: ingredient.name)
              .frame(maxWidth: .infinity, minHeight: 20, alignment: .topLeading)
              .keyboardType(.default)

          TextField("Menge", text: ingredient.val)
              .frame(maxWidth: 75, minHeight: 20, alignment: .topLeading)
              .keyboardType(.decimalPad)

          Picker("Unit", selection: ingredient.unit) {
              ForEach(MeasurementUnit.allCases, id: \.self) { option in
                  Text(option.rawValue).tag(option)
              }
          }
          .pickerStyle(MenuPickerStyle())
          .tint(theme.color(.textRegular))
          .frame(maxWidth: 75, minHeight: 20, alignment: .topLeading)
      }
    }
}

#Preview("AddRecipeView") {
    HStack{
        
    }
    .fullScreenCover(isPresented: .constant(true), content: {
        AddRecipeView(arvm: AddRecipeViewModel())
            .environmentObject(Theme())
    })
        
}

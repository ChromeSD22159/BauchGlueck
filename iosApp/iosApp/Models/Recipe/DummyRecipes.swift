//
//  DummyRecipes.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation



struct DummyRecipes {
    let recipes: [Recipe] = [
        Recipe(
            id: "1",
            user_id: "1",
            title: "Cremiges Hähnchen-Curry",
            recipeCategory: .highProtein,
            portion_size: "1 Portion",
            preparation_time: "15 Minuten",
            cooking_time: "25 Minuten",
            ingredients: [
                Ingredients(val: "200", name: "Hähnchenbrustfilet", unit: .gram),
                Ingredients(val: "100", name: "Kokosmilch (fettarm)", unit: .milliliter),
                Ingredients(val: "1", name: "Zwiebel", unit: .piece),
                Ingredients(val: "1", name: "Paprika", unit: .piece),
                Ingredients(val: "1", name: "Karotte", unit: .piece),
                Ingredients(val: "1", name: "rote Currypaste", unit: .tablespoon)
            ],
            preparation: "Hähnchen in Würfel schneiden und anbraten. Gemüse klein schneiden und hinzufügen. Kokosmilch und Currypaste unterrühren und köcheln lassen.", 
            rating: 4,
            notes: "Mit Reis oder Quinoa servieren.",
            image: "https://myfoodstory.com/wp-content/uploads/2020/10/Dhaba-Style-Chicken-Curry-1.jpg",
            is_private: false,
            last_updated: Date()
        ),
  
        Recipe(
            id: "2",
            user_id: "1",
            title: "Lachsfilet mit Kräuterquark",
            recipeCategory: .highProtein,
            portion_size: "1 Portion",
            preparation_time: "10 Minuten",
            cooking_time: "20 Minuten",
            ingredients: [
                Ingredients(val: "150", name: "Lachsfilet", unit: .gram),
                Ingredients(val: "150", name: "Magerquark", unit: .gram),
                Ingredients(val: "1", name: "Schnittlauch", unit: .piece),
                Ingredients(val: "1", name: "Salz", unit: .pinch),
                Ingredients(val: "1", name: "Pfeffer", unit: .pinch)
            ], preparation: "Lachsfilet würzen und braten oder dämpfen. Quark mit Schnittlauch, Salz und Pfeffer verrühren.",
               rating: 5,
               notes: "Mit gedünstetem Gemüse servieren.",
               image: "https://www.zaubertopf-club.de/files/styles/mainimage_normal/public/images/recipes/2022/40/drillinge_mit_lachs_und_dill-quark_thermomix.jpg?itok=gYzBq49V&t=1691077877",
               is_private: false,
               last_updated: Date()
          ),
        
        Recipe(
                id: "3",
                user_id: "1",
                title: "Vegetarische Quinoa-Bowl",
                recipeCategory: .highProtein,
                portion_size: "1 Portion",
                preparation_time: "15 Minuten",
                cooking_time: "20 Minuten",
                ingredients: [
                    Ingredients(val: "50", name: "Quinoa", unit: .gram),
                    Ingredients(val: "50", name: "Kidneybohnen", unit: .gram),
                    Ingredients(val: "1", name: "Tomate", unit: .piece),
                    Ingredients(val: "1", name: "Avocado", unit: .piece),
                    Ingredients(val: "1", name: "Limette", unit: .piece)
                ],
                preparation: "Quinoa kochen. Tomate und Avocado würfeln. Alle Zutaten mischen und mit Limettensaft abschmecken.",
                rating: 4,
                notes: "Mit frischen Kräutern garnieren.",
                image: "quinoa_bowl",
                is_private: false,
                last_updated: Date()
            ),
        
        Recipe(
            id: "4",
            user_id: "1",
            title: "Zucchini-Nudeln mit Pesto",
            recipeCategory: .lightMeals,
            portion_size: "1 Portion",
            preparation_time: "10 Minuten",
            cooking_time: "10 Minuten",
            ingredients: [
                Ingredients(val: "1", name: "Zucchini", unit: .piece),
                Ingredients(val: "2", name: "Pesto", unit: .tablespoon),
                Ingredients(val: "1", name: "Parmesan", unit: .tablespoon)
            ],
            preparation: "Zucchini in Nudeln schneiden und kurz anbraten. Pesto und Parmesan unterrühren.",
            rating: 5,
            notes: "Mit frischem Basilikum garnieren.",
            image: "zucchini_pesto",
            is_private: false,
            last_updated: Date()
        ),
        
        Recipe(
                id: "8",
                user_id: "1",
                title: "Avocado-Toast",
                recipeCategory: .vegetarian,
                portion_size: "1 Portion",
                preparation_time: "5 Minuten",
                cooking_time: "0 Minuten",
                ingredients: [
                    Ingredients(val: "1", name: "Vollkornbrot", unit: .piece),
                    Ingredients(val: "1", name: "Avocado", unit: .piece),
                    Ingredients(val: "1", name: "Zitrone", unit: .piece),
                    Ingredients(val: "1", name: "Salz", unit: .pinch),
                    Ingredients(val: "1", name: "Pfeffer", unit: .pinch)
                ],
                preparation: "Avocado zerdrücken und mit Zitronensaft, Salz und Pfeffer vermengen, auf Brot streichen.",
                rating: 5,
                notes: "Mit Koriander garnieren.",
                image: "avocado_toast",
                is_private: false,
                last_updated: Date()
            ),
        Recipe(
            id: "9",
            user_id: "1",
            title: "Beeren-Smoothie",
            recipeCategory: .vegetarian,
            portion_size: "1 Portion",
            preparation_time: "5 Minuten",
            cooking_time: "0 Minuten",
            ingredients: [
                Ingredients(val: "100", name: "Beerenmix", unit: .gram),
                Ingredients(val: "100", name: "Mandelmilch", unit: .milliliter),
                Ingredients(val: "1", name: "Banane", unit: .piece)
            ],
            preparation: "Alle Zutaten mixen.",
            rating: 5,
            notes: "Mit Minze garnieren.",
            image: "beeren_smoothie",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "10",
            user_id: "1",
            title: "Gurken-Raita",
            recipeCategory: .vegetarian,
            portion_size: "1 Portion",
            preparation_time: "10 Minuten",
            cooking_time: "0 Minuten",
            ingredients: [
                Ingredients(val: "100", name: "Gurke", unit: .gram),
                Ingredients(val: "150", name: "Joghurt", unit: .gram),
                Ingredients(val: "1", name: "Minze", unit: .piece),
                Ingredients(val: "1", name: "Salz", unit: .pinch)
            ],
            preparation: "Gurke reiben und mit Joghurt, Minze und Salz vermengen.",
            rating: 4,
            notes: "Als Beilage servieren.",
            image: "gurken_raita",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "11",
            user_id: "1",
            title: "Hühnchenbrust mit Spinat",
            recipeCategory: .lightMeals,
            portion_size: "1 Portion",
            preparation_time: "10 Minuten",
            cooking_time: "20 Minuten",
            ingredients: [
                Ingredients(val: "150", name: "Hühnchenbrustfilet", unit: .gram),
                Ingredients(val: "100", name: "Spinat", unit: .gram),
                Ingredients(val: "1", name: "Knoblauchzehe", unit: .piece),
                Ingredients(val: "1", name: "Olivenöl", unit: .tablespoon)
            ],
            preparation: "Hühnchen braten, Knoblauch und Spinat hinzufügen und kurz mitbraten.",
            rating: 4,
            notes: "Mit Zitronensaft beträufeln.",
            image: "huhn_spinat",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "12",
            user_id: "1",
            title: "Rührei mit Gemüse",
            recipeCategory: .lightMeals,
            portion_size: "1 Portion",
            preparation_time: "5 Minuten",
            cooking_time: "10 Minuten",
            ingredients: [
                Ingredients(val: "2", name: "Eier", unit: .piece),
                Ingredients(val: "50", name: "Paprika", unit: .gram),
                Ingredients(val: "50", name: "Zucchini", unit: .gram),
                Ingredients(val: "1", name: "Salz", unit: .pinch),
                Ingredients(val: "1", name: "Pfeffer", unit: .pinch)
            ],
            preparation: "Eier verquirlen und Gemüse würfeln, alles zusammen anbraten.",
            rating: 4,
            notes: "Mit Schnittlauch garnieren.",
            image: "ruehrei_gemuese",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "13",
            user_id: "1",
            title: "Hirse-Salat",
            recipeCategory: .vegetarian,
            portion_size: "1 Portion",
            preparation_time: "15 Minuten",
            cooking_time: "20 Minuten",
            ingredients: [
                Ingredients(val: "50", name: "Hirse", unit: .gram),
                Ingredients(val: "50", name: "Gurke", unit: .gram),
                Ingredients(val: "50", name: "Tomate", unit: .gram),
                Ingredients(val: "1", name: "Zitrone", unit: .piece),
                Ingredients(val: "1", name: "Olivenöl", unit: .tablespoon)
            ],
            preparation: "Hirse kochen, Gemüse würfeln und alles vermischen. Mit Zitronensaft und Olivenöl abschmecken.",
            rating: 4,
            notes: "Mit Petersilie garnieren.",
            image: "hirse_salat",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "14",
            user_id: "1",
            title: "Tofu-Stir Fry",
            recipeCategory: .vegetarian,
            portion_size: "1 Portion",
            preparation_time: "10 Minuten",
            cooking_time: "15 Minuten",
            ingredients: [
                Ingredients(val: "100", name: "Tofu", unit: .gram),
                Ingredients(val: "50", name: "Brokkoli", unit: .gram),
                Ingredients(val: "50", name: "Paprika", unit: .gram),
                Ingredients(val: "1", name: "Sojasoße", unit: .tablespoon),
                Ingredients(val: "1", name: "Sesamöl", unit: .tablespoon)
            ],
            preparation: "Tofu und Gemüse würfeln und in Sesamöl anbraten, Sojasoße hinzufügen.",
            rating: 5,
            notes: "Mit Sesamsamen garnieren.",
            image: "tofu_stir_fry",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "15",
            user_id: "1",
            title: "Quark mit Beeren",
            recipeCategory: .lightMeals,
            portion_size: "1 Portion",
            preparation_time: "5 Minuten",
            cooking_time: "0 Minuten",
            ingredients: [
                Ingredients(val: "150", name: "Magerquark", unit: .gram),
                Ingredients(val: "50", name: "Beerenmix", unit: .gram),
                Ingredients(val: "1", name: "Honig", unit: .teaspoon)
            ],
            preparation: "Quark mit Beeren und Honig verrühren.",
            rating: 5,
            notes: "Mit Minzblättern garnieren.",
            image: "quark_beeren",
            is_private: false,
            last_updated: Date()
        ),
        Recipe(
            id: "16",
            user_id: "1",
            title: "Eiersalat",
            recipeCategory: .lightMeals,
            portion_size: "1 Portion",
            preparation_time: "10 Minuten",
            cooking_time: "10 Minuten",
            ingredients: [
                Ingredients(val: "2", name: "Eier", unit: .piece),
                Ingredients(val: "50", name: "Joghurt", unit: .gram),
                Ingredients(val: "1", name: "Senf", unit: .teaspoon),
                Ingredients(val: "1", name: "Schnittlauch", unit: .piece),
                Ingredients(val: "1", name: "Salz", unit: .pinch),
                Ingredients(val: "1", name: "Pfeffer", unit: .pinch)
            ],
            preparation: "Eier kochen, würfeln und mit Joghurt, Senf, Schnittlauch, Salz und Pfeffer vermischen.",
            rating: 4,
            notes: "Mit Salatblättern servieren.",
            image: "eiersalat",
            is_private: false,
            last_updated: Date()
        ),
    ]
}

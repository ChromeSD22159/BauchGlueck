//
//  DayMeetOverView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct DayMeetOverView: View {
    let date: Date
    let mealList: [PlannedMeal]
    
    private var totalProtein: Int {
        mealList.map { $0.protein }.reduce(0, +)
    }
    
    private var totalKcal: Int {
        mealList.map { $0.kcal }.reduce(0, +)
    }
    
    @EnvironmentObject var theme: Theme
    
    private let borderRadius: CGFloat = 20
    
    var body: some View {
        NavigationLink(destination: {
            
        }, label: {
            VStack(alignment: .leading, spacing: 0) {
                header(icon: "calendar", date: date)
                
                ForEach(Array(mealList.enumerated()), id: \.element.id) { index, meal in
                    content(count: index + 1, recipe: meal.recipe, protein: meal.protein, kcal: meal.kcal)
                }
                
                footer(protein: totalProtein, kcal: totalKcal)
            }
        })
    }
    
    @ViewBuilder func header(icon: String, date: Date) -> some View {
        ZStack {
            HStack {
                Image(systemName: icon)
                    .foregroundStyle(theme.color(.textComplimentary))
                    .font(.body)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)
                
                Text(date.getWeekday)
                    .font(.body)
                    .fontWeight(.semibold)
                    .foregroundStyle(theme.color(.textComplimentary))
                
                Text(date, style: .date)
                    .font(.body)
                    .fontWeight(.semibold)
                    .foregroundStyle(theme.color(.textComplimentary))
                
                Spacer()
            }
        }
        .padding(.horizontal, theme.paddingHorizontal)
        .padding(.vertical,  theme.paddingVertical)
        .background(
            theme.gradient(array: [theme.color(.primary), theme.color(.primaryVariant)])
        )
        .clipShape(
            .rect(
                topLeadingRadius: theme.cornerRadius,
                bottomLeadingRadius: 0,
                bottomTrailingRadius: 0,
                topTrailingRadius: theme.cornerRadius
            )
        )
    }
    
    @ViewBuilder func content(count: Int, recipe: String, protein: Int, kcal: Int) -> some View {
        HStack {
            Text("\(count).")
                .font(.body)
                .foregroundStyle(theme.color(.textRegular))

            Text(recipe)
                .font(.body)
                .foregroundStyle(theme.color(.textRegular))
            
            Spacer()
            
            Text("\(protein)g/\(kcal)g")
                .font(.body)
                .foregroundStyle(theme.color(.textRegular))
        }
        .padding(.horizontal, theme.paddingHorizontal)
        .padding(.vertical,  theme.paddingVertical)
        .background(theme.color(.backgroundVariant))
    }
    
    @ViewBuilder func footer(protein: Int, kcal: Int) -> some View {
        HStack {
            Text("Gesamt:")
                .font(.body)
                .foregroundStyle(theme.color(.textRegular))
            
            Spacer()
            
            Text("\(protein)g/\(kcal)g")
                .font(.body)
                .foregroundStyle(theme.color(.textRegular))
        }
        .padding(.horizontal, theme.paddingHorizontal)
        .padding(.vertical,  theme.paddingVertical)
        .background(theme.color(.backgroundVariant))
        .clipShape(
            .rect(
                topLeadingRadius: 0,
                bottomLeadingRadius: theme.cornerRadius,
                bottomTrailingRadius: theme.cornerRadius,
                topTrailingRadius: 0
            )
        )
    }
}

struct PlannedMeal: Hashable {
    var id = UUID()
    var recipe: String
    var protein: Int
    var kcal: Int
}

#Preview("Light") {
    DayMeetOverView(
        date: Date(),
        mealList: [
            PlannedMeal(recipe: "Spagetti", protein: 20, kcal: 5),
            PlannedMeal(recipe: "Spagetti", protein: 20, kcal: 5)
        ]
    ).preferredColorScheme(.light)
}

#Preview("Dark") {
    DayMeetOverView(
        date: Date(),
        mealList: [
            PlannedMeal(recipe: "Spagetti", protein: 20, kcal: 5),
            PlannedMeal(recipe: "Spagetti", protein: 20, kcal: 5)
        ]
    ).preferredColorScheme(.dark)
}

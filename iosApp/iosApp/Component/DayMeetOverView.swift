//
//  DayMeetOverView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct DayMeetOverView: View {
    var name: String
    var theme = Theme.shared
    
    private let borderRadius: CGFloat = 20
    
    var body: some View {
        NavigationLink(destination: {
            
        }, label: {
            VStack(alignment: .leading, spacing: 0) {
                header(icon: "calendar", text: "Hallo")
                
                content(count: 1, recipe: "Hallo", protein: 20, kcal: 10)
                content(count: 2, recipe: "Hallo", protein: 50, kcal: 5)
                footer(protein: 70, kcal: 15)
            }
        })
    }
    
    @ViewBuilder func header(icon: String, text: String) -> some View {
        ZStack {
            HStack {
                Image(systemName: icon)
                    .foregroundStyle(theme.color(.textComplimentary))
                    .font(.body)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)
                
                Text(name)
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

#Preview("Light") {
    DayMeetOverView(name: "dsads")
        .preferredColorScheme(.light)
}

#Preview("Dark") {
    DayMeetOverView(name: "dsads")
        .preferredColorScheme(.dark)
}

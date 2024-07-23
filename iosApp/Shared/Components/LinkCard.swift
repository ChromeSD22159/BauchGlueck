//
//  LinkCard.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct LinkCard: View {
    @EnvironmentObject var theme: Theme
    var name: String
    var color: Color
    var icon: String

    init(name: String, icon: String, explicitColor: Color) {
        self.name = name
        self.color = explicitColor
        self.icon = icon
    }
    
    var body: some View {
        ZStack {
            color
            
            LinearGradient(
                stops: [
                    .init(color: Color.white.opacity(0.75), location: 0.0),
                    .init(color: Color.white.opacity(0.1), location: 0.3),
                    .init(color: Color.white.opacity(0), location: 0.7),
                    .init(color: Color.white.opacity(0), location: 1.0)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            
            Image(systemName: icon)
                .font(.system(size: 96))
                .fontWeight(.heavy)
                .foregroundStyle(theme.color(.textComplimentary).opacity(0.2))
            
            VStack {
                Spacer(minLength: 75)
                
                HStack {
                    Spacer()
                    
                    Text(name)
                        .font(.kodchasanBold(size: .title))
                        .foregroundStyle(theme.color(.textComplimentary))
                }
            }
            .padding(16)
        }
        .cornerRadius(16)
    }
}

#Preview {
    LinkCard(name: "Timer", icon: "stopwatch", explicitColor: Theme().color(.secondary))
        .frame(width: 150, height: 150)
        .environmentObject(Theme())
}

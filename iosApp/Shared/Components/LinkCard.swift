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

    init(name: String, explicitColor: Color) {
        self.name = name
        self.color = explicitColor
    }
    
    var body: some View {
        ZStack {
            color
            
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

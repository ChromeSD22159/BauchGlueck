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
        ZStack {
            BackgroundImage()
            VStack {
                HStack {
                    Text("Add your recipe")
                        .foregroundStyle(theme.color(.primary))
                        .font(.kodchasanBold(size: .title3))
                    
                    Spacer()
                    
                    Button {
                        arvm.isAddRecipeSheet.toggle()
                    } label: {
                        HStack {
                            Image(systemName: "xmark")
                        }.foregroundStyle(theme.color(.textRegular))
                    }
                }.padding(16)
                
                Spacer()
            }
        }
    }
}

//
//  AddWaterView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct AddWaterView: View {
    @EnvironmentObject var theme: Theme
    @ObservedObject var awvm: AddWaterViewModel
    var body: some View {
        ZStack {
            BackgroundImage()
            VStack {
                HStack {
                    Text("Add Water")
                        .foregroundStyle(theme.color(.primary))
                        .font(.kodchasanBold(size: .title3))
                    
                    Spacer()
                    
                    Button {
                        awvm.isAddWaterSheet.toggle()
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

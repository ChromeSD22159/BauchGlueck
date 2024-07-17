//
//  BackgroundImage.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct BackgroundImage: View {
    @EnvironmentObject var theme: Theme
    var body: some View {
        VStack(alignment: .trailing) {
            HStack(alignment: .top) {
                Spacer()
                ZStack(alignment: .topTrailing) {
                    
                    Image(.waveBehinde)
                        .opacity(0.3)
                        .frame(width: 266.15442, height: 283.81583, alignment: .topTrailing)
                    
                    Image(.waveAbove)
                        .opacity(0.3)
                        .frame(width: 266.15442, height: 283.81583, alignment: .topTrailing)

                    Image(.logoTransparent)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 140, height: 140)
                        .padding(.top, 80)
                        .padding(.trailing, 30)
                        .clipped()
               }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.color(.background))
        .edgesIgnoringSafeArea(.all)
    }
}

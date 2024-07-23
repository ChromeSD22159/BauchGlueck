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
    @EnvironmentObject var auth: FirebaseAuthManager
    @EnvironmentObject var arvm: AddRecipeViewModel
    @Environment(\.scenePhase) var scenePhase

    var body: some View {
        ZStack {
            theme.color(.background).ignoresSafeArea()
            
            theme.backgroundImageWithOutImage(
                background: .backgroundVariant,
                backgroundOpacity: 1
            )
      
            ScrollView{
                VStack(alignment: .leading, spacing: 16) {
                    /*ForEach(firestoreTimerManager.timerList) { timer in
                        TimerCard(countdown: timer)
                            .padding(.horizontal, 16)
                    }*/
                }
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

#Preview {
    RecipesOverView()
        .environmentObject(Theme())
        .environmentObject(FirebaseAuthManager())
}

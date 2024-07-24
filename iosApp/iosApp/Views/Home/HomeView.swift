import SwiftUI
import Shared

struct HomeView: View {
   
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var authManager: FirebaseAuthManager
    
    @StateObject var arvm: AddRecipeViewModel = AddRecipeViewModel()
    @StateObject var awvm: AddWaterViewModel = AddWaterViewModel()
    @StateObject var atvm: AddTimerViewModel = AddTimerViewModel()
    @StateObject var awwvm: AddWeightViewModel = AddWeightViewModel()
    
    @State var isSettingSheet = false
    
    var body: some View {
        NavigationStack {
            ZStack {
                theme.color(.backgroundVariant).ignoresSafeArea()

                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        LazyVGrid(
                            columns: [
                                GridItem(.flexible(), spacing: 16),
                                GridItem(.flexible(), spacing: 16)
                            ],
                            spacing: 16
                        ) {
                            NavigationLink {
                                RecipesOverView()
                                    .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                                    .environmentObject(arvm)
                            } label: {
                                LinkCard(name: "Mahlzeiten", icon: "fork.knife", explicitColor: Theme().color(.primary))
                            }

                            NavigationLink {
                                EmptyView()
                                    .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                            } label: {
                                LinkCard(name: "Medikation", icon: "pills.fill", explicitColor: Theme().color(.primaryVariant))
                            }

                            NavigationLink {
                                TimerOverView()
                                    .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                            } label: {
                                LinkCard(name: "Timer", icon: "stopwatch", explicitColor: Theme().color(.secondary))
                            }

                            NavigationLink {
                                EmptyView()
                                    .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                            } label: {
                                LinkCard(name: "Einkaufsliste", icon: "cart", explicitColor: Theme().color(.secondaryVariant))
                            }
                        }

                        WeightCardView()

                        WaterIntakeCardView()
                    }
                    .padding(.horizontal, 16)
                }
                .scrollIndicators(.never)
                .navigationTitle("BauchGlück")
                .navigationBarTitleDisplayMode(.automatic)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        HStack {
                            RoundedHeaderButton(icon: "gear") {
                                isSettingSheet.toggle()
                            }

                            ContextMenu()
                        }
                    }
                }
            }
            .settingSheet(isSettingSheet: $isSettingSheet, authManager: authManager)
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
            
            Button {
                awvm.isAddWaterSheet.toggle()
                awwvm.objectWillChange.send()
            } label: {
                Label("Add Water", systemImage: "waterbottle.fill")
            }
            
            Button {
                atvm.isAddTimerSheet.toggle()
            } label: {
                Label("Add Timer", systemImage: "timer.circle.fill")
            }
            
            Button {
                awwvm.isAddWeightSheet.toggle()
                awwvm.objectWillChange.send()
            } label: {
                Label("Add Weight", systemImage: "chart.line.downtrend.xyaxis.circle")
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
        .fullScreenCover(isPresented: $awvm.isAddWaterSheet, onDismiss: { awvm.resetDrinkAmount() }, content: {
            AddWaterView(awvm: awvm)
        })
        .fullScreenCover(isPresented: $arvm.isAddRecipeSheet, onDismiss: {}, content: {
            AddRecipeView(arvm: arvm)
        })
        .fullScreenCover(isPresented: $atvm.isAddTimerSheet, onDismiss: { }, content: {
            AddTimerView(atvm: atvm)
        })
        .fullScreenCover(isPresented: $awwvm.isAddWeightSheet, onDismiss: { awwvm.resetWeightAmount()  }, content: {
            AddWeightView(awvm: awwvm)
        })
    }
}

#Preview("Light") {
    HomeView()
}

#Preview("Dark") {
    HomeView()
        .environmentObject(Theme())
        .environmentObject(FirebaseAuthManager())
        .preferredColorScheme(.dark)
}



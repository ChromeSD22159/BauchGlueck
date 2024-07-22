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
        NavigationView {
            ZStack {
                theme.color(.backgroundVariant).ignoresSafeArea()
                
                ScrollView{
                    VStack(alignment: .leading, spacing: 16) {
                        NavigationLink{
                            EmptyView()
                                .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                        } label: {
                            LinkCard(name: "Mahlzeiten", explicitColor: Theme().color(.primary))
                        }
                        
                        WeightCardView()
                        
                        WaterIntakeCardView()
                        
                        NavigationLink{
                            EmptyView()
                                .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                        } label: {
                            LinkCard(name: "Medikation", explicitColor: Theme().color(.primaryVariant))
                        }
                        
                        NavigationLink{
                            TimerOverView()
                                .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                        } label: {
                            LinkCard(name: "Timer", explicitColor: Theme().color(.secondary))
                        }
                        
                        NavigationLink{
                            EmptyView()
                                .navigationBackButton(color: theme.color(.textRegular), text: "Home")
                        } label: {
                            LinkCard(name: "Einkaufsliste", explicitColor: Theme().color(.secondaryVariant))
                        }
                    }
                }
                .scrollIndicators(.never)
                .padding(.horizontal, 16)
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
        .fullScreenCover(isPresented: $awvm.isAddWaterSheet, onDismiss: {}, content: {
            AddWaterView(awvm: awvm)
        })
        .fullScreenCover(isPresented: $arvm.isAddRecipeSheet, onDismiss: {}, content: {
            AddRecipeView(arvm: arvm)
        })
        .fullScreenCover(isPresented: $atvm.isAddTimerSheet, onDismiss: {}, content: {
            AddTimerView(atvm: atvm)
        })
        .fullScreenCover(isPresented: $awwvm.isAddWeightSheet, onDismiss: {}, content: {
            AddWeightView(awvm: awwvm)
        })
    }
    
    static func getCurrentWeekDates() -> [Date] {
        let today = Date()
        if let weekDates = today.datesOfWeek() {
            return weekDates
        }
        return []
    }
}

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

#Preview("Light") {
    HomeView()
}

#Preview("Dark") {
    HomeView()
        .environmentObject(Theme())
        .environmentObject(FirebaseAuthManager())
        .preferredColorScheme(.dark)
}



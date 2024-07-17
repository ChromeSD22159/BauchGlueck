import SwiftUI
import Shared

struct HomeView: View {
    @State private var showContent = false
    @State private var searchText = ""
    @State private var content = ["A", "B", "C", "D", "E", "F", "G"]
    @State private var week: [Date] = []
    
   
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var authManager: FirebaseAuthManager
    
    @StateObject var arvm: AddRecipeViewModel = AddRecipeViewModel()
    @StateObject var awvm: AddWaterViewModel = AddWaterViewModel()
    @StateObject var atvm: AddTimerViewModel = AddTimerViewModel()
    
    @State var isSettingSheet = false
    
    var body: some View {
        NavigationView {
            
            ZStack {
                theme.color(.background).ignoresSafeArea()
                
                ScrollView{
                    VStack(alignment: .leading, spacing: 16) {
                        ForEach(week, id: \.self) { date in
                            DayMeetOverView(
                                date: date,
                                mealList: [
                                    PlannedMeal(recipe: "Spaghetti", protein: 20, kcal: 5),
                                    PlannedMeal(recipe: "Brot", protein: 20, kcal: 5)
                                ]
                            )
                        }
                    }
                }
                .scrollIndicators(.never)
                .padding(.horizontal, 16)
                .navigationTitle("BauchGlück")
                .navigationBarTitleDisplayMode(.automatic)
                .searchable(text: $searchText)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        RoundedHeaderButton(icon: "gear") { 
                            isSettingSheet.toggle()
                        }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Menu(content: {
                            Button {
                                arvm.isAddRecipeSheet.toggle()
                            } label: {
                                Label("Add Recipe", systemImage: "fork.knife.circle.fill")
                            }
                            
                            Button {
                                awvm.isAddWaterSheet.toggle()
                            } label: {
                                Label("Add Water", systemImage: "waterbottle.fill")
                            }
                            
                            Button {
                                atvm.isAddTimerSheet.toggle()
                            } label: {
                                Label("Add Timer", systemImage: "timer.circle.fill")
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
            }
            .settingSheet(isSettingSheet: $isSettingSheet, authManager: authManager)
            .fullScreenCover(isPresented: $awvm.isAddWaterSheet, onDismiss: {}, content: {
                AddWaterView(awvm: awvm)
            })
            .fullScreenCover(isPresented: $arvm.isAddRecipeSheet, onDismiss: {}, content: {
                AddRecipeView(arvm: arvm)
            })
            .fullScreenCover(isPresented: $atvm.isAddTimerSheet, onDismiss: {}, content: {
                AddTimerView(atvm: atvm)
            })
            
        }.onAppear {
            self.week = HomeView.getCurrentWeekDates()
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
    
    static func getCurrentWeekDates() -> [Date] {
        let today = Date()
        if let weekDates = today.datesOfWeek() {
            return weekDates
        }
        return []
    }
}

#Preview("Light") {
    HomeView()
}

#Preview("Dark") {
    HomeView()
        .environmentObject(Theme())
        .preferredColorScheme(.dark)
}



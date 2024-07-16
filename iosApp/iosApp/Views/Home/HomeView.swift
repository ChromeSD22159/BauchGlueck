import SwiftUI
import Shared

struct HomeView: View {
    @State private var showContent = false
    @State private var searchText = ""
    @State private var content = ["A", "B", "C", "D", "E", "F", "G"]
    @State private var week: [Date] = []
    @State var isSettingSheet = false
    
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var authManager: FirebaseAuthManager
    
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
                        RoundedHeaderButton(icon: "pencil") { 
                            isSettingSheet.toggle()
                        }
                    }
                }
            }
            .settingSheet(isSettingSheet: $isSettingSheet, authManager: authManager)
            
        }.onAppear {
            self.week = HomeView.getCurrentWeekDates()
        }
    }
    
    
    @ViewBuilder
    func RoundedHeaderButton(icon: String, action: @escaping () -> Void) -> some View {
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

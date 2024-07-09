import SwiftUI
import Shared

struct HomeView: View {
    @State private var showContent = false
    @State private var searchText = ""
    @State private var content = ["A", "B", "C", "D", "E", "F", "G"]
    
    var theme = Theme.shared
    
    var body: some View {
        NavigationView {
            
            ZStack {
                theme.color(.background).ignoresSafeArea()
                
                ScrollView{
                    VStack(alignment: .leading, spacing: 16) {
                        ForEach(content, id: \.self) { name in
                            DayMeetOverView(
                                date: Date(),
                                mealList: [
                                    PlannedMeal(recipe: "Spagetti", protein: 20, kcal: 5),
                                    PlannedMeal(recipe: "Spagetti", protein: 20, kcal: 5)
                                ]
                            )
                        }
                    }
                }
                .scrollIndicators(.never)
                .padding(.horizontal, 16)
                .navigationTitle("BauchGlück")
                .navigationBarTitleDisplayMode(.large)
                .searchable(text: $searchText)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        RoundedHeaderButton(icon: "pencil") { }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        RoundedHeaderButton(icon: "magnifyingglass") { }
                    }
                }
            }
            
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
}

#Preview("Hello World!") {
    HomeView()
}

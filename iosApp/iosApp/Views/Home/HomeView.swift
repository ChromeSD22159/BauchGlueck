import SwiftUI
import Shared

struct HomeView: View {
    @State private var showContent = false
    @State private var searchText = ""
    @State private var content = ["A", "B", "C", "D", "E", "F", "G"]
    @State private var week: [Date] = []
    
   
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var authManager: FirebaseAuthManager
    
    
    @State var isSettingSheet = false
    @State var isAddRecipeSheet = false
    @State var isAddWaterSheet = false
    
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
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Menu(content: {
                            Button {
                                isAddRecipeSheet.toggle()
                            } label: {
                                Label("Add Recipe", systemImage: "fork.knife.circle.fill")
                            }
                            
                            Button {
                                isAddWaterSheet.toggle()
                            } label: {
                                Label("Add Water", systemImage: "waterbottle.fill")
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
            .fullScreenCover(isPresented: $isAddWaterSheet, onDismiss: {}, content: {
                ZStack {
                    backgroundImage()
                    VStack {
                        HStack {
                            Text("Add Water")
                                .font(.title3)
                            
                            Spacer()
                            
                            Button {
                                isAddWaterSheet.toggle()
                            } label: {
                                HStack {
                                    Image(systemName: "xmark")
                                    
                                }.foregroundStyle(theme.color(.textRegular))
                            }
                        }.padding(16)
                        
                        Spacer()
                    }
                }
            })
            .fullScreenCover(isPresented: $isAddRecipeSheet, onDismiss: {}, content: {
                ZStack {
                    backgroundImage()
                    VStack {
                        HStack {
                            Text("Add your recipe")
                                .font(.title3)
                            
                            Spacer()
                            
                            Button {
                                isAddRecipeSheet.toggle()
                            } label: {
                                HStack {
                                    Image(systemName: "xmark")
                                }.foregroundStyle(theme.color(.textRegular))
                            }
                        }.padding(16)
                        
                        Spacer()
                    }
                }
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
    
    @ViewBuilder func backgroundImage() -> some View {
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

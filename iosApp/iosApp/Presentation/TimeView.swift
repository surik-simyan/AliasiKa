import SwiftUI
import MultiPlatformLibrary

struct TimeView: View {
    private var viewModel = MainViewModelHelper().mainViewModel
    private var time = stride(from: 20, to: 121, by: 5).map { $0 }
    @State private var selectedTime = 45
    @State private var pointsSelected = false
    
    var body: some View {
        VStack (spacing: 20) {
            Image("alarm.fill")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 64.0, height: 64.0)
                .foregroundColor(Color(UIColor.primary))
            Text(SharedStrings.shared.playTime.localized())
            Picker("Points", selection: $selectedTime) {
                ForEach(time, id: \.self) {
                    Text(String($0))
                }
            }
            .scaledToFit()
            .pickerStyle(.wheel)
            .padding(.vertical, -20)
            NavigationLink(destination: TeamsView(), isActive: $pointsSelected) {
                Button(action: {
                    viewModel.time = Float(selectedTime)
                    pointsSelected = true
                }) {
                    Text("Next")
                        .font(.system(size: 18))
                        .padding()
                        .foregroundColor(.white)
                }
                .background(Color(UIColor.primary))
                .cornerRadius(CGFloat.dp(value: 4))
            }
        }
    }
}


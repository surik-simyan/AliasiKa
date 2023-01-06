import SwiftUI
import MultiPlatformLibrary

struct PointsView: View {
    private var viewModel: GameViewModel
    private let points = stride(from: 5, to: 156, by: 5).map { $0 }
    @State private var selectedPoints = 100
    @State private var pointsSelected = false
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
    }
    
    var body: some View {
        VStack (spacing: 20) {
            Image("checkmark.circle.fill")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 64.0, height: 64.0)
                .foregroundColor(Color(UIColor.primary))
            Text(SharedStrings.shared.playPoints.localized())
            Picker("Points", selection: $selectedPoints) {
                ForEach(points, id: \.self) {
                    Text(String($0))
                }
            }
            .scaledToFit()
            .pickerStyle(.wheel)
            .padding(.vertical, -20)
            NavigationLink(destination: TimeView(viewModel: self.viewModel), isActive: $pointsSelected) {
                Button(action: {
                    viewModel.points = Float(selectedPoints)
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

struct PointsView_Previews: PreviewProvider {
    static var previews: some View {
        PointsView(viewModel: GameViewModel())
    }
}

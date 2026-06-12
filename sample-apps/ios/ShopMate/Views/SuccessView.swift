import SwiftUI

struct SuccessView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 64))
                .foregroundStyle(.green)
            Text("Order Placed Successfully!")
                .font(.title2.bold())
                .multilineTextAlignment(.center)
                .accessibilityIdentifier("order_success_message")
            Button("Back to Home") {
                appState.backToHome()
            }
            .buttonStyle(.borderedProminent)
            .accessibilityIdentifier("success_back_home")
        }
        .padding()
    }
}

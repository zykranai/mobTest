import SwiftUI

struct WelcomeView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        VStack(spacing: 24) {
            Spacer()
            Image(systemName: "bag.fill")
                .font(.system(size: 72))
                .foregroundStyle(.blue)
            Text("ShopMate")
                .font(.largeTitle.bold())
                .accessibilityIdentifier("welcome_title")
            Text("Your agentic test playground")
                .foregroundStyle(.secondary)
            Spacer()
            Button("Get Started") {
                appState.goToLogin()
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
            .accessibilityIdentifier("welcome_get_started")
            .padding(.bottom, 40)
        }
        .padding()
    }
}

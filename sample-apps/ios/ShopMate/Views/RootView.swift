import SwiftUI

struct RootView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        Group {
            switch appState.screen {
            case .welcome:
                WelcomeView()
            case .login:
                LoginView()
            case .home:
                HomeView()
            case .products(let category):
                ProductListView(category: category)
            case .cart:
                CartView()
            case .success:
                SuccessView()
            }
        }
        .animation(.easeInOut, value: appState.screen)
    }
}

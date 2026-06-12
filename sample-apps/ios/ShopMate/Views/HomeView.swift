import SwiftUI

struct HomeView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        NavigationStack {
            List {
                Section("Browse categories") {
                    ForEach(ProductCategory.allCases) { category in
                        Button(category.rawValue) {
                            appState.openCategory(category)
                        }
                        .accessibilityIdentifier("category_\(category.rawValue.lowercased())")
                    }
                }

                if !appState.lastMessage.isEmpty {
                    Section("Status") {
                        Text(appState.lastMessage)
                    }
                }
            }
            .navigationTitle("Home")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Cart (\(appState.cart.count))") {
                        appState.openCart()
                    }
                    .accessibilityIdentifier("home_cart_button")
                }
            }
        }
    }
}

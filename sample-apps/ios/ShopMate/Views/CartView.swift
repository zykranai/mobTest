import SwiftUI

struct CartView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Your Cart")
                .font(.title.bold())
                .accessibilityIdentifier("cart_heading")

            if appState.cart.isEmpty {
                Text("No items yet")
                    .foregroundStyle(.secondary)
                    .accessibilityIdentifier("cart_empty_message")
            } else {
                List(appState.cart) { item in
                    HStack {
                        Text(item.name)
                        Spacer()
                        Text(String(format: "$%.2f", item.price))
                    }
                }
                .listStyle(.plain)

                Text("Total: \(String(format: "$%.2f", appState.cartTotal))")
                    .font(.headline)
                    .accessibilityIdentifier("cart_total_label")
            }

            Button("Checkout") {
                appState.checkout()
            }
            .buttonStyle(.borderedProminent)
            .frame(maxWidth: .infinity)
            .accessibilityIdentifier("checkout_button")
            .disabled(appState.cart.isEmpty)

            Button("Back to Home") {
                appState.backToHome()
            }
            .accessibilityIdentifier("cart_back_home")

            Spacer()
        }
        .padding()
    }
}

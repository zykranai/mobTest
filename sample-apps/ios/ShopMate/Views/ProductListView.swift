import SwiftUI

struct ProductListView: View {
    @EnvironmentObject private var appState: AppState
    let category: ProductCategory

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                List(Product.catalog(for: category)) { product in
                    HStack {
                        VStack(alignment: .leading) {
                            Text(product.name)
                                .font(.headline)
                            Text(String(format: "$%.2f", product.price))
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                        Button("Add to Cart") {
                            appState.addToCart(product)
                        }
                        .buttonStyle(.bordered)
                        .accessibilityIdentifier("add_to_cart_\(product.id)")
                    }
                }
                .listStyle(.plain)

                Button("Go to Cart (\(appState.cart.count))") {
                    appState.openCart()
                }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
                .padding()
                .accessibilityIdentifier("products_go_to_cart")
            }
            .navigationTitle(category.rawValue)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Back") {
                        appState.backToHome()
                    }
                    .accessibilityIdentifier("products_back_button")
                }
            }
        }
    }
}

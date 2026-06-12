import Foundation

enum AppScreen: Equatable {
    case welcome
    case login
    case home
    case products(ProductCategory)
    case cart
    case success
}

@MainActor
final class AppState: ObservableObject {
    @Published var screen: AppScreen = .welcome
    @Published var email = ""
    @Published var password = ""
    @Published var cart: [Product] = []
    @Published var lastMessage = ""

    var cartTotal: Double {
        cart.reduce(0) { $0 + $1.price }
    }

    func goToLogin() {
        screen = .login
    }

    func signIn() {
        guard !email.isEmpty, !password.isEmpty else {
            lastMessage = "Please enter email and password."
            return
        }
        screen = .home
        lastMessage = "Welcome back!"
    }

    func openCategory(_ category: ProductCategory) {
        screen = .products(category)
    }

    func addToCart(_ product: Product) {
        cart.append(product)
        lastMessage = "Added \(product.name) to cart."
    }

    func openCart() {
        screen = .cart
    }

    func checkout() {
        guard !cart.isEmpty else {
            lastMessage = "Your cart is empty."
            return
        }
        cart.removeAll()
        screen = .success
    }

    func backToHome() {
        screen = .home
    }
}

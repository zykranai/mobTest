import Foundation

enum ProductCategory: String, CaseIterable, Identifiable {
    case electronics = "Electronics"
    case clothing = "Clothing"
    case groceries = "Groceries"

    var id: String { rawValue }
}

struct Product: Identifiable, Hashable {
    let id: String
    let name: String
    let price: Double
    let category: ProductCategory
}

extension Product {
    static func catalog(for category: ProductCategory) -> [Product] {
        switch category {
        case .electronics:
            return [
                Product(id: "e1", name: "Wireless Headphones", price: 79.99, category: category),
                Product(id: "e2", name: "Smart Watch", price: 199.00, category: category),
                Product(id: "e3", name: "USB-C Hub", price: 34.50, category: category)
            ]
        case .clothing:
            return [
                Product(id: "c1", name: "Denim Jacket", price: 59.00, category: category),
                Product(id: "c2", name: "Running Shoes", price: 89.00, category: category),
                Product(id: "c3", name: "Cotton T-Shirt", price: 19.99, category: category)
            ]
        case .groceries:
            return [
                Product(id: "g1", name: "Organic Apples", price: 4.99, category: category),
                Product(id: "g2", name: "Whole Milk", price: 3.49, category: category),
                Product(id: "g3", name: "Sourdough Bread", price: 5.25, category: category)
            ]
        }
    }
}

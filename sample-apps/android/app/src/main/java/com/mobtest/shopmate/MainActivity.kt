package com.mobtest.shopmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

enum class Screen { Welcome, Login, Home, Products, Cart, Success }

enum class Category(val label: String) {
    Electronics("Electronics"),
    Clothing("Clothing"),
    Groceries("Groceries")
}

data class Product(val id: String, val name: String, val price: Double, val category: Category)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ShopMateApp() }
    }
}

@Composable
fun ShopMateApp() {
    var screen by remember { mutableStateOf(Screen.Welcome) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cart by remember { mutableStateOf(listOf<Product>()) }
    var category by remember { mutableStateOf(Category.Electronics) }
    var message by remember { mutableStateOf("") }

    MaterialTheme {
        when (screen) {
            Screen.Welcome -> WelcomeScreen { screen = Screen.Login }
            Screen.Login -> LoginScreen(
                email, password, message,
                onEmail = { email = it },
                onPassword = { password = it },
                onSignIn = {
                    if (email.isBlank() || password.isBlank()) message = "Please enter email and password."
                    else { message = ""; screen = Screen.Home }
                }
            )
            Screen.Home -> HomeScreen(
                cartCount = cart.size,
                message = message,
                onCategory = { category = it; screen = Screen.Products },
                onCart = { screen = Screen.Cart }
            )
            Screen.Products -> ProductScreen(
                category = category,
                cartCount = cart.size,
                onBack = { screen = Screen.Home },
                onAdd = { product ->
                    cart = cart + product
                    message = "Added ${product.name} to cart."
                },
                onCart = { screen = Screen.Cart }
            )
            Screen.Cart -> CartScreen(
                cart = cart,
                onCheckout = {
                    if (cart.isEmpty()) message = "Your cart is empty."
                    else { cart = emptyList(); screen = Screen.Success }
                },
                onBack = { screen = Screen.Home }
            )
            Screen.Success -> SuccessScreen { screen = Screen.Home }
        }
    }
}

private fun catalog(category: Category): List<Product> = when (category) {
    Category.Electronics -> listOf(
        Product("e1", "Wireless Headphones", 79.99, category),
        Product("e2", "Smart Watch", 199.0, category),
        Product("e3", "USB-C Hub", 34.5, category)
    )
    Category.Clothing -> listOf(
        Product("c1", "Denim Jacket", 59.0, category),
        Product("c2", "Running Shoes", 89.0, category),
        Product("c3", "Cotton T-Shirt", 19.99, category)
    )
    Category.Groceries -> listOf(
        Product("g1", "Organic Apples", 4.99, category),
        Product("g2", "Whole Milk", 3.49, category),
        Product("g3", "Sourdough Bread", 5.25, category)
    )
}

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ShoppingBag, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("ShopMate", style = MaterialTheme.typography.headlineLarge)
        Text("Your agentic test playground", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.semantics { contentDescription = "welcome_get_started" }
        ) { Text("Get Started") }
    }
}

@Composable
private fun LoginScreen(
    email: String, password: String, message: String,
    onEmail: (String) -> Unit, onPassword: (String) -> Unit, onSignIn: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Sign In", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { contentDescription = "login_heading" })
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email, onValueChange = onEmail, label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "login_email_field" }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password, onValueChange = onPassword, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "login_password_field" }
        )
        if (message.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "login_submit_btn" }
        ) { Text("Sign In") }
    }
}

@Composable
private fun HomeScreen(cartCount: Int, message: String, onCategory: (Category) -> Unit, onCart: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Home", style = MaterialTheme.typography.headlineMedium)
            TextButton(
                onClick = onCart,
                modifier = Modifier.semantics { contentDescription = "home_cart_button" }
            ) { Text("Cart ($cartCount)") }
        }
        Text("Browse categories", style = MaterialTheme.typography.titleMedium)
        Category.entries.forEach { cat ->
            TextButton(
                onClick = { onCategory(cat) },
                modifier = Modifier.semantics { contentDescription = "category_${cat.name.lowercase()}" }
            ) { Text(cat.label) }
        }
        if (message.isNotBlank()) Text(message, Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ProductScreen(
    category: Category, cartCount: Int,
    onBack: () -> Unit, onAdd: (Product) -> Unit, onCart: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "products_back_button" }) {
                Text("Back")
            }
            Text(category.label, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onCart, modifier = Modifier.semantics { contentDescription = "products_cart_button" }) {
                Text("Cart")
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(catalog(category)) { product ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Text("$${"%.2f".format(product.price)}")
                    }
                    OutlinedButton(
                        onClick = { onAdd(product) },
                        modifier = Modifier.semantics { contentDescription = "add_to_cart_${product.id}" }
                    ) { Text("Add to Cart") }
                }
            }
        }
        Button(
            onClick = onCart,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "products_go_to_cart" }
        ) { Text("Go to Cart ($cartCount)") }
    }
}

@Composable
private fun CartScreen(cart: List<Product>, onCheckout: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Cart", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { contentDescription = "cart_heading" })
        if (cart.isEmpty()) {
            Text("No items yet", modifier = Modifier.semantics { contentDescription = "cart_empty_message" })
        } else {
            cart.forEach { Text("${it.name} — $${"%.2f".format(it.price)}") }
            val total = cart.sumOf { it.price }
            Text("Total: $${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { contentDescription = "cart_total_label" })
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCheckout, enabled = cart.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "checkout_button" }
        ) { Text("Checkout") }
        TextButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "cart_back_home" }) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun SuccessScreen(onHome: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Order Placed Successfully!", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { contentDescription = "order_success_message" })
        Spacer(Modifier.height(16.dp))
        Button(onClick = onHome, modifier = Modifier.semantics { contentDescription = "success_back_home" }) {
            Text("Back to Home")
        }
    }
}

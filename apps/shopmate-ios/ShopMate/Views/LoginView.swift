import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Sign In")
                .font(.title.bold())
                .accessibilityIdentifier("login_heading")

            TextField("Email", text: $appState.email)
                .textInputAutocapitalization(.never)
                .keyboardType(.emailAddress)
                .textFieldStyle(.roundedBorder)
                .accessibilityIdentifier("login_email_field")

            SecureField("Password", text: $appState.password)
                .textFieldStyle(.roundedBorder)
                .accessibilityIdentifier("login_password_field")

            if !appState.lastMessage.isEmpty {
                Text(appState.lastMessage)
                    .font(.footnote)
                    .foregroundStyle(.orange)
            }

            Button("Sign In") {
                appState.signIn()
            }
            .buttonStyle(.borderedProminent)
            .frame(maxWidth: .infinity)
            .accessibilityIdentifier("login_submit_btn")

            Spacer()
        }
        .padding()
    }
}

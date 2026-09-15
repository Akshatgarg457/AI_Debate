document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const messageEl = document.getElementById("message");

    if (messageEl) {
        // Login failed
        if (params.get("error") === "true") {
            messageEl.innerText = "Invalid username or password";
            localStorage.removeItem("username");
        }

        // Signup successful
        if (params.get("success") === "true") {
            messageEl.innerText = "Signup successful! Please login.";
            messageEl.style.color = "#4ade80";
        }

        // Password reset successful
        if (params.get("reset") === "true") {
            messageEl.innerText = "Password reset successful!";
            messageEl.style.color = "#4ade80";
        }
    }
});

function saveUsername() {
    const usernameInput = document.getElementById("loginUsername");
    if (!usernameInput) return;

    const username = usernameInput.value.trim();

    if (username) {
        /*
         * Remove the old username first.
         * This prevents an old account such as "Kavita" from remaining in localStorage.
         */
        localStorage.removeItem("username");

        /*
         * Save the username currently being used to log in.
         */
        localStorage.setItem("username", username);
    }
}

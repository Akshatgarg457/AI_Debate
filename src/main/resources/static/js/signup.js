document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const messageEl = document.getElementById("message");

    if (messageEl) {
        if (params.get("error") === "password") {
            messageEl.innerText = "Passwords do not match";
        } else if (params.get("error") === "username") {
            messageEl.innerText = "Username already exists";
        } else if (params.get("error") === "email") {
            messageEl.innerText = "Email already exists";
        }
    }
});

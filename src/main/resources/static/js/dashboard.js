document.addEventListener("DOMContentLoaded", function () {

    const username = localStorage.getItem("username");

    if (!username) {
        window.location.replace("/login");
        return;
    }

    const usernameDisplay = document.getElementById("usernameDisplay");

    if (usernameDisplay) {
        usernameDisplay.textContent = "👋 " + username;
    }

    window.history.replaceState(null, "", window.location.href);
});


function startAIDebate() {
    window.location.href = "/topic?mode=ai";
}


function startHumanDebate() {
    window.location.href = "/create-room";
}


function openAIHistory() {
    window.location.href = "/ai-history";
}


function openHumanHistory() {
    window.location.href = "/human-history";
}


function logout() {

    localStorage.removeItem("username");

    window.location.replace("/logout");
}
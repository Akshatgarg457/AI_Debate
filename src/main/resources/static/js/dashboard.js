document.addEventListener("DOMContentLoaded", function () {

    const username = localStorage.getItem("username");

    if (!username) {
        window.location.href = "/login";
        return;
    }

    document.getElementById("username").textContent = username;
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

    window.location.href = "/login";
}
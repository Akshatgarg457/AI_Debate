document.addEventListener("DOMContentLoaded", function () {
    // GET DATA
    const topic = localStorage.getItem("lastTopic");
    const winner = localStorage.getItem("lastWinner");
    const reason = localStorage.getItem("lastReason");
    const userArg = localStorage.getItem("lastUserArg");
    const aiArg = localStorage.getItem("lastAiArg");

    // SET DATA
    const topicEl = document.getElementById("topic");
    const winnerEl = document.getElementById("winner");
    const reasonEl = document.getElementById("reason");
    const userArgEl = document.getElementById("userArg");
    const aiArgEl = document.getElementById("aiArg");

    if (topicEl) topicEl.innerText = "📌 Topic: " + (topic || "N/A");
    if (winnerEl) winnerEl.innerText = "🏆 Winner: " + (winner || "N/A");
    if (reasonEl) reasonEl.innerText = reason || "No reason available";
    if (userArgEl) userArgEl.innerText = userArg || "-";
    if (aiArgEl) aiArgEl.innerText = aiArg || "-";
});

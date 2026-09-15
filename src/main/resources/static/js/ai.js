function send() {
    const inputEl = document.getElementById("input");
    const input = inputEl.value.trim();
    if (!input) return;

    const chat = document.getElementById("chat");

    // User message
    const userDiv = document.createElement("div");
    userDiv.className = "user";
    const userBubble = document.createElement("div");
    userBubble.className = "bubble-user";
    userBubble.textContent = input;
    userDiv.appendChild(userBubble);
    chat.appendChild(userDiv);
    chat.scrollTop = chat.scrollHeight;

    inputEl.value = "";

    fetch("/api/ai/debate?message=" + encodeURIComponent(input), {
        method: "POST"
    })
    .then(res => res.text())
    .then(data => {
        const aiDiv = document.createElement("div");
        aiDiv.className = "ai";
        const aiBubble = document.createElement("div");
        aiBubble.className = "bubble-ai";
        aiBubble.textContent = data;
        aiDiv.appendChild(aiBubble);
        chat.appendChild(aiDiv);
        chat.scrollTop = chat.scrollHeight;
    })
    .catch(err => {
        console.error("Error communicating with AI:", err);
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const inputEl = document.getElementById("input");
    if (inputEl) {
        inputEl.addEventListener("keypress", function (e) {
            if (e.key === "Enter") {
                send();
            }
        });
    }
});

document.addEventListener("DOMContentLoaded", function () {
    // Username handling (same as dashboard)
    let username = localStorage.getItem("username");
    if (username) {
        if (username.includes("@")) username = username.split("@")[0];
        username = username.charAt(0).toUpperCase() + username.slice(1);
    } else {
        username = "User";
    }

    const usernameDisplay = document.getElementById("usernameDisplay");
    if (usernameDisplay) {
        usernameDisplay.innerText = "👋 " + username;
    }

    loadHistory(username);
});

function loadHistory(username) {
    fetch(`/history?username=${encodeURIComponent(username)}`)
        .then(r => r.json())
        .then(historyData => {
            const totalCards = Math.ceil((historyData.length + 3) / 3) * 3;
            const emptyCount = totalCards - historyData.length;
            const allData = historyData.concat(Array(emptyCount).fill(null));
            const grid = document.getElementById('history-grid');
            if (!grid) return;

            grid.innerHTML = '';
            allData.forEach(d => {
                const card = document.createElement('div');
                card.className = d ? 'history-card' : 'history-card empty';
                if (d) {
                    const inner = `
                        <h3 class="card-title">${escapeHtml(d.topic || "Untitled")}</h3>
                        <p><strong>Winner:</strong> ${escapeHtml(d.winner || "Not decided")}</p>
                        ${d.result ? `<p><strong>Result:</strong> ${escapeHtml(d.result)}</p>` : ''}
                        ${d.score ? `<p><strong>Score:</strong> ${escapeHtml(d.score)}</p>` : ''}
                        <p class="date">${new Date(d.createdAt || Date.now()).toLocaleString()}</p>
                        <a href="/debate?uid=${encodeURIComponent(d.id)}" class="view-btn">VIEW DEBATE →</a>
                    `;
                    card.innerHTML = inner;
                } else {
                    card.innerHTML = `<p class="empty-text">EMPTY SLOT</p><p class="empty-sub">Available for your next debate</p>`;
                }
                grid.appendChild(card);
            });
        })
        .catch(err => {
            console.error("Error loading history:", err);
        });
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}

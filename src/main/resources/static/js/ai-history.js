const username = localStorage.getItem("username") || "guest";

document.addEventListener("DOMContentLoaded", function () {

    setUsername();

    loadHistory();

});


function setUsername() {

    let displayName = username;

    if (displayName.includes("@")) {
        displayName = displayName.split("@")[0];
    }

    displayName =
        displayName.charAt(0).toUpperCase() +
        displayName.slice(1);

    document.getElementById("usernameDisplay").innerText =
        "👋 " + displayName;
}


async function loadHistory() {

    try {

        const response = await fetch(
            `/api/history/ai?username=${encodeURIComponent(username)}`
        );

        if (!response.ok) {
            throw new Error("History loading failed");
        }

        const debates = await response.json();

        document.getElementById("loading")
            .classList.add("hidden");

        createCards(debates);

    } catch (error) {

        document.getElementById("loading")
            .classList.add("hidden");

        document.getElementById("error")
            .classList.remove("hidden");
    }
}


function createCards(debates) {

    const grid =
        document.getElementById("history-grid");

    grid.innerHTML = "";

    const minimumSlots = 6;
    const emptySlots = 2;

    const totalSlots =
        Math.max(
            minimumSlots,
            debates.length + emptySlots
        );

    for (let i = 0; i < totalSlots; i++) {

        if (i < debates.length) {

            createDebateCard(
                debates[i],
                grid
            );

        } else {

            createEmptyCard(grid);

        }

    }
}


function createDebateCard(debate, grid) {

    const card =
        document.createElement("div");

    card.className = "history-card";

    const score =
        debate.userScore !== null &&
        debate.userScore !== undefined
            ? `${debate.userScore} - ${debate.aiScore}`
            : "Not scored";

    card.innerHTML = `

        <span class="card-badge">
            AI DEBATE
        </span>

        <h3 class="card-title">
            ${escapeHtml(
                debate.topic || "Untitled Debate"
            )}
        </h3>

        <div class="card-info">

            <div>
                <strong>Stance:</strong>
                ${escapeHtml(
                    debate.userStance || "Unknown"
                )}
            </div>

            <div>
                <strong>Winner:</strong>
                ${escapeHtml(
                    debate.winner || "Not decided"
                )}
            </div>

            <div>
                <strong>Score:</strong>
                ${escapeHtml(score)}
            </div>

        </div>

        <div class="card-date">
            ${formatDate(debate.createdAt)}
        </div>

        <button
            class="view-btn"
            onclick="showDetails('${debate.id}')">

            VIEW DEBATE →

        </button>
    `;

    grid.appendChild(card);
}


function createEmptyCard(grid) {

    const card =
        document.createElement("div");

    card.className =
        "history-card empty";

    card.innerHTML = `

        <div class="empty-plus">
            +
        </div>

        <div class="empty-text">
            EMPTY SLOT
        </div>

        <div class="empty-sub">
            Available for your next debate
        </div>

    `;

    grid.appendChild(card);
}


async function showDetails(debateId) {

    const modal =
        document.getElementById("detailsModal");

    const details =
        document.getElementById("debateDetails");

    modal.classList.remove("hidden");

    details.innerHTML = `
        <div class="history-message">
            Loading debate...
        </div>
    `;

    try {

        const response = await fetch(
            `/api/history/ai/${encodeURIComponent(debateId)}?username=${encodeURIComponent(username)}`
        );

        if (!response.ok) {
            throw new Error("Unable to load debate");
        }

        const debate =
            await response.json();

        renderDetails(debate);

    } catch (error) {

        details.innerHTML = `
            <div class="history-message error">
                Unable to load debate details.
            </div>
        `;
    }
}


function renderDetails(debate) {

    const details =
        document.getElementById("debateDetails");

    const score =
        debate.userScore !== null &&
        debate.userScore !== undefined
            ? `${debate.userScore} - ${debate.aiScore}`
            : "Not scored";

    let transcript = "";

    if (
        debate.messages &&
        debate.messages.length > 0
    ) {

        transcript =
            debate.messages.map(message => {

                const isUser =
                    message.sender === "USER";

                return `

                    <div class="transcript-message
                        ${isUser
                            ? "transcript-user"
                            : "transcript-ai"}">

                        <div class="message-sender">
                            ${isUser
                                ? "You"
                                : "Gemini"}
                        </div>

                        <div class="message-text">
                            ${escapeHtml(
                                message.content || ""
                            )}
                        </div>

                        <div class="message-time">
                            ${formatDate(
                                message.timestamp
                            )}
                        </div>

                    </div>

                `;

            }).join("");

    } else {

        transcript = `
            <div class="history-message">
                No messages recorded.
            </div>
        `;
    }


    details.innerHTML = `

        <div class="details-grid">

            <div class="detail-box">
                <span>TOPIC</span>
                <strong>
                    ${escapeHtml(
                        debate.topic || "Unknown"
                    )}
                </strong>
            </div>

            <div class="detail-box">
                <span>YOUR STANCE</span>
                <strong>
                    ${escapeHtml(
                        debate.userStance || "Unknown"
                    )}
                </strong>
            </div>

            <div class="detail-box">
                <span>WINNER</span>
                <strong>
                    ${escapeHtml(
                        debate.winner || "Not decided"
                    )}
                </strong>
            </div>

            <div class="detail-box">
                <span>SCORE</span>
                <strong>
                    ${escapeHtml(score)}
                </strong>
            </div>

            <div class="detail-box">
                <span>STARTED</span>
                <strong>
                    ${formatDate(debate.startedAt)}
                </strong>
            </div>

            <div class="detail-box">
                <span>ENDED</span>
                <strong>
                    ${formatDate(debate.endedAt)}
                </strong>
            </div>

        </div>


        <div class="result-box">

            <h3>
                🏆 Debate Result
            </h3>

            <p>
                ${escapeHtml(
                    debate.result ||
                    "No result available"
                )}
            </p>

        </div>


        <h3 class="transcript-title">
            Debate Transcript
        </h3>

        ${transcript}


        <div class="readonly">
            🔒 This debate is read-only.
        </div>
    `;
}


function closeModal() {

    document
        .getElementById("detailsModal")
        .classList.add("hidden");
}


function formatDate(value) {

    if (!value) {
        return "N/A";
    }

    const date = new Date(value);

    if (isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        }
    );
}


function escapeHtml(value) {

    const div =
        document.createElement("div");

    div.textContent =
        value ?? "";

    return div.innerHTML;
}


window.addEventListener(
    "click",
    function (event) {

        const modal =
            document.getElementById(
                "detailsModal"
            );

        if (event.target === modal) {
            closeModal();
        }

    }
);
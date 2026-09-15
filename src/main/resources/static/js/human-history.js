const username =
    localStorage.getItem("username") || "guest";


document.addEventListener(
    "DOMContentLoaded",
    function () {

        setUsername();

        loadHistory();

    }
);


function setUsername() {

    let name = username;

    if (name.includes("@")) {
        name = name.split("@")[0];
    }

    name =
        name.charAt(0).toUpperCase() +
        name.slice(1);

    document.getElementById(
        "usernameDisplay"
    ).innerText = "👋 " + name;
}


async function loadHistory() {

    try {

        const response = await fetch(
            `/api/history/human?username=${encodeURIComponent(username)}`
        );

        if (!response.ok) {
            throw new Error();
        }

        const debates =
            await response.json();

        document.getElementById(
            "loading"
        ).classList.add("hidden");

        createCards(debates);

    } catch (error) {

        document.getElementById(
            "loading"
        ).classList.add("hidden");

        document.getElementById(
            "error"
        ).classList.remove("hidden");

    }

}


function createCards(debates) {

    const grid =
        document.getElementById(
            "history-grid"
        );

    grid.innerHTML = "";


    const minimumSlots = 6;

    const permanentEmptySlots = 2;


    const totalSlots =
        Math.max(
            minimumSlots,
            debates.length +
            permanentEmptySlots
        );


    for (
        let i = 0;
        i < totalSlots;
        i++
    ) {

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


function createDebateCard(
    debate,
    grid
) {

    const isPlayer1 =
        username === debate.player1;


    const opponent =
        isPlayer1
            ? debate.player2
            : debate.player1;


    const userStance =
        isPlayer1
            ? debate.player1Stance
            : debate.player2Stance;


    const card =
        document.createElement("div");


    card.className =
        "history-card";


    card.innerHTML = `

        <span class="card-badge">
            HUMAN DEBATE
        </span>


        <h3 class="card-title">

            ${escapeHtml(
                debate.topic ||
                "Untitled Debate"
            )}

        </h3>


        <div class="card-info">

            <div>

                <strong>
                    Opponent:
                </strong>

                ${escapeHtml(
                    opponent ||
                    "Unknown"
                )}

            </div>


            <div>

                <strong>
                    Your Stance:
                </strong>

                ${escapeHtml(
                    userStance ||
                    "Unknown"
                )}

            </div>


            <div>

                <strong>
                    Winner:
                </strong>

                ${escapeHtml(
                    debate.winner ||
                    "Not decided"
                )}

            </div>

        </div>


        <div class="card-date">

            ${formatDate(
                debate.createdAt
            )}

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


async function showDetails(
    debateId
) {

    const modal =
        document.getElementById(
            "detailsModal"
        );


    const details =
        document.getElementById(
            "debateDetails"
        );


    modal.classList.remove(
        "hidden"
    );


    details.innerHTML = `

        <div class="history-message">

            Loading debate...

        </div>

    `;


    try {

        const response =
            await fetch(
                `/api/history/human/${encodeURIComponent(debateId)}?username=${encodeURIComponent(username)}`
            );


        if (!response.ok) {
            throw new Error();
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
        document.getElementById(
            "debateDetails"
        );


    const isPlayer1 =
        username === debate.player1;


    const opponent =
        isPlayer1
            ? debate.player2
            : debate.player1;


    const userStance =
        isPlayer1
            ? debate.player1Stance
            : debate.player2Stance;


    let transcript = "";


    if (
        debate.messages &&
        debate.messages.length > 0
    ) {

        transcript =
            debate.messages
                .map(message => {

                    const sender =
                        message.sender ||
                        "Opponent";


                    const isUser =
                        sender === username ||
                        sender === "USER";


                    return `

                        <div class="transcript-message
                            ${isUser
                                ? "transcript-user"
                                : "transcript-ai"}">


                            <div class="message-sender">

                                ${escapeHtml(
                                    isUser
                                        ? "You"
                                        : sender
                                )}

                            </div>


                            <div class="message-text">

                                ${escapeHtml(
                                    message.content ||
                                    ""
                                )}

                            </div>


                            <div class="message-time">

                                ${formatDate(
                                    message.timestamp
                                )}

                            </div>


                        </div>

                    `;

                })
                .join("");

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

                <span>
                    TOPIC
                </span>

                <strong>

                    ${escapeHtml(
                        debate.topic ||
                        "Unknown"
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    OPPONENT
                </span>

                <strong>

                    ${escapeHtml(
                        opponent ||
                        "Unknown"
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    YOUR STANCE
                </span>

                <strong>

                    ${escapeHtml(
                        userStance ||
                        "Unknown"
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    WINNER
                </span>

                <strong>

                    ${escapeHtml(
                        debate.winner ||
                        "Not decided"
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    STATUS
                </span>

                <strong>

                    ${escapeHtml(
                        debate.status ||
                        "Unknown"
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    ROOM
                </span>

                <strong>

                    ${escapeHtml(
                        debate.roomId ||
                        "Unknown"
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    STARTED
                </span>

                <strong>

                    ${formatDate(
                        debate.startedAt
                    )}

                </strong>

            </div>


            <div class="detail-box">

                <span>
                    ENDED
                </span>

                <strong>

                    ${formatDate(
                        debate.endedAt
                    )}

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
        .getElementById(
            "detailsModal"
        )
        .classList.add(
            "hidden"
        );

}


function formatDate(value) {

    if (!value) {
        return "N/A";
    }


    const date =
        new Date(value);


    if (
        isNaN(
            date.getTime()
        )
    ) {

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
        document.createElement(
            "div"
        );

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


        if (
            event.target === modal
        ) {

            closeModal();

        }

    }
);
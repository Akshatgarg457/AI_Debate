let currentCreatedRoomId = "";
let createdStance = "";
let selectedCreateTopic = "";
let foundRoomId = "";

// ===========================
// AUTOFILL USERNAME & EVENT LISTENERS
// ===========================
document.addEventListener("DOMContentLoaded", function () {
    let storedUser = localStorage.getItem("username") || "";
    if (storedUser.includes("@")) {
        storedUser = storedUser.split("@")[0];
    }
    if (storedUser) {
        storedUser = storedUser.charAt(0).toUpperCase() + storedUser.slice(1);
    }

    const createUsername = document.getElementById("createUsername");
    const joinUsername = document.getElementById("joinUsername");
    if (createUsername) createUsername.value = storedUser;
    if (joinUsername) joinUsername.value = storedUser;

    // Clear card selection when typing custom topic
    const createCustomTopic = document.getElementById("createCustomTopic");
    if (createCustomTopic) {
        createCustomTopic.addEventListener("input", function () {
            if (this.value.trim() !== "") {
                document.querySelectorAll("#sectionCreate .topic-card").forEach(card => {
                    card.classList.remove("active");
                });
                selectedCreateTopic = "";
            }
        });
    }
});

// ===========================
// TAB SWITCHING
// ===========================
function switchTab(tab) {
    const tabCreate = document.getElementById("tabCreate");
    const tabJoin = document.getElementById("tabJoin");
    const sectionCreate = document.getElementById("sectionCreate");
    const sectionJoin = document.getElementById("sectionJoin");

    if (tab === 'create') {
        tabCreate.className = "tab-btn active";
        tabJoin.className = "tab-btn inactive";
        sectionCreate.style.display = "flex";
        sectionJoin.style.display = "none";
    } else {
        tabCreate.className = "tab-btn inactive";
        tabJoin.className = "tab-btn active";
        sectionCreate.style.display = "none";
        sectionJoin.style.display = "flex";
    }
}

// ===========================
// CREATE ROOM - TOPIC SELECTION
// ===========================
function selectCreateTopic(element) {
    document.querySelectorAll("#sectionCreate .topic-card").forEach(card => {
        card.classList.remove("active");
    });
    element.classList.add("active");
    selectedCreateTopic = element.innerText;

    // Clear custom input when a card is selected
    const customTopicInput = document.getElementById("createCustomTopic");
    if (customTopicInput) customTopicInput.value = "";
}

// ===========================
// CREATE ROOM
// ===========================
async function handleCreateRoom() {
    const stance = "unknown";
    const username = document.getElementById("createUsername").value.trim();
    const customTopic = document.getElementById("createCustomTopic").value.trim();
    const topic = customTopic || selectedCreateTopic;

    if (!username) {
        alert("Please enter your username!");
        return;
    }
    if (!topic) {
        alert("Please select or enter a debate topic!");
        return;
    }

    localStorage.setItem("username", username);
    createdStance = stance;

    try {
        const res = await fetch("/api/rooms/create", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, topic })
        });
        const data = await res.json();

        if (data.success) {
            currentCreatedRoomId = data.roomId;
            document.getElementById("createdRoomId").innerText = data.roomId;
            document.getElementById("createdTopicDisplay").innerText = topic;
            document.getElementById("roomCreatedCard").style.display = "flex";
        } else {
            alert("Failed to create room.");
        }
    } catch (err) {
        console.error(err);
        alert("Error communicating with server.");
    }
}

function copyRoomId() {
    if (!currentCreatedRoomId) return;
    navigator.clipboard.writeText(currentCreatedRoomId).then(() => {
        alert("Room ID copied to clipboard!");
    });
}

function enterCreatedRoom() {
    if (!currentCreatedRoomId) return;
    const username = document.getElementById("createUsername").value.trim() || "Debater";
    window.location.href = `/human-debate?roomId=${encodeURIComponent(currentCreatedRoomId)}&username=${encodeURIComponent(username)}&stance=${encodeURIComponent(createdStance)}`;
}

// ===========================
// JOIN ROOM - LOOKUP
// ===========================
async function lookupRoom() {
    const username = document.getElementById("joinUsername").value.trim();
    const roomId = document.getElementById("joinRoomId").value.trim();

    if (!username) {
        alert("Please enter your username!");
        return;
    }
    if (!roomId) {
        alert("Please enter the Room ID!");
        return;
    }

    localStorage.setItem("username", username);

    try {
        const res = await fetch(`/api/rooms/${encodeURIComponent(roomId)}`);
        if (res.status === 404) {
            alert("Room not found! Please check the Room ID.");
            document.getElementById("joinRoomInfo").style.display = "none";
            return;
        }
        const data = await res.json();

        if (data.exists) {
            foundRoomId = roomId;
            document.getElementById("joinTopicDisplay").innerText = data.topic || "General Debate";
            document.getElementById("joinCreatorDisplay").innerText = data.creator || "Unknown";
            document.getElementById("joinPlayerCount").innerText = `${data.playerCount || 1}/2`;
            document.getElementById("joinRoomInfo").style.display = "block";
        } else {
            alert("Room does not exist.");
            document.getElementById("joinRoomInfo").style.display = "none";
        }
    } catch (err) {
        console.error(err);
        alert("Failed to look up room.");
    }
}

// ===========================
// JOIN ROOM - WITH STANCE
// ===========================
function handleJoinRoom(stance) {
    const username = document.getElementById("joinUsername").value.trim() || "Debater";
    if (!foundRoomId) {
        alert("No room selected!");
        return;
    }
    window.location.href = `/human-debate?roomId=${encodeURIComponent(foundRoomId)}&username=${encodeURIComponent(username)}&stance=${encodeURIComponent(stance)}`;
}

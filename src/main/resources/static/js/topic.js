let selectedTopic = "";

// SELECT FROM CARDS
function selectTopic(element) {
    document.querySelectorAll(".topic-card").forEach(card => {
        card.classList.remove("active");
    });

    element.classList.add("active");
    selectedTopic = element.innerText;
}

// START DEBATE
function startDebate(stance) {
    const custom = document.getElementById("customTopic").value;

    if (custom.trim() !== "") {
        selectedTopic = custom;
    }

    if (selectedTopic === "") {
        alert("Please select or enter a topic!");
        return;
    }

    // GET MODE FROM URL
    const params = new URLSearchParams(window.location.search);
    const mode = params.get("mode") || "ai";

    window.location.href = `/debate?topic=${encodeURIComponent(selectedTopic)}&mode=${mode}&stance=${stance}`;
}

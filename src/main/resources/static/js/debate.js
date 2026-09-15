const params = new URLSearchParams(window.location.search);
let topic   = params.get("topic") || "General Debate";
let mode    = params.get("mode")  || "ai";
let stance  = params.get("stance") || "for";

let lastUserArg  = "";
let lastAiArg    = "";
let debateEnded  = false;
let voiceEnabled = true;

// ── Initialise UI ──
document.addEventListener("DOMContentLoaded", function () {
    const stanceLabel = stance === "for" ? "In Motion (For) 👍" : "Against the Motion 👎";
    const stanceColour = stance === "for" ? "#86efac" : "#f87171";

    const topicTitle = document.getElementById("topicTitle");
    const headerTopic = document.getElementById("headerTopic");
    const headerStance = document.getElementById("headerStance");

    if (topicTitle) topicTitle.innerText  = `Topic: ${topic}`;
    if (headerTopic) headerTopic.innerText = topic;
    if (headerStance) {
        headerStance.innerText = stanceLabel;
        headerStance.style.color = stanceColour;
    }

    if (mode === "human") {
        const modeIcon = document.getElementById("modeIcon");
        const modeTitle = document.getElementById("modeTitle");
        if (modeIcon) modeIcon.innerText = "👥";
        if (modeTitle) modeTitle.innerText = "Human Debate";
    }

    // ── Enter key ──
    const argumentInput = document.getElementById("argumentInput");
    if (argumentInput) {
        argumentInput.addEventListener("keypress", function (e) {
            if (e.key === "Enter") {
                e.preventDefault();
                sendArgument();
            }
        });
    }
});

// ── Send Argument ──
function sendArgument() {
    if (debateEnded) {
        alert("Debate already ended!");
        return;
    }
    const input = document.getElementById("argumentInput");
    const userText = input.value.trim();
    if (!userText) return;

    lastUserArg = userText;
    appendBubble("You: " + userText, "bubble-user");
    input.value = "";

    if (mode === "human") {
        appendBubble("Opponent: " + userText, "bubble-opponent");
        return;
    }

    // AI typing indicator
    const loadingId = "loading-" + Date.now();
    const chatBox = document.getElementById("chatBox");
    const loader  = document.createElement("div");
    loader.id = loadingId;
    loader.className = "loading-indicator";
    loader.innerHTML = `<div class="dot"></div><div class="dot"></div><div class="dot"></div>`;
    chatBox.appendChild(loader);
    chatBox.scrollTop = chatBox.scrollHeight;

    fetch("/debate", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({argument: userText, topic: topic, stance: stance})
    })
    .then(res => res.text())
    .then(data => {
        document.getElementById(loadingId)?.remove();
        lastAiArg = data;
        appendBubble("🤖 AI: " + data, "bubble-opponent");
        speakText(data);
    })
    .catch(() => {
        document.getElementById(loadingId)?.remove();
        appendBubble("🤖 AI: Error communicating with server.", "bubble-opponent");
    });
}

function appendBubble(text, className) {
    const chatBox = document.getElementById("chatBox");
    const bubble  = document.createElement("div");
    bubble.className = "chat-bubble " + className;
    bubble.innerText = text;
    chatBox.appendChild(bubble);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// ── End Debate ──
function endDebate() {
    if (debateEnded) {
        alert("Debate already ended!");
        return;
    }
    if (!lastUserArg || !lastAiArg) {
        alert("Complete at least one round first!");
        return;
    }
    debateEnded = true;

    let winner, reason;
    if (lastUserArg.length > lastAiArg.length) {
        winner = "You 🥇";
        reason = "Your argument was longer and more detailed.";
    } else {
        winner = "AI 🤖";
        reason = "AI argument was longer and more detailed.";
    }

    // Update header status
    const statusEl = document.getElementById("headerStatus");
    if (statusEl) {
        statusEl.innerText = "Completed";
        statusEl.className = "status-completed";
    }

    // Disable inputs
    const argInput = document.getElementById("argumentInput");
    const sendBtn = document.getElementById("sendBtn");
    const micBtn = document.getElementById("micBtn");
    const endBtn = document.getElementById("endBtn");

    if (argInput) argInput.disabled = true;
    if (sendBtn) sendBtn.disabled = true;
    if (micBtn) micBtn.disabled  = true;
    if (endBtn) endBtn.disabled  = true;

    document.getElementById("resultWinner").innerText = `🏆 Winner: ${winner}`;
    document.getElementById("resultReason").innerText = reason;
    document.getElementById("resultModal").style.display = "flex";

    localStorage.setItem("lastWinner", winner);
    localStorage.setItem("lastReason", reason);
    localStorage.setItem("lastTopic",  topic);

    saveDebate();
}

function saveDebate() {
    const user = localStorage.getItem("username") || "Guest";
    fetch("/saveDebate", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: `username=${encodeURIComponent(user)}&topic=${encodeURIComponent(topic)}&userArgument=${encodeURIComponent(lastUserArg)}&aiArgument=${encodeURIComponent(lastAiArg)}`
    });
}

// ── Voice TTS ──
function toggleVoice() {
    voiceEnabled = !voiceEnabled;
    const btn = document.getElementById("voiceToggleBtn");
    if (btn) {
        btn.innerText = voiceEnabled ? "🔊" : "🔈";
        btn.title     = voiceEnabled ? "AI Voice On" : "AI Voice Off";
        btn.classList.toggle("off", !voiceEnabled);
    }
    if (!voiceEnabled) window.speechSynthesis.cancel();
}

function speakText(text) {
    if (!voiceEnabled) return;
    const utt = new SpeechSynthesisUtterance(text.replace(/[*_]/g, ""));
    utt.lang = "en-US";
    utt.rate = 1.0;
    window.speechSynthesis.speak(utt);
}

// ── Speech Recognition ──
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
let recognition;
if (SpeechRecognition) {
    recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.lang = "en-US";
    recognition.onstart = () => {
        const btn = document.getElementById("micBtn");
        if (btn) {
            btn.classList.add("recording");
            btn.innerText = "🔴 Recording";
        }
    };
    recognition.onresult = e => {
        const t = e.results[0][0].transcript;
        const inp = document.getElementById("argumentInput");
        if (inp) {
            inp.value += (inp.value ? " " : "") + t;
        }
    };
    recognition.onerror = () => {
        const btn = document.getElementById("micBtn");
        if (btn) {
            btn.classList.remove("recording");
            btn.innerText = "🎤 Speak";
        }
    };
    recognition.onend = () => {
        const btn = document.getElementById("micBtn");
        if (btn) {
            btn.classList.remove("recording");
            btn.innerText = "🎤 Speak";
        }
    };
}

function startVoiceRecognition() {
    if (recognition) {
        recognition.start();
    } else {
        alert("Speech Recognition not supported. Use Chrome or Edge.");
    }
}

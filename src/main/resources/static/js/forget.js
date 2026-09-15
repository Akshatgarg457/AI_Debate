const questions = {
    anime: "Your School Name",
    superhero: "Your Favorite Superhero",
    cricketer: "Your Favorite Cricketer",
    lucky: "Your Lucky Number",
    nickname: "Your Childhood Nickname"
};

let currentUser = "";
let currentQuestion = "";

// STEP 1
function searchUser() {
    const user = document.getElementById("username").value.trim();

    if (user === "") {
        alert("Enter username");
        return;
    }

    fetch("/get-question?username=" + encodeURIComponent(user))
        .then(res => res.text())
        .then(data => {
            const key = data.trim();

            if (key === "NOT_FOUND") {
                alert("User not found");
                return;
            }

            currentUser = user;
            currentQuestion = key;

            document.getElementById("question").style.display = "block";
            document.getElementById("answer").style.display = "block";
            document.getElementById("verifyBtn").style.display = "block";

            document.getElementById("question").value = questions[key] || key;
        })
        .catch(err => {
            console.error("Error searching user:", err);
            alert("Error communicating with server.");
        });
}

// STEP 2
function verifyAnswer() {
    const answer = document.getElementById("answer").value.trim();

    if (answer === "") {
        alert("Enter answer");
        return;
    }

    document.getElementById("resetForm").style.display = "block";

    document.getElementById("hiddenUsername").value = currentUser;
    document.getElementById("hiddenQ").value = currentQuestion;
    document.getElementById("hiddenAnswer").value = answer;
}

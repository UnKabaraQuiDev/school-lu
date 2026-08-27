function apiUrl(path) {
    if (window.location.hostname === "school-lu.kbra.lu") {
        return `https://api.school-lu.kbra.lu/${path}`;
    }

    if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1") {
        return `http://localhost:8080/${path}`;
    }

    return `https://api.school-lu.kbra.lu/${path}`;
}

let csrf;

async function initCsrf() {
    const response = await fetch(apiUrl("csrf"), {
        method: "GET",
        credentials: "include"
    });

    if (!response.ok) {
        throw new Error("Failed to obtain CSRF token");
    }

    csrf = await response.json();
    console.log("got", csrf)
}

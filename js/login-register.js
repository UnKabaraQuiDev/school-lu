async function login(username, password) {
    return await fetch(
        apiUrl("login"),
        {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                [csrf.headerName]: csrf.token
            },
            body: new URLSearchParams({
                username: username,
                password: password
            })
        }
    );
}

async function register(username, email, password, confirmPassword) {
    return await fetch(
        apiUrl("register"),
        {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password,
                confirmPassword: confirmPassword
            })
        }
    );
}
document.addEventListener("DOMContentLoaded", function () {
    initCsrf();

    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");

    if (loginForm) {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const form = event.currentTarget;

            const response = await login(
                form.username.value,
                form.password.value
            );

            if (response.ok) {
                window.location.href = "/";
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const form = event.currentTarget;

            const response = await register(
                form.username.value,
                form.email.value,
                form.password.value,
                form.confirmPassword.value
            );

            if (response.ok) {
                window.location.href = "/";
            }
        });
    }
});
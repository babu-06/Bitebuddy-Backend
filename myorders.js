const API_BASE_URL = "http://localhost:8080/api/orders";

function getLoggedInUsername() {
    const possibleKeys = ["username", "user", "loggedInUser", "loggedInUsername", "currentUser"];

    for (const key of possibleKeys) {
        const value = localStorage.getItem(key);
        if (value && value.trim()) {
            return value.trim();
        }
    }

    return "";
}

function setStatus(message) {
    document.getElementById("statusMessage").textContent = message;
}

function renderOrders(orders) {
    const container = document.getElementById("ordersContainer");
    container.innerHTML = "";

    if (!orders.length) {
        container.innerHTML = '<div class="empty">No orders found for this user.</div>';
        return;
    }

    orders.forEach((order) => {
        const card = document.createElement("article");
        card.className = "order-card";

        const orderedAt = order.orderedAt
            ? new Date(order.orderedAt).toLocaleString()
            : "Not available";

        card.innerHTML = `
            <h2 class="order-title">${order.itemName}</h2>
            <p class="order-row">Quantity: ${order.quantity}</p>
            <p class="order-row">Price: Rs. ${order.price}</p>
            <p class="order-row">Ordered At: ${orderedAt}</p>
            <p class="order-row">Username: ${order.username}</p>
        `;

        container.appendChild(card);
    });
}

async function loadOrders() {
    const username = getLoggedInUsername();
    const userBadge = document.getElementById("loggedInUser");

    if (!username) {
        userBadge.textContent = "User not signed in";
        setStatus("No logged-in username found in localStorage.");
        renderOrders([]);
        return;
    }

    userBadge.textContent = `Logged in as: ${username}`;
    setStatus("Loading your orders...");

    try {
        const response = await fetch(`${API_BASE_URL}?username=${encodeURIComponent(username)}`);

        if (!response.ok) {
            throw new Error("Failed to fetch orders");
        }

        const orders = await response.json();
        renderOrders(orders);
        setStatus(`Showing ${orders.length} order(s) for ${username}.`);
    } catch (error) {
        setStatus("Could not load orders. Make sure the Spring Boot server is running.");
        renderOrders([]);
        console.error(error);
    }
}

document.getElementById("refreshBtn").addEventListener("click", loadOrders);
window.addEventListener("load", loadOrders);

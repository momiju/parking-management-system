async function loadHeader() {
    const container = document.getElementById("header-container");

    if (!container) {
        return;
    }

    try {
        const response = await fetch("/header.html");

        if (!response.ok) {
            console.error("헤더를 불러오지 못했습니다.");
            return;
        }

        const headerHtml = await response.text();
        container.innerHTML = headerHtml;

        const logoutBtn = document.getElementById("logoutBtn");

        if (logoutBtn) {
            logoutBtn.onclick = () => {
                localStorage.removeItem("token");
                alert("로그아웃 되었습니다.");
                location.href = "/login.html";
            };
        }
    } catch (error) {
        console.error("헤더 로딩 중 오류가 발생했습니다.", error);
    }
}

function connectWarningStream() {

    const token =
        localStorage.getItem("token");

    if (!token) {
        return;
    }

    if (
        "Notification" in window &&
        Notification.permission === "default"
    ) {
        Notification.requestPermission();
    }

    const eventSource =
        new EventSource("/api/warning/stream");

    // 미등록 차량 입차 알림
    eventSource.addEventListener(
        "warning",
        function (event) {

            if (
                "Notification" in window &&
                Notification.permission === "granted"
            ) {

                new Notification(
                    "미등록 차량 감지",
                    {
                        body: event.data
                    }
                );
            }
        }
    );

    // 미등록 차량 위치 확인 알림
    eventSource.addEventListener(
        "location",
        function (event) {

            if (
                "Notification" in window &&
                Notification.permission === "granted"
            ) {

                new Notification(
                    "미등록 차량 위치 확인",
                    {
                        body: event.data
                    }
                );
            }
        }
    );

    eventSource.onerror =
        function () {
            console.error(
                "실시간 경고 서버 연결에 문제가 발생했습니다."
            );
        };
}

document.addEventListener("DOMContentLoaded", () => {
    loadHeader();
    connectWarningStream();
});
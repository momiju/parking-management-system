async function loadHeader() {

    const container =
        document.getElementById("header-container");


    if (!container) {
        return;
    }


    try {

        const response =
            await fetch("/header.html");


        if (!response.ok) {

            console.error("헤더를 불러오지 못했습니다.");

            return;
        }


        const headerHtml =
            await response.text();


        container.innerHTML =
            headerHtml;


        const logoutBtn =
            document.getElementById("logoutBtn");


        if (logoutBtn) {

            logoutBtn.onclick = () => {

                localStorage.removeItem("token");

                alert("로그아웃 되었습니다.");

                location.href =
                    "/login.html";
            };
        }


    } catch (error) {

        console.error(
            "헤더 로딩 중 오류가 발생했습니다.",
            error
        );
    }
}


document.addEventListener(
    "DOMContentLoaded",
    loadHeader
);
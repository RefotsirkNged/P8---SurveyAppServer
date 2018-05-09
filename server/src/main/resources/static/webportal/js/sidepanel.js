function sidepanel() {
    //var content = document.body.innerHTML;
    document.body.innerHTML = "<div class=\"sidenav\">\n" +
        "    <a class='sidepanela' href='../groupmanager/index.html' onclick='validateLogin()'>Group Manager</a>\n" +
        "    <a class='sidepanela' href='../adduser/index.html' onclick='validateLogin()'>Create User</a>\n" +
        "    <a class='sidepanela' href='../surveymanager/index.html' onclick='validateLogin()'>Create Survey</a>\n" +
        "    <a class='sidepanela' href='../login/index.html' onclick='logout()'>Log Out</a>\n" +
        "</div>";
        // "<div class=\"main\">\n" +
        // content + "\n" +
        // "</div>";

    var currentUrl = document.location.href;

    var panels = document.getElementsByClassName("sidepanela");

    for(var i = 0; i < panels.length; i++) {
        if (panels[i].href === currentUrl) {
            console.log("I am here: " + currentUrl);
            panels[i].style.textDecoration = "underline overline";
        }
    }
}

function logout(){
    document.cookie = "token=;" + Date.now();
}

function validateLogin() {
    function groupManager(){
        if(!isLoggedIn()) {
            logout();
            document.location.href = "../login/index.html";
        }
    }
}

window.onload = sidepanel();
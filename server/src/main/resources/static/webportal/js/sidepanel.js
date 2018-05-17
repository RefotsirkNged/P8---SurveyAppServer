function sidepanel() {
    //var content = document.body.innerHTML;
    document.body.innerHTML = "<div class=\"sidenav\">\n" +
        "    <img src='../image/logo.png' style='width:100%;' /> \n" +
        "    <a class='sidepanela' href='../groupmanager/index.html' onclick='validateLogin()'>Gruppe manager</a>\n" +
        "    <a class='sidepanela' href='../adduser/index.html' onclick='validateLogin()'>Opret bruger</a>\n" +
        "    <a class='sidepanela' href='../surveymanager/index.html' onclick='surveyButtonClick()'>Opret spørgskema</a>\n" +
        "    <a class='sidepanela' href='../QueryPage/index.html' onclick='validateLogin()'>Exporter data</a> \n " +
        "    <a class='sidepanela' href='../login/index.html' onclick='logout()'>Log ud</a>\n" +
        "</div>";
        // "<div class=\"main\">\n" +
        // content + "\n" +
        // "</div>";

    var currentUrl = document.location.href;

    var panels = document.getElementsByClassName("sidepanela");

    for(var i = 0; i < panels.length; i++) {
        if (panels[i].href === currentUrl) {
            console.log("I am here: " + currentUrl);
            panels[i].style.color = "#02d2a6";
        }
    }
}

function logout(){
    document.cookie = "token=;" + Date.now();
}

function validateLogin() {
    function groupManager(){
        if(!isLoggedIn()) {
            console.log("Logging out");
            logout();
            document.location.href = "../login/index.html";
        }
    }
}

function surveyButtonClick(){
    validateLogin();
    sessionStorage.removeItem("surveyid");
}

window.onload = sidepanel();
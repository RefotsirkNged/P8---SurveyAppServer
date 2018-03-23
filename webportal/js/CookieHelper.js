function getCookie(cname) {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  console.log(document.cookie);
  var ca = decodedCookie.split(';');
  for(var i = 0; i <ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function setCookie(cname, cvalue, exdays) {
  var d = new Date();
  d.setTime(d.getTime() + (exdays*24*60*60*1000));
  var expires = "expires="+ d.toUTCString();
  document.cookie = cname + "=" + cvalue + "; " + expires + "; path=/;";
  //console.log(cname + "=" + cvalue + ";" + expires + ";path=/;");
}

function isLoggedIn()
{
  var usertoken = sessionStorage.getItem('token');
  return usertoken !== null && usertoken !== "" && usertoken !== "null";
}

function login() {
    var email = document.getElementById('loginemail').value;
    var password = document.getElementById('loginpassword').value;

    $.ajax({
        url: 'http://localhost:8081/api/researcher/login',
        type: 'POST',
        beforeSend: function (request) {
            request.setRequestHeader("email", email);
            request.setRequestHeader("password", password);
        },
        //data: {email: email, password: password},
        success: function (response) {
            if(response.hasOwnProperty("error")){
                modal.style.display = "block";
                document.getElementById("loginfailedmessage").innerText = response.error;
            }
            else {
                sessionStorage.setItem('token', response.token);
                setCookie("token", response.token, 1);
                window.location.replace("../index.html");
            }
        },
        error: function (response) {
            modal.style.display = "block";

            document.getElementById("loginfailedmessage").innerText = "Something happened";
        }
    });
}

function logout(){
    sessionStorage.removeItem('token');
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    window.location.replace("../index.html");
}

function groupManager(){
    if(isLoggedIn()){
        window.location.replace("../groupmanager/index.html");
    }
    else {
        window.location.replace("../login/index.html");
    }
}
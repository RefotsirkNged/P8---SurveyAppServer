/**
 * Created by chrae on 14-03-2018.
 */

function gotoHome(){
    if(isLoggedIn()){
        window.location.replace("../home/index.html");
    }
    else {
        window.location.replace("../login/index.html");
    }
}
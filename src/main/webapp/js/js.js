/**
 * Created by guoluwei on 2016/12/31.
 */


function login() {
    $.getJSON("login",function (result) {
        if(result["success"]){
            window.location.href = "success";
        }else {
            $("#status").text(result["msg"]);
            setTimeout(function () {
                login();
            },200);
        }
    });
}

login();

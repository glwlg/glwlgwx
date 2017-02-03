<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>index</title>
</head>
<body>
<img src="images/${qrCodePath}" alt="" id="qrCode">
<h id="status"></h>
<span class="uuid" data-uuid="${uuid}" style="display: none"></span>
</body>
<script language="JavaScript" src="js/jquery.js"></script>
<script language="JavaScript">
    function login() {
        var uuid = $('.uuid').attr("data-uuid");
        $.ajax({
            type: "POST",
            dataType: "json",
            data: {
                uuid:uuid
            },
            url: "login",
            success: function (result) {
                $('.uuid').attr("data-uuid", result.dataObject);
                if(result.success){
                    $("#status").text(result.msg);
                }else {
                    $("#status").text(result.msg);
                    setTimeout(function () {
                        login();
                    },400);
                }
            }
        });
    }



    login();
</script>

</html>

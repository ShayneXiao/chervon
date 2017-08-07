<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <script type="text/javascript" src="/js/jquery-1.7.1.min.js" ></script>
    <script type="text/javascript" src="/js/jquery-form.js" ></script>
    <link rel="stylesheet" href="/css/reset.css" />
    <title>Reset Password</title>
</head>
<body id="content">
<script>
    function resetPassword() {
        var pwd = $("#password").val();
        if(pwd == "" || pwd == null) {
            alert("password cannot be empty !!!");
            $("#resetPwdForm").reset();
            return false;
        }
        var confirmpwd = $("#password_confirmation").val();
        if(confirmpwd == "" || confirmpwd == null) {
            alert("password_confirmation cannot be empty !!!");
            $("#resetPwdForm").reset();
            return false;
        }
        if(pwd != confirmpwd) {
            alert("password is not same with password_confirmation !!!");
            $("#resetPwdForm").reset();
            return false;
        }

        var formdata ={"data": {
                "type": $("#type").val(),
                "id": $("id").val(),
                "attributes": {
                    "password": pwd,
                    "password_confirmation": confirmpwd
                }
            }
        };

        console.log(formdata);
        $.ajax({
            url: "/api/v1/resets",
            headers: {
                "Content-Type": "application/vnd.api+json",
                "Accept": "application/vnd.api+json",
                "Authorization": $("#token").val()
            },
            type: "patch",
            dataType: "json",
            data: JSON.stringify(formdata),
            success: function(data){

                $("#content").innerHTML = data;
            },
            error: function(data){

                console.log(data.responseText);
                $("#content").html(data.responseText);
            }
        });
    }
</script>
<div id="header">
    <div id="logo">
        <img src="/img/logo.png"/>
    </div>
</div>
<div id="whitespace"></div>
<div>
    <div class="clearfix" id="customer">
        <div id="recover-password">
            <h1><span class="header-green">CUSTOMER</span> Reset Password</h1>
            <div class="six columns">
                <input type="hidden" id="token" name="token" value="${resets.Authorization}">
                <input type="hidden" id="id" name="id" value="${resets.id}">
                <input type="hidden" id="type" name="type" value="${resets.type}" />
                <form id="resetPwdForm" method="post" action="/api/v1/resets" accept-charset="UTF-8" novalidate="novalidate">
                    <div id="recover_email" class="clearfix large_form">
                        <label for="password" class="large"><strong>New Password</strong></label>
                        <input type="password" size="30" name="password" id="password" class="large required">
                    </div>
                    <div id="recover_email" class="clearfix large_form">
                        <label for="password"><strong>Confirmation Password</strong></label>
                        <input type="password" size="30" name="password_confirmation" id="password_confirmation" class="large required">
                    </div>
                    <p class="action_bottom">
                        <input class="btn action_button" type="button" value="Submit" onclick="return resetPassword()">
                    </p>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>

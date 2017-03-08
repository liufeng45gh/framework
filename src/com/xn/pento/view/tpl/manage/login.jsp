<%@page language="java" contentType="text/html" pageEncoding="utf-8"%>

<%@ include file="include/header.jsp" %>

<form id="login_form">
用户名: <input type="text" name="user_name" id="user_name"><br>
密码: <input type="password" name="password" id="password"><br>
<input type=button value="登录" id="loginBtn">
</form>

<script>
$('#loginBtn').click(function () {
    var params = {
        user_name : $('#user_name').val(),
        password : $('#password').val()
    };

    rest_post("/api/manage/user/login.json", params, function(data) {
        if (data.ok) {
            alert("登录成功！");
            window.location = "/manage"
        } else {
            alert("登录失败！");
        }
    });
});

</script>

<%@ include file="include/footer.jsp" %>
<%@page language="java" contentType="text/html" pageEncoding="utf-8"%>

<%@ include file="include/header.jsp" %>

<br><br>
<div id="user_list">
</div>

<script>
$(document).ready(function() {
    update_div_get('user_list', '/tpl/manage/user_list.ejs', "/api/manage/virtual_user/list.json");
});
</script>

<%@ include file="include/footer.jsp" %>
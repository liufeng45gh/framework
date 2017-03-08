<%@page language="java" contentType="text/html" pageEncoding="utf-8"%>

function rest(method, url, data, callback) {
    $.ajax({
        type: method,
        url: url,
        data: data
    }).always(function (data) {
        callback(data);
    })        
}

function rest_get(url, callback) {
    rest("GET", url, null, callback);
}

function rest_post(url, data, callback) {
    rest("POST", url, data, callback);
}

function update_div(div, tpl, method, url, data) {
    rest(method, url, data, function (data) {
        if (data.ok) {
            var html = new EJS({url: tpl}).render({data: data.data});
            $('#' + div).html(html);
        }
    });
}

function update_div_get(div, tpl, url) {
    update_div(div, tpl, "GET", url, null);
}

function update_div_post(div, tpl, url, data) {
    update_div(div, tpl, "POST", url, data);
}

function user_status_str(status) {
    return ['激活', '禁言', '禁用'][status];
}
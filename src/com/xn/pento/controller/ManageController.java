package com.xn.pento.controller;

import com.xn.pento.common.BaseController;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-12
 * Time: PM6:57
 * To change this template use File | Settings | File Templates.
 */
public class ManageController extends BaseController {
    @BeforeFilter
    private final static Map $checkManageAuth = map(except, list("login"));

    @At(path = "/manage/login.do", types = RestRequest.Method.GET)
    public void login() throws Exception {
        render("/tpl/manage/login.jsp", null);
    }

    @At(path = "/manage", types = RestRequest.Method.GET)
    public void welcome() throws Exception {
        render("/tpl/manage/user_manage.jsp", null);
    }

    @At(path = "/manage/user_manage.do", types = RestRequest.Method.GET)
    public void userManage() throws Exception {
        render("/tpl/manage/user_manage.jsp", null);
    }

}

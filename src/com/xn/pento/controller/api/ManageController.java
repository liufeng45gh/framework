package com.xn.pento.controller.api;

import com.xn.pento.common.Result;
import com.xn.pento.common.BaseController;
import com.xn.pento.model.ManageUser;
import com.xn.pento.model.User;
import com.xn.pento.service.ManageUserService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-13
 * Time: PM5:25
 * To change this template use File | Settings | File Templates.
 */
public class ManageController extends BaseController {
//    @BeforeFilter
//    private final static Map $checkManageAuth = map(except, list("login"));
//
//    @At(path = "/api/manage/user/login.json", types = RestRequest.Method.POST)
//    public void login() throws Exception {
//        ManageUser user = findService(ManageUserService.class).login(param("user_name"), param("password"));
//        if (user != null) {
//            request.getSession().setAttribute("manage_user", user);
//            render(Result.ok());
//        } else {
//            render(Result.fail("login failed"));
//        }
//    }
//
//    @At(path = "/api/manage/virtual_user/list.json", types = RestRequest.Method.GET)
//    public void userList() throws Exception {
//        ManageUser manageUser = getManageUser();
//
//        List<User> userList = manageUser.associate("users").fetch();
////        List<User> userList = User.findAll();
//        render(Result.ok(userList));
//    }
//
}

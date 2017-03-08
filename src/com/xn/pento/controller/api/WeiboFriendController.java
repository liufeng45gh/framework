package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-29
 * Time: AM11:14
 * To change this template use File | Settings | File Templates.
 */
public class WeiboFriendController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list());

    @At(path = "/api/weibo_friend/import", types = RestRequest.Method.POST)
    public void friendImport() throws Exception {

    }

}

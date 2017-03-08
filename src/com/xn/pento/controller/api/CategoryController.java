package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.service.CategoryService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-25
 * Time: PM3:10
 * To change this template use File | Settings | File Templates.
 */
public class CategoryController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list(""));

    /**
     * 查询所有类别信息
     *
     * @return 类别信息
     */
    @At(path = "/api/category/show.json", types = RestRequest.Method.GET)
    public void show() throws Exception {
        render(Result.ok(findService(CategoryService.class).categories()));
    }
}

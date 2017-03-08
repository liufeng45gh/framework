package com.xn.pento.service;

import com.xn.pento.cache.AppCache;
import com.xn.pento.cache.CacheProvider;
import com.xn.pento.common.BaseService;
import com.xn.pento.model.Category;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-25
 * Time: PM3:12
 * To change this template use File | Settings | File Templates.
 */
public class CategoryService extends BaseService {

    public List<Category> categories() throws Exception {
        return (List<Category>)AppCache.fetch("categories", new CacheProvider() {
            @Override
            public Class getDataClass() {
                return Category.class;
            }

            @Override
            public Object getData() {
                return Category.findAll();
            }
        });
    }

}

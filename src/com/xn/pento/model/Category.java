package com.xn.pento.model;

import com.xn.pento.common.BaseModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-17
 * Time: PM8:52
 * To change this template use File | Settings | File Templates.
 */
public class Category extends BaseModel {
    List<Board> boards(int offset, int limit) {
        return Board.where("category_id=:category").offset(offset).limit(limit).fetch();
    }
}

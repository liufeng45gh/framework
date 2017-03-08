package com.xn.pento.model;

import com.xn.pento.common.BaseModel;
import com.xn.pento.service.UserService;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-19
 * Time: PM7:08
 * To change this template use File | Settings | File Templates.
 */
public class Subscription extends BaseModel {
    public Board board() {
        return Board.find(attrAsInt("board_id"));
    }

    public User user() throws Exception {
        return findService(UserService.class).findById(attrAsInt("user_id"));
    }
}

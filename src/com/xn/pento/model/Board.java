package com.xn.pento.model;

import com.xn.pento.common.BaseModel;
import com.xn.pento.common.VisibleType;

import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-17
 * Time: PM8:52
 * To change this template use File | Settings | File Templates.
 */
public class Board extends BaseModel {
    public Category category() {
        return Category.find(attrAsInt("category_id"));
    }

    private void remove(VisibleType type) {
        attr("visible", type.ordinal());
        Pin.getJPAContext().jpql(Pin.class.getName()).update("set visible=? where visible=0 and board_id=?", new Object[] {
                type.ordinal(),
                id()}
        );
    }

    public void userRemove() {
        remove(VisibleType.userRemoved);
    }

    public void adminRemove() {
        remove(VisibleType.adminRemoved);
    }
}

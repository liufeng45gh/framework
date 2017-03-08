package com.xn.pento.model;

import com.xn.pento.common.BaseModel;
import com.xn.pento.common.VisibleType;
import com.xn.pento.service.PinService;
import net.csdn.common.exception.ValidateErrorException;

import java.util.Collections;
import java.util.List;

import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-17
 * Time: PM8:52
 * To change this template use File | Settings | File Templates.
 */
public class Pin extends BaseModel {
    private void remove(VisibleType type) {
        attr("visible", type.ordinal());
    }

    public void userRemove() {
        remove(VisibleType.userRemoved);
    }

    public void adminRemove() {
        remove(VisibleType.adminRemoved);
    }

    public Board board() {
        return Board.find(attrAsInt("board_id"));
    }

    public String content() throws Exception {
        return findService(PinService.class).findContentById(attrAsInt("content_id"));
    }
}

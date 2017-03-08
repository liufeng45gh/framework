package com.xn.pento.service;

import com.xn.pento.common.BaseService;
import com.xn.pento.model.ManageUser;
import net.csdn.common.exception.RecordNotFoundException;

import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-13
 * Time: PM5:35
 * To change this template use File | Settings | File Templates.
 */
public class ManageUserService extends BaseService {

    public ManageUser login(String userName, String password) throws Exception {
        ManageUser manageUser = ManageUser.where("user_name=:user_name", map(
                "user_name", userName
        )).single_fetch();

        if (manageUser == null) {
            throw new RecordNotFoundException("user not found");
        }

        if (manageUser.validatePassword(password)) {
            return manageUser;
        }

        return null;
    }

}

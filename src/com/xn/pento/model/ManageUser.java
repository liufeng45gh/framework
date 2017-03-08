package com.xn.pento.model;

import com.xn.pento.common.BaseModel;
import com.xn.pento.util.Helper;
import net.csdn.annotation.validate.Validate;

import javax.persistence.OneToMany;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.list;
import static net.csdn.modules.http.ApplicationController.map;
import static net.csdn.validate.ValidateHelper.format;
import static net.csdn.validate.ValidateHelper.presence;
import static net.csdn.validate.ValidateHelper.uniqueness;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-13
 * Time: PM2:15
 * To change this template use File | Settings | File Templates.
 */
public class ManageUser extends BaseModel {
    @Validate
    private final static Map $user_name = map(
            presence, map("message", "{}不能为空"),
            uniqueness, map("message", "{}不能重复")
    );

    @Validate
    private final static Map $password = map(
            presence, map("message", "{}不能为空")
    );

    public void encryptPassword() {
        attr("password", Helper.encrypt(attr("password", String.class)));
    }

    public boolean validatePassword(String password) {
        return attr("password", String.class).equals(Helper.encrypt(password));
    }
}


package com.xn.pento.model;

import com.xn.pento.common.BaseModel;
import com.xn.pento.service.UserService;
import net.csdn.common.exception.AutoGeneration;
import net.csdn.enhancer.association.ManyToOneEnhancer;
import net.csdn.jpa.association.Association;

import javax.persistence.ManyToOne;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-18
 * Time: AM10:44
 * To change this template use File | Settings | File Templates.
 */
public class UserToken extends BaseModel {
    public User user() throws Exception {
        return User.find(attrAsInt("user_id"));
    }

}

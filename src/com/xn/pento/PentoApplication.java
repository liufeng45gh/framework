package com.xn.pento;

import net.csdn.ServiceFramework;
import net.csdn.bootstrap.Application;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-2-20
 * Time: PM1:22
 * To change this template use File | Settings | File Templates.
 */
public class PentoApplication extends Application {
    public static void main(String[] args) {
        ServiceFramework.scanService.setLoader(PentoApplication.class);
        Application.main(args);
    }
}

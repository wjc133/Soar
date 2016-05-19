package com.elite.tools.soar.toolbox;

/**
 * Created by wjc133
 * Date: 2016/5/20
 * Time: 1:32
 * Description:
 */
public enum  SystemPathGetter {
    INSTANCE;

    public String getRootPath() {
        return getClass().getResource("/").getFile();
    }
}

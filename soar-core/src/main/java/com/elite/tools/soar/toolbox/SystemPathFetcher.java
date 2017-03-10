package com.elite.tools.soar.toolbox;

/**
 * Created by wjc133
 * Date: 2016/5/20
 * Time: 1:32
 * Description: 系统路径拾取器
 */
public enum SystemPathFetcher {
    INSTANCE;

    public String getRootPath() {
        return getClass().getResource("/").getFile();
    }
}

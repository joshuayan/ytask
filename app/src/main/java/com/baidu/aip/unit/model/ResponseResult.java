/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.unit.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseResult {


    private long logId;
    public Result result;

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String jsonRes;

    public String getJsonRes() {
        return jsonRes;
    }

    public void setJsonRes(String jsonRes) {
        this.jsonRes = jsonRes;
    }



    public class Result {
        public UnitResponseResult.Schema schema;
        public List<UnitResponseResult.Action> actionList = new ArrayList<>();

        public String sessionId;

    }
}

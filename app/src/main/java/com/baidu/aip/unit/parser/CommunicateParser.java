/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.unit.parser;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.unit.exception.UnitError;
import com.baidu.aip.unit.model.UnitResponseResult;

public class CommunicateParser implements Parser<UnitResponseResult> {

    @Override
    public UnitResponseResult parse(String json) throws UnitError {


        UnitResponseResult response=  JSON.parseObject(json,UnitResponseResult.class);

//
//        Log.e("xx", "CommunicateParser:" + json);
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//
//            if (jsonObject.has("error_code")) {
//                UnitError error = new UnitError(jsonObject.optInt("error_code"), jsonObject.optString("error_msg"));
//                throw error;
//            }
//
//            UnitResponseResult result = new UnitResponseResult();
//            result.setLogId(jsonObject.optLong("log_id"));
//            result.setJsonRes(json);
//
//            JSONObject resultObject = jsonObject.getJSONObject("result");
//            List<UnitResponseResult.Action> actionList = result.actionList;
//            JSONArray actionListArray = resultObject.optJSONArray("action_list");
//            if (actionListArray != null) {
//                for (int i = 0; i < actionListArray.length(); i++) {
//                    JSONObject actionListObject = actionListArray.optJSONObject(i);
//                    if (actionListObject == null) {
//                        continue;
//                    }
//                    UnitResponseResult.Action action = new UnitResponseResult.Action();
//                    action.actionId = actionListObject.optString("action_id");
//                    JSONObject actionTypeObject = actionListObject.optJSONObject("action_type");
//
//                    action.actionType = new UnitResponseResult.ActionType();
//                    action.actionType.target = actionTypeObject.optString("act_target");
//                    action.actionType.targetDetail = actionTypeObject.optString("act_target_detail");
//                    action.actionType.type = actionTypeObject.optString("act_type");
//                    action.actionType.typeDetail = actionTypeObject.optString("act_type_detail");
//
//                    action.confidence = actionListObject.optInt("confidence");
//                    action.say = actionListObject.optString("say");
//
//                    JSONArray hintListArray = actionListObject.optJSONArray("hint_list");
//                    if (hintListArray != null) {
//                        for (int j = 0; j < hintListArray.length(); j++) {
//                            JSONObject hintQuery =  hintListArray.optJSONObject(j);
//                            if (hintQuery != null) {
//                                action.hintList.add(hintQuery.optString("hint_query"));
//                            }
//                        }
//                    }
//
//                    actionList.add(action);
//                }
//            }
//
//            result.sessionId = resultObject.optString("session_id");

            return response;
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//            UnitError error = new UnitError(UnitError.ErrorCode.JSON_PARSE_ERROR, "Json parse error:" + json, e);
//            throw error;
//        }
    }
}

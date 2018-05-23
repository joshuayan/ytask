/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.unit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UnitResponseResult extends ResponseResult {

    public static class Action {
        public String actionId;
        public ActionType actionType;
        public List argList = new ArrayList<>();
        // public CodeAction codeAction;
        public int confidence;
        public List exeStatusList = new ArrayList<>();
        public List<String> hintList = new ArrayList<String>();
        public String mainExe;
        public String say;
    }

    public static class ActionType {
        public String target;
        public String targetDetail;
        public String type;
        public String typeDetail;
    }

    // public static class CodeAction {}

    public static class Schema {
        public List<SlotItem> botMergedSlots = new ArrayList();
        public String currentQueryInent;
        public int intentConfidence;

        public List<SlotItem> getSlotsByType(String type) {

            List<SlotItem> result = new ArrayList<>();

            for (SlotItem item : this.botMergedSlots) {
                if (Objects.equals(type, item.type)) {
                    result.add(item);
                }
            }

            return result;
        }
    }

    public static class SlotItem {
        public int begin;
        public float confidence;
        public int length;
        public String mergeMethod;
        public String normalizedWord;
        public String originalWord;
        public int sessionOffset;
        public String type;
        public String wordType;

        @Override
        public String toString() {
            return "SlotItem{" +
                    "begin=" + begin +
                    ", confidence=" + confidence +
                    ", length=" + length +
                    ", mergeMethod='" + mergeMethod + '\'' +
                    ", normalizedWord='" + normalizedWord + '\'' +
                    ", originalWord='" + originalWord + '\'' +
                    ", sessionOffset=" + sessionOffset +
                    ", type='" + type + '\'' +
                    ", wordType='" + wordType + '\'' +
                    '}';
        }
    }

}

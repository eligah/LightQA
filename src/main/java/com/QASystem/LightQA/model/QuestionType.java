package com.QASystem.LightQA.model;

public enum  QuestionType {
    NULL("未知"), PERSON_NAME("人名"), LOCATION_NAME("地名"), ORGANIZATION_NAME("团体机构名"), NUMBER("数字"), TIME("时间"), DEFINITIION("定义"), OBJECT("对象");

    public String getPos() {
        String pos = "unknown";

        if (QuestionType.PERSON_NAME == this){
            pos = "nr";
        }
        else if (QuestionType.LOCATION_NAME == this){
            pos = "ns";
        }
        else if (QuestionType.ORGANIZATION_NAME == this){
            pos = "nt";
        }
        else if (QuestionType.NUMBER == this){
            pos = "m";
        }
        else if (QuestionType.TIME == this){
            pos = "t";
        }
        return pos;
    }

    private String des;
    private QuestionType(String des) { this.des = des; }

    public String getDes() { return des; }
}
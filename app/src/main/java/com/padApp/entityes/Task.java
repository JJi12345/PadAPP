package com.padApp.entityes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {
    String carNo;
    String detail;
    String level;
    String prisonerName;
    String taskNo;
    String userName;


    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String toString(){
        return "carNo-"+this.carNo+"-"+
                "detail-" +this.detail+"-"+
                "level-" +this.level+"-"+
                "prisonerName-" +this.prisonerName+"-"+
                "taskNo-"+this.taskNo +"-"+
                "userName-"+this.userName +"-";
    }

    public Task String2Task(String string){
        Task task_ = new Task();
        if (string ==null){
        }
        else {
                String pattern = "-.*-";
                Pattern p1 = Pattern.compile("carNo"+pattern);
                Pattern p2 = Pattern.compile("detail"+pattern);
                Pattern p3 = Pattern.compile("level"+pattern);
                Pattern p4 = Pattern.compile("prisonerName"+pattern);
                Pattern p5 = Pattern.compile("taskNo"+pattern);
                Pattern p6 = Pattern.compile("userName"+pattern);

                Matcher m1 = p1.matcher(string);
                Matcher m2 = p2.matcher(string);
                Matcher m3 = p3.matcher(string);
                Matcher m4 = p4.matcher(string);
                Matcher m5 = p5.matcher(string);
                Matcher m6 = p6.matcher(string);

                if (m1.find()){
                    String[] groups = m1.group().split("-");
                    task_.setCarNo(groups[1]);
                }
                if (m2.find()) {
                    String[] groups = m2.group().split("-");
                   task_.setDetail(groups[1]);
                }
                if (m3.find()){
                    String[] groups = m3.group().split("-");
                   task_.setLevel(groups[1]);
                }
                if (m4.find()){
                    String[] groups = m4.group().split("-");
                    task_.setPrisonerName(groups[1]);
                }
                if (m5.find()){
                    String[] groups = m5.group().split("-");
                    task_.setTaskNo(groups[1]);
                }
                if (m6.find()){
                    String[] groups = m6.group().split("-");
                    task_.setUserName(groups[1]);
                }
        }
        return task_;
    }
}

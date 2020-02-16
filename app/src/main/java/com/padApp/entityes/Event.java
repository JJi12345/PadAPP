package com.padApp.entityes;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event {
    String id;
    String time;
    String carNo;
    String riskValue;
    String comment;
    String state;

    String prisonerId;
    Boolean misdeclaration;

    String dealState;
    String createAt;

    String prisonerName;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getId() {
        return id;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDealState() {
        return dealState;
    }

    public void setDealState(String dealState) {
        this.dealState = dealState;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPrisonerId() {
        return prisonerId;
    }

    public void setPrisonerId(String prisonerId) {
        this.prisonerId = prisonerId;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getRiskValue() {
        return riskValue;
    }

    public void setRiskValue(String riskValue) {
        this.riskValue = riskValue;
    }

    public Boolean getMisdeclaration() {
        return misdeclaration;
    }

    public void setMisdeclaration(Boolean misdeclaration) {
        this.misdeclaration = misdeclaration;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String toString(){
        return "id-"+this.id+"-"+
                "time-" +this.time+"-"+
                "name-" +this.prisonerName+"-"+
                "related_car-" +this.carNo+"-"+
                "state-"+this.state +"-"+
                "detail-"+this.comment +"-"+
                "prisonerId-"+this.prisonerId +"-"+
                "misdeclaration-"+this.misdeclaration +"-";
    }
    public ArrayList<Event> String2Events(String string){
        ArrayList<Event> eventList = new ArrayList<>();

        if (string ==null){

        }
        else {
            String[] arrayString = string.split("%");
            ArrayList<String>events = new ArrayList<>();
            for (int i=0 ;i<arrayString.length; i++) {
                if(arrayString[i]!=null && arrayString[i].length()!=0){ //过滤掉数组arrayString里面的空字符串
                    events.add(arrayString[i]);
                }
            }
            System.out.println(events);
            for(int i = 0;i<events.size();i++){
                String str = events.get(i);
                Event event_ = new Event();
                String pattern = "-.*-";
                Pattern p1 = Pattern.compile("time"+pattern);
                Pattern p2 = Pattern.compile("name"+pattern);
                Pattern p3 = Pattern.compile("related_car"+pattern);
                Pattern p4 = Pattern.compile("state"+pattern);
                Pattern p6 = Pattern.compile("id"+pattern);
                Pattern p7 = Pattern.compile("detail"+pattern);
                Pattern p8 = Pattern.compile("prisonerId"+pattern);
                Pattern p9 = Pattern.compile("misdeclaration"+pattern);

                Matcher m9 = p9.matcher(str);
                Matcher m8 = p8.matcher(str);
                Matcher m7 = p7.matcher(str);
                Matcher m6 = p6.matcher(str);
                Matcher m1 = p1.matcher(str);
                Matcher m2 = p2.matcher(str);
                Matcher m3 = p3.matcher(str);
                Matcher m4 = p4.matcher(str);



                if (m1.find()){
                    String[] groups = m1.group().split("-");
                    event_.setTime(groups[1]);
                }
                if (m2.find()) {
                    String[] groups = m2.group().split("-");
                    event_.setPrisonerName(groups[1]);
                }
                if (m3.find()){
                    String[] groups = m3.group().split("-");
                    event_.setCarNo(groups[1]);
                }
                if (m4.find()){
                    String[] groups = m4.group().split("-");
                    event_.setState(groups[1]);
                }

                if (m6.find()){
                    String[] groups = m6.group().split("-");
                    event_.setId(groups[1]);
                }
                if (m7.find()){
                    String[] groups = m7.group().split("-");
                    event_.setComment(groups[1]);
                }
                if (m8.find()){
                    String[] groups = m8.group().split("-");
                    event_.setPrisonerId(groups[1]);
                }
                if (m9.find()){
                    String[] groups = m9.group().split("-");
                    event_.setMisdeclaration(Boolean.getBoolean(groups[1]));
                }
                eventList.add(event_);
            }
        }
        return eventList;
    }
}

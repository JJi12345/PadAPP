package com.padApp.entityes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Criminal {
    String photo;
    String name;
    String age;
    String gender;
    String educationLevel;
    String height;
    String weight;
    String bfr;
    String crimes;
    String ex;
    String note;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBfr() {
        return bfr;
    }

    public void setBfr(String bfr) {
        this.bfr = bfr;
    }

    public String getCrimes() {
        return crimes;
    }

    public void setCrimes(String crimes) {
        this.crimes = crimes;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public String toString(){
        return "photo-" +this.photo+"-"+
                "name-" +this.name+"-"+
                "age-" +this.age+"-"+
                "gender-"+this.gender +"-"+
                "educationLevel-"+this.educationLevel +"-"+
                "height-"+this.height+"-"+
                "weight-"+this.weight +"-"+
                "bfr-"+this.bfr +"-"+
                "crimes-"+this.crimes +"-"+
                "ex-"+this.ex+"-"+
                "note-"+this.note +"-";
    }
    public Criminal String2Criminal(String str){

        Criminal criminal = new Criminal();
        if (str ==null){

        }
        else {
            String pattern = "-.*-";
            Pattern p1 = Pattern.compile("photo" + pattern);
            Pattern p2 = Pattern.compile("name" + pattern);
            Pattern p3 = Pattern.compile("age" + pattern);
            Pattern p4 = Pattern.compile("gender" + pattern);
            Pattern p5 = Pattern.compile("educationLevel" + pattern);
            Pattern p6 = Pattern.compile("height" + pattern);
            Pattern p7 = Pattern.compile("weight" + pattern);
            Pattern p8 = Pattern.compile("bfr" + pattern);
            Pattern p9 = Pattern.compile("crimes" + pattern);
            Pattern p10 = Pattern.compile("ex" + pattern);
            Pattern p11 = Pattern.compile("note" + pattern);
            Matcher m1 = p1.matcher(str);
            Matcher m2 = p2.matcher(str);
            Matcher m3 = p3.matcher(str);
            Matcher m4 = p4.matcher(str);
            Matcher m5 = p5.matcher(str);
            Matcher m6 = p6.matcher(str);
            Matcher m7 = p7.matcher(str);
            Matcher m8 = p8.matcher(str);
            Matcher m9 = p9.matcher(str);
            Matcher m10 = p10.matcher(str);
            Matcher m11 = p11.matcher(str);

            if (m1.find()) {
                String[] groups = m1.group().split("-");
                criminal.setPhoto(groups[1]);
            }
            if (m2.find()) {
                String[] groups = m2.group().split("-");
                criminal.setName(groups[1]);
            }
            if (m3.find()) {
                String[] groups = m3.group().split("-");
                criminal.setAge(groups[1]);
            }
            if (m4.find()) {
                String[] groups = m4.group().split("-");
                criminal.setGender(groups[1]);
            }
            if (m5.find()) {
                String[] groups = m5.group().split("-");
                criminal.setEducationLevel(groups[1]);
            }
            if (m6.find()) {
                String[] groups = m6.group().split("-");
                criminal.setHeight(groups[1]);
            }
            if (m7.find()) {
                String[] groups = m7.group().split("-");
                criminal.setWeight(groups[1]);
            }
            if (m8.find()) {
                String[] groups = m8.group().split("-");
                criminal.setBfr(groups[1]);
            }
            if (m9.find()) {
                String[] groups = m9.group().split("-");
                criminal.setCrimes(groups[1]);
            }
            if (m10.find()) {
                String[] groups = m10.group().split("-");
                criminal.setEx(groups[1]);
            }
            if (m11.find()) {
                String[] groups = m11.group().split("-");
                criminal.setNote(groups[1]);
            }
        }
        return criminal;
    }
}

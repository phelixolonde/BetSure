package com.automata.betsure;

/**
 * Created by Hansen on 23-Jun-17.
 */

public class Model {
    String country,league,home,away,tip,odd,time,result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public Model() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getOdd() {
        return odd;
    }

    public void setOdd(String odd) {
        this.odd = odd;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

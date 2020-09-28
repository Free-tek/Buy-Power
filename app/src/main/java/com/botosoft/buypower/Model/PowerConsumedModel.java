package com.botosoft.buypower.Model;

public class PowerConsumedModel {

    private String timeStamp, port1, port2, port3, port4, port5, port6, port1Name, port2Name, port3Name, port4Name, port5Name, port6Name, powerConsumed;

    public PowerConsumedModel(){

    }

    public PowerConsumedModel(String timeStamp, String port1, String port2, String port3, String port4, String port5, String port6, String port1Name, String port2Name, String port3Name, String port4Name, String port5Name, String port6Name, String powerConsumed) {
        this.timeStamp = timeStamp;
        this.port1 = port1;
        this.port2 = port2;
        this.port3 = port3;
        this.port4 = port4;
        this.port5 = port5;
        this.port6 = port6;
        this.port1Name = port1Name;
        this.port2Name = port2Name;
        this.port3Name = port3Name;
        this.port4Name = port4Name;
        this.port5Name = port5Name;
        this.port6Name = port6Name;
        this.powerConsumed = powerConsumed;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPort1() {
        return port1;
    }

    public void setPort1(String port1) {
        this.port1 = port1;
    }

    public String getPort2() {
        return port2;
    }

    public void setPort2(String port2) {
        this.port2 = port2;
    }

    public String getPort3() {
        return port3;
    }

    public void setPort3(String port3) {
        this.port3 = port3;
    }

    public String getPort4() {
        return port4;
    }

    public void setPort4(String port4) {
        this.port4 = port4;
    }

    public String getPort5() {
        return port5;
    }

    public void setPort5(String port5) {
        this.port5 = port5;
    }

    public String getPort6() {
        return port6;
    }

    public void setPort6(String port6) {
        this.port6 = port6;
    }

    public String getPowerConsumed() {
        return powerConsumed;
    }

    public void setPowerConsumed(String powerConsumed) {
        this.powerConsumed = powerConsumed;
    }


    public String getPort1Name() {
        return port1Name;
    }

    public void setPort1Name(String port1Name) {
        this.port1Name = port1Name;
    }

    public String getPort2Name() {
        return port2Name;
    }

    public void setPort2Name(String port2Name) {
        this.port2Name = port2Name;
    }

    public String getPort3Name() {
        return port3Name;
    }

    public void setPort3Name(String port3Name) {
        this.port3Name = port3Name;
    }

    public String getPort4Name() {
        return port4Name;
    }

    public void setPort4Name(String port4Name) {
        this.port4Name = port4Name;
    }

    public String getPort5Name() {
        return port5Name;
    }

    public void setPort5Name(String port5Name) {
        this.port5Name = port5Name;
    }

    public String getPort6Name() {
        return port6Name;
    }

    public void setPort6Name(String port6Name) {
        this.port6Name = port6Name;
    }
}

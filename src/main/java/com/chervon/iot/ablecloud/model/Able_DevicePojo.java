package com.chervon.iot.ablecloud.model;
import com.chervon.iot.ablecloud.model.Able_BatteryPojo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by 喷水君 on 2017/7/29.
 */
public class Able_DevicePojo {
    private String psId;
    private long timestamp;
    private boolean bat4Exist;
    private boolean bat3Exist;
    private boolean bat2Exist;
    private boolean bat1Exist;
    private boolean ac3;
    private boolean ac2;
    private boolean ac1;
    private boolean dc;
    private int link;
    private boolean ac3Schedule;
    private boolean ac2Schedule;
    private boolean ac1Schedule;
    private boolean dcSchedule;
    private int chargeState;
    private boolean lock;
    private boolean chargeModule;
    private boolean electricFan;
    private boolean dcModule;
    private boolean inverter;
    private boolean underVolt;
    private boolean overCurrent;
    private boolean overTemp;
    private boolean otherFault;
    private double ac1Current;
    private double ac2Current;
    private double ac3Current;
    private int ac1Power;
    private int ac2Power;
    private int ac3Power;
    private Able_BatteryPojo bat1;
    private Able_BatteryPojo bat2;
    private Able_BatteryPojo bat3;
    private Able_BatteryPojo bat4;
    private long  totalRemainingTime;



    public  Able_DevicePojo(){

    }

    public Able_DevicePojo(String psId, long timestamp, boolean bat4Exist, boolean bat3Exist, boolean bat2Exist, boolean bat1Exist,
                           boolean ac3, boolean ac2, boolean ac1, boolean dc, int link, boolean ac3Schedule, boolean ac2Schedule,
                           boolean ac1Schedule, boolean dcSchedule, int chargeState, boolean lock, boolean chargeModule, boolean electricFan,
                           boolean dcModule, boolean inverter, boolean underVolt, boolean overCurrent, boolean overTemp, boolean otherFault,
                           double ac1Current, double ac2Current, double ac3Current, int ac1Power, int ac2Power, int ac3Power, Able_BatteryPojo bat1,
                           Able_BatteryPojo bat2, Able_BatteryPojo bat3, Able_BatteryPojo bat4,long totalRemainingTime) {
        this.psId = psId;
        this.timestamp = timestamp;
        this.bat4Exist = bat4Exist;
        this.bat3Exist = bat3Exist;
        this.bat2Exist = bat2Exist;
        this.bat1Exist = bat1Exist;
        this.ac3 = ac3;
        this.ac2 = ac2;
        this.ac1 = ac1;
        this.dc = dc;
        this.link = link;
        this.ac3Schedule = ac3Schedule;
        this.ac2Schedule = ac2Schedule;
        this.ac1Schedule = ac1Schedule;
        this.dcSchedule = dcSchedule;
        this.chargeState = chargeState;
        this.lock = lock;
        this.chargeModule = chargeModule;
        this.electricFan = electricFan;
        this.dcModule = dcModule;
        this.inverter = inverter;
        this.underVolt = underVolt;
        this.overCurrent = overCurrent;
        this.overTemp = overTemp;
        this.otherFault = otherFault;
        this.ac1Current = ac1Current;
        this.ac2Current = ac2Current;
        this.ac3Current = ac3Current;
        this.ac1Power = ac1Power;
        this.ac2Power = ac2Power;
        this.ac3Power = ac3Power;
        this.bat1 = bat1;
        this.bat2 = bat2;
        this.bat3 = bat3;
        this.bat4 = bat4;
        this.totalRemainingTime =totalRemainingTime;
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isBat4Exist() {
        return bat4Exist;
    }

    public void setBat4Exist(boolean bat4Exist) {
        this.bat4Exist = bat4Exist;
    }

    public boolean isBat3Exist() {
        return bat3Exist;
    }

    public void setBat3Exist(boolean bat3Exist) {
        this.bat3Exist = bat3Exist;
    }

    public boolean isBat2Exist() {
        return bat2Exist;
    }

    public void setBat2Exist(boolean bat2Exist) {
        this.bat2Exist = bat2Exist;
    }

    public boolean isBat1Exist() {
        return bat1Exist;
    }

    public void setBat1Exist(boolean bat1Exist) {
        this.bat1Exist = bat1Exist;
    }

    public boolean isAc3() {
        return ac3;
    }

    public void setAc3(boolean ac3) {
        this.ac3 = ac3;
    }

    public boolean isAc2() {
        return ac2;
    }

    public void setAc2(boolean ac2) {
        this.ac2 = ac2;
    }

    public boolean isAc1() {
        return ac1;
    }

    public void setAc1(boolean ac1) {
        this.ac1 = ac1;
    }

    public boolean isDc() {
        return dc;
    }

    public void setDc(boolean dc) {
        this.dc = dc;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public boolean isAc3Schedule() {
        return ac3Schedule;
    }

    public void setAc3Schedule(boolean ac3Schedule) {
        this.ac3Schedule = ac3Schedule;
    }

    public boolean isAc2Schedule() {
        return ac2Schedule;
    }

    public void setAc2Schedule(boolean ac2Schedule) {
        this.ac2Schedule = ac2Schedule;
    }

    public boolean isAc1Schedule() {
        return ac1Schedule;
    }

    public void setAc1Schedule(boolean ac1Schedule) {
        this.ac1Schedule = ac1Schedule;
    }

    public boolean isDcSchedule() {
        return dcSchedule;
    }

    public void setDcSchedule(boolean dcSchedule) {
        this.dcSchedule = dcSchedule;
    }

    public int getChargeState() {
        return chargeState;
    }

    public void setChargeState(int chargeState) {
        this.chargeState = chargeState;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isChargeModule() {
        return chargeModule;
    }

    public void setChargeModule(boolean chargeModule) {
        this.chargeModule = chargeModule;
    }

    public boolean isElectricFan() {
        return electricFan;
    }

    public void setElectricFan(boolean electricFan) {
        this.electricFan = electricFan;
    }

    public boolean isDcModule() {
        return dcModule;
    }

    public void setDcModule(boolean dcModule) {
        this.dcModule = dcModule;
    }

    public boolean isInverter() {
        return inverter;
    }

    public void setInverter(boolean inverter) {
        this.inverter = inverter;
    }

    public boolean isUnderVolt() {
        return underVolt;
    }

    public void setUnderVolt(boolean underVolt) {
        this.underVolt = underVolt;
    }

    public boolean isOverCurrent() {
        return overCurrent;
    }

    public void setOverCurrent(boolean overCurrent) {
        this.overCurrent = overCurrent;
    }

    public boolean isOverTemp() {
        return overTemp;
    }

    public void setOverTemp(boolean overTemp) {
        this.overTemp = overTemp;
    }

    public boolean isOtherFault() {
        return otherFault;
    }

    public void setOtherFault(boolean otherFault) {
        this.otherFault = otherFault;
    }

    public double getAc1Current() {
        return ac1Current;
    }

    public void setAc1Current(double ac1Current) {
        this.ac1Current = ac1Current;
    }

    public double getAc2Current() {
        return ac2Current;
    }

    public void setAc2Current(double ac2Current) {
        this.ac2Current = ac2Current;
    }

    public double getAc3Current() {
        return ac3Current;
    }

    public void setAc3Current(double ac3Current) {
        this.ac3Current = ac3Current;
    }

    public int getAc1Power() {
        return ac1Power;
    }

    public void setAc1Power(int ac1Power) {
        this.ac1Power = ac1Power;
    }

    public int getAc2Power() {
        return ac2Power;
    }

    public void setAc2Power(int ac2Power) {
        this.ac2Power = ac2Power;
    }

    public int getAc3Power() {
        return ac3Power;
    }

    public void setAc3Power(int ac3Power) {
        this.ac3Power = ac3Power;
    }

    public Able_BatteryPojo getBat1() {
        return bat1;
    }

    public void setBat1(Able_BatteryPojo bat1) {
        this.bat1 = bat1;
    }

    public Able_BatteryPojo getBat2() {
        return bat2;
    }

    public void setBat2(Able_BatteryPojo bat2) {
        this.bat2 = bat2;
    }

    public Able_BatteryPojo getBat3() {
        return bat3;
    }

    public void setBat3(Able_BatteryPojo bat3) {
        this.bat3 = bat3;
    }

    public Able_BatteryPojo getBat4() {
        return bat4;
    }

    public void setBat4(Able_BatteryPojo bat4) {
        this.bat4 = bat4;
    }
    public long getTotalRemainingTime() {
        return totalRemainingTime;
    }

    public void setTotalRemainingTime(long totalRemainingTime) {
        this.totalRemainingTime = totalRemainingTime;
    }
}

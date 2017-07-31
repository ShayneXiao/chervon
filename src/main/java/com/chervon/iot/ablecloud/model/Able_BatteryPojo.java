package com.chervon.iot.ablecloud.model;

/**
 * Created by 喷水君 on 2017/7/29.
 */
public class Able_BatteryPojo {
    private String batId;
    private int chargeState;
    private boolean overTemp;
    private boolean underVolt;
    private boolean fuseDamage;
    private boolean voltImbalance;
    private boolean singleLowVolt;
    private boolean commFault;
    private boolean dPinDamage;
    private boolean lowRecCapacity;
    private boolean precharge;
    private boolean otherFault;
    private double totalVolt;
    private int dumpEnergy;
    private int dumpEnergyPercent;
    private int temp;
    private double current;
    public  Able_BatteryPojo(){}

    public Able_BatteryPojo(String batId, int chargeState, boolean overTemp, boolean underVolt, boolean fuseDamage, boolean voltImbalance,
                            boolean singleLowVolt, boolean commFault, boolean dPinDamage, boolean lowRecCapacity, boolean precharge, boolean otherFault,
                            double totalVolt, int dumpEnergy, int dumpEnergyPercent, int temp, double current) {
        this.batId = batId;
        this.chargeState = chargeState;
        this.overTemp = overTemp;
        this.underVolt = underVolt;
        this.fuseDamage = fuseDamage;
        this.voltImbalance = voltImbalance;
        this.singleLowVolt = singleLowVolt;
        this.commFault = commFault;
        this.dPinDamage = dPinDamage;
        this.lowRecCapacity = lowRecCapacity;
        this.precharge = precharge;
        this.otherFault = otherFault;
        this.totalVolt = totalVolt;
        this.dumpEnergy = dumpEnergy;
        this.dumpEnergyPercent = dumpEnergyPercent;
        this.temp = temp;
        this.current = current;
    }

    public String getBatId() {
        return batId;
    }

    public void setBatId(String batId) {
        this.batId = batId;
    }

    public int getChargeState() {
        return chargeState;
    }

    public void setChargeState(int chargeState) {
        this.chargeState = chargeState;
    }

    public boolean isOverTemp() {
        return overTemp;
    }

    public void setOverTemp(boolean overTemp) {
        this.overTemp = overTemp;
    }

    public boolean isUnderVolt() {
        return underVolt;
    }

    public void setUnderVolt(boolean underVolt) {
        this.underVolt = underVolt;
    }

    public boolean isFuseDamage() {
        return fuseDamage;
    }

    public void setFuseDamage(boolean fuseDamage) {
        this.fuseDamage = fuseDamage;
    }

    public boolean isVoltImbalance() {
        return voltImbalance;
    }

    public void setVoltImbalance(boolean voltImbalance) {
        this.voltImbalance = voltImbalance;
    }

    public boolean isSingleLowVolt() {
        return singleLowVolt;
    }

    public void setSingleLowVolt(boolean singleLowVolt) {
        this.singleLowVolt = singleLowVolt;
    }

    public boolean isCommFault() {
        return commFault;
    }

    public void setCommFault(boolean commFault) {
        this.commFault = commFault;
    }

    public boolean isdPinDamage() {
        return dPinDamage;
    }

    public void setdPinDamage(boolean dPinDamage) {
        this.dPinDamage = dPinDamage;
    }

    public boolean isLowRecCapacity() {
        return lowRecCapacity;
    }

    public void setLowRecCapacity(boolean lowRecCapacity) {
        this.lowRecCapacity = lowRecCapacity;
    }

    public boolean isPrecharge() {
        return precharge;
    }

    public void setPrecharge(boolean precharge) {
        this.precharge = precharge;
    }

    public boolean isOtherFault() {
        return otherFault;
    }

    public void setOtherFault(boolean otherFault) {
        this.otherFault = otherFault;
    }

    public double getTotalVolt() {
        return totalVolt;
    }

    public void setTotalVolt(double totalVolt) {
        this.totalVolt = totalVolt;
    }

    public int getDumpEnergy() {
        return dumpEnergy;
    }

    public void setDumpEnergy(int dumpEnergy) {
        this.dumpEnergy = dumpEnergy;
    }

    public int getDumpEnergyPercent() {
        return dumpEnergyPercent;
    }

    public void setDumpEnergyPercent(int dumpEnergyPercent) {
        this.dumpEnergyPercent = dumpEnergyPercent;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }
}

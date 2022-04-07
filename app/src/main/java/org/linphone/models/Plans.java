package org.linphone.models;

public class Plans {

    private int planId, initialCost, periodCost;
    private String name, period;

    public Plans(int planId, String name){//, String period, int initialCost, int periodCost) {
        this.planId = planId;
        //this.initialCost = initialCost; //not used
        //this.periodCost = periodCost;  //not used
        this.name = name;
        //this.period = period; //not used
    }

    public int getPlanId() {
        return planId;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public int getPeriodCost() {
        return periodCost;
    }

    public String getName() {
        return name;
    }

    public String getPeriod() {
        return period;
    }
}

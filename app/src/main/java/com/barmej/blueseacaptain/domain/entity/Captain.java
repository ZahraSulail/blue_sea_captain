package com.barmej.blueseacaptain.domain.entity;

import java.io.Serializable;

public class Captain implements Serializable {

    private String id;
    private String status;
    private String name;
    private String assignedTrip;


    public Captain() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAssignedTrip() {
        return assignedTrip;
    }

    public void setAssignedTrip(String assignedTrip) {
        this.assignedTrip = assignedTrip;
    }


    public enum Status{
        AVAILABEL,
        ON_TRIP,
    }
}

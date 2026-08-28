package com.springboot.patientmanagementsystem.rw.ac.auca.model;

import javax.annotation.ManagedBean;

@ManagedBean
public class Patient {
    private Long id;
    private String patientId;
    private String observation;

    public Patient(Long id, String patientId, String observation) {
        this.id = id;
        this.patientId = patientId;
        this.observation = observation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}

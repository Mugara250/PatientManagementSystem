package rw.ac.auca.model;

import javax.persistence.*;

@Entity
@Table(name = "patients")
public class Patient extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 5)
    private String patientId;

    @Column(nullable = false, length = 255)
    private String observation;

    public Patient() {
        super(); // now valid — Person() exists
    }

    public Patient(Long id, String patientId, String observation) {
        super();
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
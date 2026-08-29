package rw.ac.auca.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityTransaction;
import rw.ac.auca.model.Patient;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Named("patientBean")
@SessionScoped
public class PatientBean implements Serializable {

    private Patient patient = new Patient();
    private List<Patient> patients = new ArrayList<>();

    // matches the persistence-unit name in your persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default");

    // ---------- CREATE / UPDATE ----------
    public String save() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (patient.getId() == 0) {
                em.persist(patient);   // new patient
            } else {
                em.merge(patient);     // existing patient being edited
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null; // stay on the same page if something goes wrong
        } finally {
            em.close();
        }

        patient = new Patient(); // reset form for next entry
        return "patientList?faces-redirect=true";
    }

    // ---------- READ (list) ----------
    public List<Patient> getPatients() {
        EntityManager em = emf.createEntityManager();
        try {
            patients = em.createQuery("SELECT p FROM Patient p", Patient.class)
                    .getResultList();
        } finally {
            em.close();
        }
        return patients;
    }

    // ---------- EDIT (prepare form with existing data) ----------
    public String prepareEdit(Patient selected) {
        this.patient = selected;
        return "patientRegistration?faces-redirect=true";
    }

    // ---------- DELETE ----------
    public String delete(Patient selected) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Patient managed = em.find(Patient.class, selected.getId());
            if (managed != null) {
                em.remove(managed);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return "patientList?faces-redirect=true";
    }

    // ---------- getters/setters ----------
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}

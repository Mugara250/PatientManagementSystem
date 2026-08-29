package rw.ac.auca.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
    private static final String PATIENT_ID_PATTERN = "^PAT-\\d{3}$";
    // matches the persistence-unit name in your persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default");

    // ---------- CREATE / UPDATE ----------
    public String save() {

        // ---- business rule: patientId must be of the pattern PAT-001
        if (patient.getPatientId() == null || !patient.getPatientId().matches(PATIENT_ID_PATTERN)) {
            addError("Patient ID must follow the format PAT-001.");
            return null;
        }



        // ---- business rule: patientId must be unique ----
        if (isDuplicatePatientId(patient)) {
            addError("Patient ID '" + patient.getPatientId() + "' is already in use.");
            return null;
        }

        // ---- business rule: age must be realistic for a "new" record ----
        if (patient.getAge() < 0 || patient.getAge() > 130) {
            addError("Age must be between 0 and 130.");
            return null;
        }

        // ---- business rule: observation required if age indicates newborn ----
        if (patient.getAge() == 0 &&
                (patient.getObservation() == null || patient.getObservation().trim().isEmpty())) {
            addError("Observation notes are required for newborn patients.");
            return null;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (patient.getId() == null) {
                em.persist(patient);   // new patient
            } else {
                em.merge(patient);     // existing patient being edited
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            addError("An unexpected error occurred while saving. Please try again.");
            return null;
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
            addError("Could not delete patient. Please try again.");
        } finally {
            em.close();
        }
        return "patientList?faces-redirect=true";
    }

    // ---------- helper: uniqueness check ----------
    private boolean isDuplicatePatientId(Patient p) {
        EntityManager em = emf.createEntityManager();
        try {
            Long excludedId = (p.getId() == null) ? -1L : p.getId();
            Long count = em.createQuery(
                            "SELECT COUNT(pt) FROM Patient pt WHERE pt.patientId = :pid AND pt.id != :id",
                            Long.class)
                    .setParameter("pid", p.getPatientId())
                    .setParameter("id", excludedId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // ---------- helper: add a FacesMessage ----------
    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    // ---------- getters/setters ----------
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
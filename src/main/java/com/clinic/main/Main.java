// File: com/clinic/main/Main.java
package com.clinic.main;

import com.clinic.database.DBConnection;
import com.clinic.database.DatabaseInitializer;
import com.clinic.exception.*;
import com.clinic.model.*;
import com.clinic.model.Appointment.Status;
import com.clinic.repository.*;
import com.clinic.service.*;
import com.clinic.utils.DateHelper;
import com.clinic.utils.InputHelper;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║         🏥  CLINIC MANAGEMENT SYSTEM  🏥             ║
 * ║         Console UI · Java 17 · SQLite                ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * Entry point.  Wires up repositories → services → menus.
 */
public class Main {

    // ── Services (wired once at startup) ─────────────────────────────────────
    private static PatientService       patientService;
    private static DoctorService        doctorService;
    private static AppointmentService   appointmentService;
    private static MedicalRecordService medicalRecordService;

    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        printBanner();

        // 1. Init DB
        DatabaseInitializer.initialize();

        // 2. Wire up layer
        PatientRepository     patientRepo     = new PatientRepository();
        DoctorRepository      doctorRepo      = new DoctorRepository();
        AppointmentRepository appointmentRepo = new AppointmentRepository();
        MedicalRecordRepository recordRepo    = new MedicalRecordRepository();
        PrescriptionRepository  rxRepo        = new PrescriptionRepository();

        patientService       = new PatientService(patientRepo);
        doctorService        = new DoctorService(doctorRepo);
        appointmentService   = new AppointmentService(appointmentRepo, patientRepo, doctorRepo);
        medicalRecordService = new MedicalRecordService(recordRepo, rxRepo, patientRepo);

        // 3. Main loop
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputHelper.readIntInRange("  Enter choice: ", 1, 6);
            switch (choice) {
                case 1 -> managePatients();
                case 2 -> manageDoctors();
                case 3 -> manageAppointments();
                case 4 -> manageMedicalRecords();
                case 5 -> managePrescriptions();
                case 6 -> running = false;
            }
        }

        System.out.println("\n  👋  Goodbye! Clinic Management System shutting down.");
        DBConnection.closeConnection();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN MENU
    // ═══════════════════════════════════════════════════════════════════════

    private static void printMainMenu() {
        System.out.println("""

          ╔═══════════════════════════════════════╗
          ║        CLINIC MANAGEMENT SYSTEM       ║
          ╠═══════════════════════════════════════╣
          ║  1. 👤  Manage Patients               ║
          ║  2. 🩺  Manage Doctors                ║
          ║  3. 📅  Manage Appointments           ║
          ║  4. 📋  Manage Medical Records        ║
          ║  5. 💊  Manage Prescriptions          ║
          ║  6. 🚪  Exit                          ║
          ╚═══════════════════════════════════════╝""");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. PATIENTS
    // ═══════════════════════════════════════════════════════════════════════

    private static void managePatients() {
        boolean back = false;
        while (!back) {
            System.out.println("""

          ── Patients ──────────────────────────────
           1. Add Patient
           2. Find Patient by ID
           3. List All Patients
           4. Update Patient
           5. Delete Patient
           0. Back to Main Menu
          ──────────────────────────────────────────""");
            int choice = InputHelper.readIntInRange("  Enter choice: ", 0, 5);
            switch (choice) {
                case 1 -> addPatient();
                case 2 -> findPatient();
                case 3 -> listPatients();
                case 4 -> updatePatient();
                case 5 -> deletePatient();
                case 0 -> back = true;
            }
        }
    }

    private static void addPatient() {
        System.out.println("\n  ── Add New Patient ──");
        try {
            String name  = InputHelper.readRequiredString("  Name  : ");
            int    age   = InputHelper.readInt("  Age   : ");
            String phone = InputHelper.readRequiredString("  Phone : ");
            String email = InputHelper.readRequiredString("  Email : ");

            Patient p = patientService.addPatient(name, age, phone, email);
            System.out.println("  ✅ Patient added successfully! " + p.getDisplayInfo());
        } catch (InvalidDataException e) {
            System.out.println("  ❌ Validation error: " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void findPatient() {
        System.out.println("\n  ── Find Patient by ID ──");
        try {
            int id = InputHelper.readPositiveInt("  Patient ID: ");
            Patient p = patientService.getPatientById(id);
            System.out.println("  " + p.getDisplayInfo());
        } catch (PatientNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void listPatients() {
        System.out.println("\n  ── All Patients ──");
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("  (no patients registered)");
        } else {
            patients.forEach(p -> System.out.println("  " + p.getDisplayInfo()));
            System.out.println("  Total: " + patients.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void updatePatient() {
        System.out.println("\n  ── Update Patient ──");
        try {
            int    id    = InputHelper.readPositiveInt("  Patient ID : ");
            Patient existing = patientService.getPatientById(id);
            System.out.println("  Current: " + existing.getDisplayInfo());

            String name  = InputHelper.readRequiredString("  New Name  [" + existing.getName()  + "]: ");
            String ageStr= InputHelper.readString("  New Age   [" + existing.getAge()   + "]: ");
            String phone = InputHelper.readRequiredString("  New Phone [" + existing.getPhone() + "]: ");
            String email = InputHelper.readRequiredString("  New Email [" + existing.getEmail() + "]: ");

            int age = ageStr.isBlank() ? existing.getAge() : Integer.parseInt(ageStr);
            Patient updated = patientService.updatePatient(id, name, age, phone, email);
            System.out.println("  ✅ Updated: " + updated.getDisplayInfo());
        } catch (PatientNotFoundException | InvalidDataException | NumberFormatException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void deletePatient() {
        System.out.println("\n  ── Delete Patient ──");
        try {
            int id = InputHelper.readPositiveInt("  Patient ID: ");
            Patient p = patientService.getPatientById(id);
            System.out.println("  " + p.getDisplayInfo());
            if (InputHelper.confirm("  ⚠️  Delete this patient and all their records?")) {
                patientService.deletePatient(id);
                System.out.println("  ✅ Patient deleted.");
            } else {
                System.out.println("  Cancelled.");
            }
        } catch (PatientNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. DOCTORS
    // ═══════════════════════════════════════════════════════════════════════

    private static void manageDoctors() {
        boolean back = false;
        while (!back) {
            System.out.println("""

          ── Doctors ───────────────────────────────
           1. Add Doctor
           2. Find Doctor by ID
           3. List All Doctors
           4. Update Doctor
           5. Delete Doctor
           0. Back to Main Menu
          ──────────────────────────────────────────""");
            int choice = InputHelper.readIntInRange("  Enter choice: ", 0, 5);
            switch (choice) {
                case 1 -> addDoctor();
                case 2 -> findDoctor();
                case 3 -> listDoctors();
                case 4 -> updateDoctor();
                case 5 -> deleteDoctor();
                case 0 -> back = true;
            }
        }
    }

    private static void addDoctor() {
        System.out.println("\n  ── Add New Doctor ──");
        try {
            String name  = InputHelper.readRequiredString("  Name           : ");
            String spec  = InputHelper.readRequiredString("  Specialization : ");
            String phone = InputHelper.readRequiredString("  Phone          : ");

            Doctor d = doctorService.addDoctor(name, spec, phone);
            System.out.println("  ✅ Doctor added! " + d.getDisplayInfo());
        } catch (InvalidDataException e) {
            System.out.println("  ❌ Validation error: " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void findDoctor() {
        System.out.println("\n  ── Find Doctor by ID ──");
        try {
            int id = InputHelper.readPositiveInt("  Doctor ID: ");
            Doctor d = doctorService.getDoctorById(id);
            System.out.println("  " + d.getDisplayInfo());
        } catch (DoctorNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void listDoctors() {
        System.out.println("\n  ── All Doctors ──");
        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("  (no doctors registered)");
        } else {
            doctors.forEach(d -> System.out.println("  " + d.getDisplayInfo()));
            System.out.println("  Total: " + doctors.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void updateDoctor() {
        System.out.println("\n  ── Update Doctor ──");
        try {
            int id = InputHelper.readPositiveInt("  Doctor ID: ");
            Doctor existing = doctorService.getDoctorById(id);
            System.out.println("  Current: " + existing.getDisplayInfo());

            String name  = InputHelper.readRequiredString("  New Name           [" + existing.getName()           + "]: ");
            String spec  = InputHelper.readRequiredString("  New Specialization [" + existing.getSpecialization() + "]: ");
            String phone = InputHelper.readRequiredString("  New Phone          [" + existing.getPhone()          + "]: ");

            Doctor updated = doctorService.updateDoctor(id, name, spec, phone);
            System.out.println("  ✅ Updated: " + updated.getDisplayInfo());
        } catch (DoctorNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void deleteDoctor() {
        System.out.println("\n  ── Delete Doctor ──");
        try {
            int id = InputHelper.readPositiveInt("  Doctor ID: ");
            Doctor d = doctorService.getDoctorById(id);
            System.out.println("  " + d.getDisplayInfo());
            if (InputHelper.confirm("  ⚠️  Delete this doctor?")) {
                doctorService.deleteDoctor(id);
                System.out.println("  ✅ Doctor deleted.");
            } else {
                System.out.println("  Cancelled.");
            }
        } catch (DoctorNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. APPOINTMENTS
    // ═══════════════════════════════════════════════════════════════════════

    private static void manageAppointments() {
        boolean back = false;
        while (!back) {
            System.out.println("""

          ── Appointments ──────────────────────────
           1. Create Appointment
           2. Find Appointment by ID
           3. List All Appointments
           4. Appointments for a Patient
           5. Appointments for a Doctor
           6. Complete Appointment
           7. Cancel Appointment
           8. Delete Appointment
           0. Back to Main Menu
          ──────────────────────────────────────────""");
            int choice = InputHelper.readIntInRange("  Enter choice: ", 0, 8);
            switch (choice) {
                case 1 -> createAppointment();
                case 2 -> findAppointment();
                case 3 -> listAppointments();
                case 4 -> appointmentsForPatient();
                case 5 -> appointmentsForDoctor();
                case 6 -> completeAppointment();
                case 7 -> cancelAppointment();
                case 8 -> deleteAppointment();
                case 0 -> back = true;
            }
        }
    }

    private static void createAppointment() {
        System.out.println("\n  ── Create Appointment ──");
        System.out.println("  Date/Time format: yyyy-MM-dd HH:mm  (e.g. " + DateHelper.now() + ")");
        try {
            int    patientId = InputHelper.readPositiveInt("  Patient ID : ");
            int    doctorId  = InputHelper.readPositiveInt("  Doctor ID  : ");
            String dateTime  = InputHelper.readRequiredString("  Date/Time  : ");

            Appointment appt = appointmentService.createAppointment(patientId, doctorId, dateTime);
            System.out.println("  ✅ Appointment booked! " + appt.getDisplayInfo());
        } catch (PatientNotFoundException | DoctorNotFoundException e) {
            System.out.println("  ❌ Not found: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void findAppointment() {
        System.out.println("\n  ── Find Appointment by ID ──");
        try {
            int id = InputHelper.readPositiveInt("  Appointment ID: ");
            Appointment a = appointmentService.getAppointmentById(id);
            System.out.println("  " + a.getDisplayInfo());
        } catch (AppointmentNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void listAppointments() {
        System.out.println("\n  ── All Appointments ──");
        List<Appointment> list = appointmentService.getAllAppointments();
        if (list.isEmpty()) {
            System.out.println("  (no appointments)");
        } else {
            list.forEach(a -> System.out.println("  " + a.getDisplayInfo()));
            System.out.println("  Total: " + list.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void appointmentsForPatient() {
        System.out.println("\n  ── Appointments for Patient ──");
        try {
            int id = InputHelper.readPositiveInt("  Patient ID: ");
            List<Appointment> list = appointmentService.getAppointmentsForPatient(id);
            if (list.isEmpty()) {
                System.out.println("  (no appointments for this patient)");
            } else {
                list.forEach(a -> System.out.println("  " + a.getDisplayInfo()));
            }
        } catch (PatientNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void appointmentsForDoctor() {
        System.out.println("\n  ── Appointments for Doctor ──");
        try {
            int id = InputHelper.readPositiveInt("  Doctor ID: ");
            List<Appointment> list = appointmentService.getAppointmentsForDoctor(id);
            if (list.isEmpty()) {
                System.out.println("  (no appointments for this doctor)");
            } else {
                list.forEach(a -> System.out.println("  " + a.getDisplayInfo()));
            }
        } catch (DoctorNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void completeAppointment() {
        System.out.println("\n  ── Complete Appointment ──");
        try {
            int id = InputHelper.readPositiveInt("  Appointment ID: ");
            appointmentService.completeAppointment(id);
            System.out.println("  ✅ Appointment ID=" + id + " marked as COMPLETED.");
        } catch (AppointmentNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void cancelAppointment() {
        System.out.println("\n  ── Cancel Appointment ──");
        try {
            int id = InputHelper.readPositiveInt("  Appointment ID: ");
            appointmentService.cancelAppointment(id);
            System.out.println("  ✅ Appointment ID=" + id + " marked as CANCELLED.");
        } catch (AppointmentNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void deleteAppointment() {
        System.out.println("\n  ── Delete Appointment ──");
        try {
            int id = InputHelper.readPositiveInt("  Appointment ID: ");
            if (InputHelper.confirm("  ⚠️  Delete appointment ID=" + id + "?")) {
                appointmentService.deleteAppointment(id);
                System.out.println("  ✅ Deleted.");
            } else {
                System.out.println("  Cancelled.");
            }
        } catch (AppointmentNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. MEDICAL RECORDS
    // ═══════════════════════════════════════════════════════════════════════

    private static void manageMedicalRecords() {
        boolean back = false;
        while (!back) {
            System.out.println("""

          ── Medical Records ───────────────────────
           1. Add Medical Record
           2. View Record by ID (with Prescriptions)
           3. Records for a Patient
           4. List All Records
           5. Delete Record
           0. Back to Main Menu
          ──────────────────────────────────────────""");
            int choice = InputHelper.readIntInRange("  Enter choice: ", 0, 5);
            switch (choice) {
                case 1 -> addMedicalRecord();
                case 2 -> viewRecord();
                case 3 -> recordsForPatient();
                case 4 -> listAllRecords();
                case 5 -> deleteRecord();
                case 0 -> back = true;
            }
        }
    }

    private static void addMedicalRecord() {
        System.out.println("\n  ── Add Medical Record ──");
        System.out.println("  Date format: yyyy-MM-dd  (leave blank for today: " + DateHelper.today() + ")");
        try {
            int    patientId   = InputHelper.readPositiveInt("  Patient ID   : ");
            String description = InputHelper.readRequiredString("  Description  : ");
            String diagnosis   = InputHelper.readRequiredString("  Diagnosis    : ");
            String date        = InputHelper.readString("  Date (blank=today): ");

            MedicalRecord rec = medicalRecordService.addRecord(patientId, description, diagnosis, date);
            System.out.println("  ✅ Record added! " + rec.getDisplayInfo());
        } catch (PatientNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void viewRecord() {
        System.out.println("\n  ── View Medical Record ──");
        try {
            int id = InputHelper.readPositiveInt("  Record ID: ");
            MedicalRecord rec = medicalRecordService.getRecordById(id);
            System.out.println("  " + rec.getDisplayInfo());

            List<Prescription> rxList = medicalRecordService.getPrescriptionsForRecord(id);
            if (rxList.isEmpty()) {
                System.out.println("    (no prescriptions)");
            } else {
                System.out.println("    Prescriptions:");
                rxList.forEach(rx -> System.out.println("    " + rx.getDisplayInfo()));
            }
        } catch (InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void recordsForPatient() {
        System.out.println("\n  ── Medical Records for Patient ──");
        try {
            int id = InputHelper.readPositiveInt("  Patient ID: ");
            List<MedicalRecord> records = medicalRecordService.getRecordsForPatient(id);
            if (records.isEmpty()) {
                System.out.println("  (no records for this patient)");
            } else {
                for (MedicalRecord rec : records) {
                    System.out.println("  " + rec.getDisplayInfo());
                    rec.getPrescriptions().forEach(rx -> System.out.println("    " + rx.getDisplayInfo()));
                }
            }
        } catch (PatientNotFoundException | InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void listAllRecords() {
        System.out.println("\n  ── All Medical Records ──");
        List<MedicalRecord> records = medicalRecordService.getAllRecords();
        if (records.isEmpty()) {
            System.out.println("  (no records)");
        } else {
            records.forEach(r -> System.out.println("  " + r.getDisplayInfo()));
            System.out.println("  Total: " + records.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void deleteRecord() {
        System.out.println("\n  ── Delete Medical Record ──");
        try {
            int id = InputHelper.readPositiveInt("  Record ID: ");
            if (InputHelper.confirm("  ⚠️  Delete record ID=" + id + " and its prescriptions?")) {
                medicalRecordService.deleteRecord(id);
                System.out.println("  ✅ Record deleted.");
            } else {
                System.out.println("  Cancelled.");
            }
        } catch (InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. PRESCRIPTIONS
    // ═══════════════════════════════════════════════════════════════════════

    private static void managePrescriptions() {
        boolean back = false;
        while (!back) {
            System.out.println("""

          ── Prescriptions ─────────────────────────
           1. Add Prescription to Record
           2. List Prescriptions for a Record
           3. List All Prescriptions
           4. Delete Prescription
           0. Back to Main Menu
          ──────────────────────────────────────────""");
            int choice = InputHelper.readIntInRange("  Enter choice: ", 0, 4);
            switch (choice) {
                case 1 -> addPrescription();
                case 2 -> prescriptionsForRecord();
                case 3 -> listAllPrescriptions();
                case 4 -> deletePrescription();
                case 0 -> back = true;
            }
        }
    }

    private static void addPrescription() {
        System.out.println("\n  ── Add Prescription ──");
        try {
            int    recordId     = InputHelper.readPositiveInt("  Medical Record ID : ");
            String medication   = InputHelper.readRequiredString("  Medication        : ");
            String dosage       = InputHelper.readRequiredString("  Dosage            : ");
            String instructions = InputHelper.readRequiredString("  Instructions      : ");

            Prescription rx = medicalRecordService.addPrescription(
                recordId, medication, dosage, instructions);
            System.out.println("  ✅ Prescription added! " + rx.getDisplayInfo());
        } catch (InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void prescriptionsForRecord() {
        System.out.println("\n  ── Prescriptions for Medical Record ──");
        try {
            int id = InputHelper.readPositiveInt("  Record ID: ");
            List<Prescription> list = medicalRecordService.getPrescriptionsForRecord(id);
            if (list.isEmpty()) {
                System.out.println("  (no prescriptions for this record)");
            } else {
                list.forEach(rx -> System.out.println("  " + rx.getDisplayInfo()));
            }
        } catch (InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void listAllPrescriptions() {
        System.out.println("\n  ── All Prescriptions ──");
        List<Prescription> list = medicalRecordService.getAllPrescriptions();
        if (list.isEmpty()) {
            System.out.println("  (no prescriptions)");
        } else {
            list.forEach(rx -> System.out.println("  " + rx.getDisplayInfo()));
            System.out.println("  Total: " + list.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private static void deletePrescription() {
        System.out.println("\n  ── Delete Prescription ──");
        try {
            int id = InputHelper.readPositiveInt("  Prescription ID: ");
            if (InputHelper.confirm("  ⚠️  Delete prescription ID=" + id + "?")) {
                medicalRecordService.deletePrescription(id);
                System.out.println("  ✅ Prescription deleted.");
            } else {
                System.out.println("  Cancelled.");
            }
        } catch (InvalidDataException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
        InputHelper.pressEnterToContinue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BANNER
    // ═══════════════════════════════════════════════════════════════════════

    private static void printBanner() {
        System.out.println("""
          ╔══════════════════════════════════════════════════════╗
          ║                                                      ║
          ║         🏥  CLINIC MANAGEMENT SYSTEM  🏥            ║
          ║                                                      ║
          ║   Java 17  ·  Maven  ·  SQLite  ·  Console UI       ║
          ║                                                      ║
          ╚══════════════════════════════════════════════════════╝
          """);
    }
}
# 🏥 Clinic Management System

A console-based Clinic Management System built with **Java 17**, **Maven**, and **SQLite**.

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installing Maven](#installing-maven)
  - [Windows](#windows)
  - [macOS](#macos)
  - [Linux](#linux)
- [Project Setup](#project-setup)
- [Building & Running](#building--running)
- [Project Structure](#project-structure)
- [Database](#database)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## ✨ Features

- **Patient Management** — Add, search, update, and delete patient records.
- **Doctor Management** — Manage doctor profiles and specializations.
- **Appointment Scheduling** — Book, complete, cancel, and delete appointments.
- **Medical Records** — Maintain patient history with descriptions and diagnoses.
- **Prescriptions** — Attach medications, dosages, and instructions to medical records.
- **Persistent Storage** — All data is saved locally in an SQLite database (`data/clinic.db`).

---

## 🛠 Prerequisites

| Requirement | Version | Download |
|-------------|---------|----------|
| **Java JDK** | 17+ | [Oracle](https://www.oracle.com/java/technologies/downloads/) / [OpenJDK](https://adoptium.net/) |
| **Maven** | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| **Git** | any | [git-scm.com](https://git-scm.com/downloads) |

> Verify your installations:
> ```bash
> java -version
> mvn -version
> git --version
> ```

---

## 📦 Installing Maven

### Windows

1. Download the binary zip from [Maven Downloads](https://maven.apache.org/download.cgi).
2. Extract it to a folder, e.g. `C:\Program Filespache-maven-3.9.x`.
3. Add the `bin` folder to your **System PATH**:
   - `C:\Program Filespache-maven-3.9.xin`
4. Verify:
   ```cmd
   mvn -version
   ```

### macOS

Using **Homebrew** (recommended):
```bash
brew install maven
```

Or manually:
```bash
# Download & extract
curl -O https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /usr/local/

# Add to ~/.zshrc or ~/.bash_profile
export PATH="/usr/local/apache-maven-3.9.6/bin:$PATH"
source ~/.zshrc

mvn -version
```

### Linux (Ubuntu / Debian)

```bash
sudo apt update
sudo apt install maven
mvn -version
```

Or install the latest version manually:
```bash
curl -O https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/

# Add to ~/.bashrc
export PATH="/opt/apache-maven-3.9.6/bin:$PATH"
source ~/.bashrc

mvn -version
```

---

## 🚀 Project Setup

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/clinic-management-system.git
cd clinic-management-system
```

### 2. Verify project structure

```
clinic-management-system/
├── pom.xml
├── data/                    # SQLite database folder (auto-created)
├── src/
│   └── main/
│       └── java/
│           └── com/clinic/
│               ├── database/
│               ├── exception/
│               ├── main/
│               ├── model/
│               ├── repository/
│               ├── service/
│               └── utils/
└── target/                  # Build output (auto-created by Maven)
```

### 3. Build the project

```bash
mvn clean package
```

This will:
- Compile all Java sources.
- Run any tests.
- Package the application into `target/clinic-management-1.0.0.jar`.

---

## ▶️ Building & Running

### Standard run

```bash
java -jar target/*.jar
```

### Run with native access enabled (recommended for Java 17+)

If you see a JVM warning about restricted native methods, start with:

```bash
java --enable-native-access=ALL-UNNAMED -jar target/*.jar
```

---

## 🗄 Database

The application uses **SQLite** with **WAL (Write-Ahead Logging)** mode for better concurrency.

- **Database file:** `data/clinic.db`
- **WAL files:** `data/clinic.db-wal`, `data/clinic.db-shm`

> ⚠️ **Do not delete** the `.db-wal` or `.db-shm` files while the application is running. If the app crashes and leaves stale WAL files, see the [Troubleshooting](#troubleshooting) section.

### Schema

The following tables are created automatically on first run:

| Table | Description |
|-------|-------------|
| `patients` | Patient demographics |
| `doctors` | Doctor profiles & specializations |
| `appointments` | Scheduled appointments with status |
| `medical_records` | Patient visit history |
| `prescriptions` | Medications linked to medical records |

---

## 🐛 Troubleshooting

### `SQLITE_BUSY` — Database file is locked

If you see:
```
java.lang.RuntimeException: Failed to open database connection: [SQLITE_BUSY] ...
```

1. **Kill any hanging Java processes:**
   ```bash
   pkill -f clinic-management
   ```

2. **Remove stale WAL/SHM files:**
   ```bash
   rm -f data/clinic.db-wal data/clinic.db-shm
   ```

3. **Rebuild and run:**
   ```bash
   mvn clean package
   java --enable-native-access=ALL-UNNAMED -jar target/*.jar
   ```

### `ClassNotFoundException: org.sqlite.JDBC`

Ensure the SQLite JDBC dependency is present in `pom.xml`:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

Then rebuild:
```bash
mvn clean package
```

### SLF4J Warnings

The SLF4J NOP warning is harmless. To suppress it, add to `pom.xml`:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.12</version>
</dependency>
```

---

## 📁 Project Structure

```
com.clinic
├── database
│   ├── DBConnection.java          # Singleton SQLite connection manager
│   └── DatabaseInitializer.java   # Schema creation on startup
├── exception
│   ├── InvalidDataException.java
│   ├── PatientNotFoundException.java
│   ├── DoctorNotFoundException.java
│   └── AppointmentNotFoundException.java
├── main
│   └── Main.java                  # Console UI entry point
├── model
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Appointment.java
│   ├── MedicalRecord.java
│   └── Prescription.java
├── repository
│   ├── PatientRepository.java
│   ├── DoctorRepository.java
│   ├── AppointmentRepository.java
│   ├── MedicalRecordRepository.java
│   └── PrescriptionRepository.java
├── service
│   ├── PatientService.java
│   ├── DoctorService.java
│   ├── AppointmentService.java
│   └── MedicalRecordService.java
└── utils
    ├── DateHelper.java
    └── InputHelper.java
```

---

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).

---

> **Happy coding!** 🚀 If you encounter issues, please open a GitHub issue or check the [Troubleshooting](#troubleshooting) section above.

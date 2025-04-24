# 🏢 BTO Management System

The **Build-To-Order (BTO) Management System** is a Java-based console application designed for managing BTO housing applications in Singapore. It serves as a centralized platform for both **applicants** and **HDB staff** to log in, view, apply, and manage BTO project information.

## 📌 Features

- User login and password management
- Role-based access (Applicant, HDB Officer, HDB Manager)
- BTO project creation and management (by Manager)
- Viewing and applying for projects (Applicant)
- Application approval, withdrawal handling, and flat booking (Officer & Manager)
- Enquiry submission, modification, and responses
- Officer registration and approval process
- Report generation with filtering by marriage status, age range, room type, and project
- Use of abstraction and inheritance across user roles


## 🛠️ Tech Stack

- Language: **Java**
- No external libraries required

## 🚀 Getting Started

### 1. Compile the project

Open a terminal and run:

```bash
cd BTOManagementSystem
javac -d bin src/*.java

```

### 2. Run the App

```bash
java -cp bin App

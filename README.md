# VirtualFileVault 💾🔐

A Java Swing-based secure file vault application using MySQL.

## 🔧 Technologies
- Java (JDK 17+)
- Swing GUI
- MySQL
- JDBC

## 🗂️ Project Structure
/VirtualFileVault/
├── /src/ # Java source files
│ ├── Main.java
│ ├── LoginScreen.java
│ ├── VaultDashboard.java
│ ├── CryptoUtil.java
│ ├── FileManager.java
│ └── User.java
├── /vault/ # Encrypted uploaded files
├── /resources/ # Icons/images (optional)
├── users.db # MySQL DB connection config (optional)
├── logs.txt # Activity logs

## 🛠️ Setup Instructions

1. Ensure MySQL server is running.
2. Create a database `virtual_vault` and run:
```sql
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE,
  password VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS files (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  filename VARCHAR(255),
  file_size INT,
  upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  file_url TEXT
);

Update DBConnection.java with your MySQL username & password.

Run Main.java from src/ to start the app.


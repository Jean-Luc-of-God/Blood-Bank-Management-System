-- 1. Create and Select Database
CREATE DATABASE IF NOT EXISTS blood_bank_db;
USE blood_bank_db;

-- 2. Create Donors Table
CREATE TABLE IF NOT EXISTS Donors (
    donor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    blood_type VARCHAR(10) NOT NULL,
    date_registered DATE NOT NULL
);

-- 3. Create BloodUnits Table (Stock)
CREATE TABLE IF NOT EXISTS BloodUnits (
    blood_id INT AUTO_INCREMENT PRIMARY KEY,
    blood_type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    donation_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    donor_id INT,
    FOREIGN KEY (donor_id) REFERENCES Donors(donor_id) ON DELETE SET NULL
);

-- 4. Create BloodRequests Table
CREATE TABLE IF NOT EXISTS BloodRequests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    blood_type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    request_date DATE NOT NULL,
    fulfilled BOOLEAN DEFAULT 0
);

-- 5. Create Alerts Table
CREATE TABLE IF NOT EXISTS Alerts (
    alert_id INT AUTO_INCREMENT PRIMARY KEY,
    blood_id INT,
    alert_type VARCHAR(20) NOT NULL,
    date_generated DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (blood_id) REFERENCES BloodUnits(blood_id) ON DELETE CASCADE
);

-- 6. Create Users Table (Login)
CREATE TABLE IF NOT EXISTS Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'Admin'
);

-- 7. Insert Default Admin User
INSERT IGNORE INTO Users (username, password, role)
VALUES ('admin', 'password123', 'Admin');
```

**SAVE THIS FILE.** This is critical for your exam submission too.

---

### Step 2: The "Nuclear" Reset (Fixes Everything)

Now we tell Docker: *"Delete the broken database and build a perfect one using the script I just saved."*

1.  Open your terminal in the project folder.
2.  Run this command to **delete** the broken container and volume:
    ```bash
    docker-compose down -v
    ```
3.  Run this command to **start fresh**:
    ```bash
    docker-compose up -d
    ```

**Wait 15 seconds** for MySQL to initialize.

### Step 3: Verify & Run

Now, verify everything is there without guessing. Run this command:

```bash
docker exec -it blood_bank_mysql mysql -u JeanLucJava -ppassword123 -e "USE blood_bank_db; SHOW TABLES;"
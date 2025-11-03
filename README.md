📖 Overview

A robust, industry-ready automation testing framework built on Selenium + Java + TestNG, designed to validate critical flows of the JPetStore demo website
.This framework is data-driven, supports cross-browser parallel execution, integrates dynamic Allure reporting with videos, screenshots, logs, and provides a scalable Page Object Factory design for maintainable test automation.

Key highlights:

30+ smoke/regression test cases

Data-driven approach using CSV files

Dynamic Allure reporting (graphs, logs, browser console, env details)

Automatic video clips & screenshots on failures

Cross-browser parallel execution (Chrome, Firefox, Edge)

Scalable Page Object Factory pattern

CI/CD integration with Jenkins

✨ Features

✅ 30+ test cases across login, signup, checkout, owner management, etc.

✅ CSV-based test data (src/test/resources/data/*.csv)

✅ Allure reporting with:

Real-time execution graphs

Console logs

Environment info

Screenshots & videos on failures

✅ Cross-browser execution (Chrome, Firefox)

✅ Page Object Factory → clean, reusable, maintainable

✅ Jenkins CI/CD pipeline ready

🏗 Project Structure
Pets_store_selenium_java/
│── .allure/                 # Allure system files
│── allure-report/           # Generated Allure HTML report
│── allure-results/          # Raw Allure result files
│── screenshots/             # Captured screenshots
│── videos/                  # Test failure video recordings
│── src/
│   ├── main/java/
│   │   ├── factory/         # DriverFactory, Browser options
│   │   └── utils/           # CSV utilities, helpers
│   │
│   └── test/java/
│       ├── base/            # BaseTest setup
│       ├── pages/           # Page Object Factory classes
│       ├── reports/         # TestNG listeners, Allure integrations
│       └── tests/           # Test classes (30+ cases)
│
│── src/test/resources/
│   └── data/                # CSV test data
│       ├── animal.csv
│       ├── find_owner.csv
│       ├── login.csv
│       ├── owner.csv
│       ├── payment.csv
│       ├── signin.csv
│       └── signup.csv
│
│── testng.xml               # TestNG suite configuration
│── pom.xml                  # Maven build + dependencies
│── README.md                # Project documentation
│── target/                  # Build output

⚙️ Tech Stack

Language: Java 17

Framework: TestNG 7.x

Automation: Selenium 4.x

Build Tool: Maven 3.8+

Reports: Allure 2.x

Data: OpenCSV (CSV-driven tests)

CI/CD: Jenkins (with Allure plugin)

🚀 Getting Started
🔹 Prerequisites

Java 17+

Maven 3.8+

Allure Commandline

Chrome + Firefox browsers

Verify setup:

java -version
mvn -version
allure --version

🔹 Clone Repository
git clone https://github.com/raxit00701/JPetStore-Selenium-TestNG-Framework.git
cd JPetStore-Selenium-TestNG-Framework

🔹 Run Tests

Default (Chrome):

mvn clean test


Firefox:

mvn clean test -Dbrowser=firefox


Parallel via TestNG:

mvn clean test -DsuiteXmlFile=testng.xml

🔹 Allure Reports

Generate & open report locally:

allure serve target/allure-results


Generate HTML report:

allure generate target/allure-results -o target/allure-report --clean

📊 Example Data-Driven Test

CSV file (login.csv):

username,password
j2ee,j2ee
testuser1,pass123
testuser2,pass456


Java DataProvider:

@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return CsvUtils.readCsv("src/test/resources/data/login.csv");
}

🔄 Jenkins CI/CD

Configured with Allure Jenkins Plugin

Build step:

mvn clean test


Post-build:

Publish Allure report → target/allure-results

Badge in README auto-updates:


🧑‍💻 Author

👤 Raxit Sharma

💼 QA Automation Engineer Enthusiast

🌐 GitHub Profile





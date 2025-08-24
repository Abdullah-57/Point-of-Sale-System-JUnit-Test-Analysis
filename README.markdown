# Point of Sale System Test Suite

This repository contains a comprehensive test suite for a Point of Sale System. The project focuses on unit testing using JUnit and code coverage analysis with EclEmma, covering components like employee management, inventory handling, and transaction processing.

---

## 🌐 Overview

The test suite includes 178 test cases, evaluating functionalities across multiple classes such as `Employee`, `Item`, `ReturnItem`, `Management`, `POH`, `POSSystem`, `PointOfSale`, `POR`, and `POSTest`. The tests verify critical operations like adding, updating, and deleting employees, inventory management, transaction processing, and error handling for scenarios like file not found and IO exceptions. The system achieved 100% code coverage for `Employee.java`, `Item.java`, and `ReturnItem.java`, with partial coverage for other classes due to complex logic and untested edge cases.

Key features of the repository include:

- JUnit Test Cases: Comprehensive tests for various system components, ensuring robust functionality and error handling.
- EclEmma Coverage Reports: Detailed analysis of code coverage, identifying gaps in complex classes like `POH.java` (76.8%), `Management.java` (90.0%), and `POSSystem.java` (86.7%).
- Test Documentation: A detailed test report with test case descriptions, inputs, expected outputs, actual outputs, and pass/fail statuses.
- Improvement Recommendations: Suggestions for enhancing coverage through expanded test cases, mocking external dependencies, and code refactoring for better testability.

This repository serves as a valuable resource for understanding unit testing, code coverage, and software quality assurance practices in a Java-based POS system.

---

## 🚀 Getting Started

### Prerequisites

🔹 Java Development Kit (JDK) 8 or higher\
🔹 JUnit 4 or 5\
🔹 Eclipse IDE with EclEmma plugin for coverage analysis\
🔹 Maven or Gradle for dependency management (if applicable)

### Installation

- Clone the repository:

  ```bash
  git clone https://github.com/username/employee-management-test-suite.git
  ```
- Navigate to the project directory:

  ```bash
  cd employee-management-test-suite
  ```
- Import the project into Eclipse or your preferred IDE.
- Ensure JUnit and EclEmma are configured in your IDE.

### Running Tests

- Open the project in Eclipse.
- Right-click on the test package (e.g., `test/`) and select **Run As &gt; JUnit Test**.
- To generate coverage reports, use EclEmma:
  - Right-click the project, select **Coverage As &gt; JUnit Test**.
  - View coverage results in the EclEmma Coverage view.

---

## 📁 Project Structure

- **src/main/java**: Contains the source code for the Employee Management System, including classes like `Employee.java`, `Item.java`, `Management.java`, etc.
- **src/test/java**: Contains JUnit test classes, such as `EmployeeManagementTest.java`, `InventoryTest.java`, `PointOfSaleTest.java`, etc.
- **docs/**: Includes the test report (`Test Report.docx`) and coverage screenshots.
- **media/**: Stores images referenced in the test report.

---

## 🔍 Test Suite Overview

The test suite covers the following classes and functionalities: 🔹 **EmployeeManagement**: Tests for adding, updating, deleting employees, and file operations.\
🔹 **Employee**: Tests for constructor, getters, and setters.\
🔹 **Inventory**: Tests for accessing and updating inventory, including error scenarios.\
🔹 **Item**: Tests for item properties and updates.\
🔹 **Management**: Tests for user management and rental operations.\
🔹 **POH/PointOfSale/POR/POSTest**: Tests for POS transaction processing, including item retrieval, deletion, and total calculations.\
🔹 **ReturnItem**: Tests for return item properties.\
🔹 **POSSystem**: Tests for login/logout and file operations.

---

## 📊 Coverage Analysis

🔹 **100% Coverage**: Achieved for `Employee.java`, `Item.java`, and `ReturnItem.java` due to simpler logic and comprehensive test cases.\
🔹 **Partial Coverage**: Classes like `POH.java` (76.8%), `Management.java` (90.0%), and `POSSystem.java` (86.7%) have gaps due to:

- Complex conditional logic
- Untested exception handling
- Unhandled edge cases

**🔹Improvement Plan**:

- Expand test cases for exception handling and edge cases.
- Use mocking frameworks (e.g., Mockito) for external dependencies.
- Refactor complex methods for better testability.

---

## 👨‍💻 Contributors
- **Abdullah Daoud (22I-2626)**  
- **Usman Ali (22I-2725)**  
- **Faizan Rasheed (22I-2734)**

---

## ⚖️ License
This project is for **academic and personal skill development purposes only**.  
Reuse is allowed for **learning and research** with proper credit.

---

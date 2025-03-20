
 
# BASIC Language Parser (Java)

This project is a **Java-based parser** for a BASIC-like programming language. It processes variable assignments, arithmetic expressions, conditionals (`IF` statements), and user input (`INPUT`, `PRINT`).

## 📌 How to Clone & Run This Project

### 🚀 1️⃣ Clone the Repository
Open a **terminal** or **command prompt** and run:
git clone https://github.com/Eashamashud/BASIC-Language-Parser.git
cd BASIC-Language-Parser

### 📌 2️⃣ Ensure Java is Installed
Make sure **Java 8+** is installed on your system:

If Java is not installed:
- **Windows:** Download Java from [Oracle](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or install via Chocolatey:

- **Mac:** Install via Homebrew:

- **Linux (Ubuntu/Debian):**


---

## 📌 3️⃣ Running the Project

### ✅ **Option 1: Run via Terminal (Command Line)**
For those **not using an IDE**, compile and run the project using:
javac -d bin src/*.java java -cp bin Basic path/to/test-file.basic

📌 **Example Usage**:
java -cp bin Basic src/basic_test_file.basic

---

### ✅ **Option 2: Run in IntelliJ IDEA (Recommended)**
1. **Open the project** in IntelliJ IDEA.
2. **Mark `src` as a Sources Root**:
   - In **Project Explorer**, right-click `src` → **"Mark Directory as" → "Sources Root"**.
3. **Edit Run Configurations**:
   - Go to **Run → Edit Configurations**.
   - Under **"Program Arguments"**, paste the **full path** to your test file.
   - Click **Apply**, then **OK**.
4. **Run the Project**:
   - Find `Basic.main()`, then press **Run ▶️**.

---

### ✅ **Option 3: Run in VS Code / Eclipse**
If you're using **VS Code** or **Eclipse**, ensure Java is set up correctly, and run the program with a test file as a **command-line argument**.

For **VS Code**:
- Install the **Java Extension Pack** (if not installed).
- Open the project in VS Code.
- Modify `"args"` in `launch.json`:
"args": ["src/basic_test_file.basic"]

For **Eclipse**:
- Import the project as a **Java Project**.
- Right-click `src`, select **"Build Path" → "Use as Source Folder"**.
- Go to **Run Configurations → Arguments**, add the test file path under **"Program Arguments"**.
- Click **Run**.

---




# VS Code Setup for Spring Boot Project

## Required Extensions

Install the following VS Code extensions for the best Spring Boot development experience:

1. **Extension Pack for Java** (vscjava.vscode-java-pack)
   - Includes: Language Support for Java, Debugger for Java, Test Runner for Java, Maven for Java, Project Manager for Java

2. **Spring Boot Extension Pack** (vscjava.vscode-spring-boot)
   - Includes: Spring Boot Tools, Spring Initializr Java Support, Spring Boot Dashboard

## How to See Run/Debug Buttons

After installing the required extensions:

1. **Open the project** in VS Code:
   ```bash
   code /Users/kiran/Downloads/source/security-clearance-tracker-api
   ```

2. **Wait for Java Language Server** to initialize (you'll see progress in the bottom status bar)

3. **Open the main application file**:
   `src/main/java/com/clearance/tracker/SecurityClearanceTrackerApiApplication.java`

4. **Look for Run/Debug buttons** above the `main` method and class declaration

## Alternative Ways to Run

### Method 1: Command Palette
- Press `Ctrl+Shift+P` (Windows/Linux) or `Cmd+Shift+P` (Mac)
- Type "Java: Run"
- Select your main class

### Method 2: Spring Boot Dashboard
- Open the Spring Boot Dashboard from the Activity Bar (left side)
- Your application should appear in the dashboard
- Click the play button to run

### Method 3: Terminal
```bash
mvn spring-boot:run
```

### Method 4: Debug Panel
- Go to Run and Debug view (Ctrl+Shift+D)
- Select "SecurityClearanceTrackerApiApplication" from the dropdown
- Click the green play button

## Troubleshooting

If you don't see the run/debug buttons:

1. **Check Java installation**:
   ```bash
   java --version
   ```
   Ensure Java 17+ is installed

2. **Reload VS Code window**:
   - `Ctrl+Shift+P` → "Developer: Reload Window"

3. **Check VS Code Java settings**:
   - `Ctrl+,` → Search for "java"
   - Verify `java.home` points to correct JDK

4. **Clean and rebuild**:
   - `Ctrl+Shift+P` → "Java: Rebuild Projects"

5. **Check the Problems panel** for any compilation errors

## Project Structure Verification

Make sure your project structure matches:
```
security-clearance-tracker-api/
├── .vscode/
│   ├── launch.json
│   ├── tasks.json
│   ├── settings.json
│   └── extensions.json
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── clearance/
│                   └── tracker/
│                       └── SecurityClearanceTrackerApiApplication.java
├── pom.xml
└── README.md
```

## Expected Behavior

Once everything is set up correctly, you should see:
- **CodeLens** above the main method showing "Run | Debug"
- **Spring Boot Dashboard** showing your application
- **Run configurations** in the Run and Debug panel
- **Java outline** in the Explorer panel
# digitalbheem

This repository contains two Java desktop example applications demonstrating basic GUI-based features and simple data access patterns.

## Projects

- **LibraryManagementSystem**: A simple library management demo with a Swing GUI supporting basic book and member management.
- **OnlineBankingSystem**: A simple online banking demo with GUI components, DAO classes, and transaction handling.

## Repository layout

```
LibraryManagementSystem/
  lib/    # third-party jars (optional)
  src/    # Java source: com.Library.*
OnlineBankingSystem/
  lib/    # third-party jars (optional)
  src/    # Java source: com.bank.*
```

## Prerequisites

- Java JDK 8 or newer installed and available on `PATH` (`java`, `javac`).
- Optional: An IDE (IntelliJ IDEA, Eclipse) for easier running and debugging.

## Quick start (PowerShell)

**Build & run `LibraryManagementSystem`:**

```powershell
Set-Location 'LibraryManagementSystem'
mkdir -Force bin
# Compile all Java files into bin
Get-ChildItem -Path src -Recurse -Filter '*.java' | ForEach-Object { $_.FullName } | ForEach-Object { javac -d bin -cp "lib/*" $_ }
# Run the main GUI class
java -cp "bin;lib/*" com.Library.LibraryGUI
```

**Build & run `OnlineBankingSystem`:**

```powershell
Set-Location 'OnlineBankingSystem'
mkdir -Force bin
Get-ChildItem -Path src -Recurse -Filter '*.java' | ForEach-Object { $_.FullName } | ForEach-Object { javac -d bin -cp "lib/*" $_ }
java -cp "bin;lib/*" com.bank.Main
```

## Notes

- If you prefer a single-step compile, you can compile all sources at once, but a build tool is recommended for real projects.
- To use third-party jars, place them in the project's `lib/` folder; the commands above include `lib/*` on the classpath.

## Recommended improvements

- Convert each project to use Maven or Gradle for dependency management and reproducible builds.
- Add unit tests and CI (GitHub Actions) to run builds and tests automatically.

## Contributing

- Open an issue or a pull request to suggest improvements.

## License

- No license is included by default. Add a `LICENSE` file if you want to make this project open-source.

## Maintainer

- Add your contact information or GitHub handle here.

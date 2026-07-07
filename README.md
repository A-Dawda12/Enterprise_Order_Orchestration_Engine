# Enterprise Order Orchestration Engine

Enterprise Order Orchestration Engine is a multi-module Java (Spring Boot) project that implements a Camunda-orchestrated order fulfillment platform.

This repository contains several modules (services, clients and an orchestration app) and uses Maven for build and dependency management.

## Quick facts
- Java 21
- Maven multi-module project
- Modules: order-common, channel (clients), services (order, inventory, payment, shipping, notification, fraud), order-orchestration-app

## Local build
From the repository root run the Maven wrapper (recommended):

```powershell
cd C:\Users\adida\Documents\Enterprise_Order_Orchestration_Engine
cmd.exe /c "order-orchestration-app\mvnw.cmd clean install -DskipTests"
```

To run tests remove `-DskipTests`.

## How to initialize and push this repository to GitHub
1. Ensure `git` is installed and configured (user.name and user.email).
2. If the remote repository doesn't exist, create it on GitHub. If you already created it (https://github.com/A-Dawda12/Enterprise_Order_Orchestration_Engine.git), follow the commands below.

Recommended PowerShell commands (replace TOKEN with a GitHub personal access token if not using `gh`):

```powershell
cd C:\Users\adida\Documents\Enterprise_Order_Orchestration_Engine
git init
git checkout -b main
git add .
git commit -m "chore: initial commit - scaffold project"
# If you have GitHub CLI installed:
# gh repo create A-Dawda12/Enterprise_Order_Orchestration_Engine --public --source=. --remote=origin --push
# OR add remote and push manually (will prompt for credentials):
git remote add origin https://github.com/A-Dawda12/Enterprise_Order_Orchestration_Engine.git
git push -u origin main
```

If you prefer to push using a personal access token in the remote URL (less secure because the token appears in shell history), use:

```powershell
git remote set-url origin https://<TOKEN>@github.com/A-Dawda12/Enterprise_Order_Orchestration_Engine.git
git push -u origin main
```

## Next steps / Recommendations
- Add CI (GitHub Actions) to run `mvn -B -ntp -DskipTests=false clean verify` for pull requests and pushes to `main`.
- Add a CONTRIBUTING.md and CODE_OF_CONDUCT.md if this repo will be public.
- Protect the `main` branch and enable branch rules if collaborating.

---

If you'd like, I can:
- add a GitHub Actions CI workflow to the repo now, and
- attempt to run `git` commands and push (if you provide a PAT or authorize via gh), or
- just provide the exact commands for you to run locally.


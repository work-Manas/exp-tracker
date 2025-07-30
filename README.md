# 🧾 Expense Tracker (Console-Based Java Application with DevOps CI/CD)

A **console-based Expense Tracker** built in Java, containerized with Docker, and deployed through a complete **CI/CD DevOps pipeline** using Jenkins, Docker Hub, Ansible, and monitored via Graphite + Grafana.

---

## 📌 Features

- Add, View, Delete, and Analyze expenses from the console
- Track expenses by **category** and **date**
- Metrics instrumentation using **Dropwizard Metrics**
- Grafana dashboard integration using **Graphite**
- Simulated user input with `text.txt` for CLI automation

---

## 🚀 Tech Stack

| Layer           | Tools Used                             |
|----------------|------------------------------------------|
| Language        | Java (JDK 11)                          |
| Build Tool      | Maven                                  |
| Testing         | JUnit                                  |
| CI/CD Pipeline  | Jenkins                                |
| Containerization| Docker, Docker Hub                     |
| Automation      | Ansible                                |
| Monitoring      | Dropwizard Metrics, Graphite, Grafana  |
| OS              | Fedora Linux                           |

---

## 🛠️ Project Structure

```
exp-tracker/
├── src/                         # Java source files
├── pom.xml                     # Maven build file
├── Dockerfile                  # Builds CLI app container
├── Jenkinsfile                 # Jenkins CI/CD pipeline
├── ansible/
│   ├── playbook.yml            # Ansible deployment script
│   └── inventory.ini           # Inventory config
├── text.txt                    # Simulated user input for Docker
└── README.md                   # You’re here!
```

---

## 🔄 CI/CD Pipeline Stages (Jenkins)

1. **Checkout** - Clones repo from GitHub
2. **Build** - Uses Maven to compile and package app
3. **Test** - Runs JUnit test cases
4. **Docker Build** - Builds Docker image
5. **Docker Login** - Authenticates with Docker Hub
6. **Push Image** - Pushes tagged image to Docker Hub
7. **Deploy** - Optional: Uses Ansible to run app with input

---

## 🐳 Dockerized CLI App

Dockerfile (summary):

```Dockerfile
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/expense-tracker-1.0.jar app.jar
COPY text.txt /app/input.txt
CMD ["sh", "-c", "cat /app/input.txt | java -jar app.jar"]
```

Run the container:

```bash
docker run --rm --network host d3wqq/expense-tracker
```

---

## 📊 Grafana Dashboard (Metrics Monitoring)

This project uses [Dropwizard Metrics](https://metrics.dropwizard.io) to monitor:

- `add-expense.duration.mean`
- `delete-expense.count`
- `view-all-expenses.count`
- `add-expense.errors.count`

These are sent to **Graphite** every 60 seconds and visualized using **Grafana**.

---

## 🔐 Ansible Playbook (Deploy CLI App)

Sample deployment:

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

This pulls the Docker image and runs the app using `text.txt` as console input.

---

## 📂 Sample `text.txt`

Used to simulate user input in non-interactive runs:

```
1
Lunch
200
Food
2025-07-06
9
```

---

## 📈 Preview

![Grafana Metrics Dashboard](docs/screenshots/grafana_dashboard.png)

---

## ✅ Future Improvements

- Add a Spring Boot Web UI frontend
- Integrate database for persistent storage
- Deploy with Kubernetes (K8s)
- Add Prometheus + Alertmanager integration

---

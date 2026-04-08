# AI Plant Species Classifier 🌿

A Java native desktop application built with JavaFX and modern UI design (AtlantaFX). This intelligent application allows you to determine the species of plants using an ONNX runtime machine learning classifier.

## ✨ Features
- **Smart Image Classification**: Upload, drag-and-drop, or snap photos from your webcam to identify plants in real-time.
- **Detailed Flora Information**: Backed by a MySQL database, every identified plant brings up rich details including descriptions, care instructions, and toxicity warnings.
- **Persistent Scan History**: Keeps a track of your past 10 scans easily so you never lose an identified plant.
- **Authentication System**: Secure user login and registration system with individual profiles and history storage.
- **Dynamic Theming**: Support for both Light Mode and Dark Mode with dynamic switching capabilities in the Account settings.

## 🛠️ Requirements & Tech Stack
- **Java**: JDK 17 or higher
- **Maven**: For dependency management
- **Database**: MySQL Server 8.0+
- **Machine Learning**: ONNX Runtime
- **UI Frameworks**: JavaFX & AtlantaFX

## 🚀 Setup & Installation

### 1. Database Setup
The application natively handles database creation on the first run. However, you must have an active MySQL server running locally on port `3306`.
Ensure your MySQL credentials match what's configured in `src/main/java/classifier/DatabaseManager.java`:
- **Default Database**: `plant_db`
- **Username**: `your_mysql_username`
- **Password**: `your_mysql_password`

### 2. Build the Application
Ensure you have cloned the repository and install the dependencies utilizing Maven:
```bash
mvn clean install
```

### 3. Run the Application
You can easily launch the local JavaFX app using the Maven JavaFX plugin:
```bash
mvn javafx:run
```

## 📸 Usage
1. First, create an account using the **Sign Up** feature or use the default setup (Username: `admin`, Password: `password123`).
2. Navigate to the **Classifier** using the graphical side menu.
3. Bring in a plant picture locally or via a webcam snapshot.
4. Review the returned details natively fetched from the MySQL Plant tables!

## 📄 License
This project is open-source. Please feel free to fork it, create feature branches, and submit PRs!

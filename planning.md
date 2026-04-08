# Planning: Plant Classifier Desktop App (JavaFX)

## 1. Project Overview
A modern, high-performance desktop application built with **JavaFX** and **Antigravity**. The app will allow users to upload or capture a photo of a plant and use a local **YOLOv11 ONNX** model to predict the species instantly.

## 2. Tech Stack
* **Language:** Java 21+
* **UI Framework:** JavaFX (OpenJFX)
* **Inference Engine:** ONNX Runtime (Java API)
* **Build Tool:** Maven or Gradle
* **Styling:** CSS (Modern "Fluent" or "Material" look)

## 3. Core Features
- [ ] **Image Upload:** Drag-and-drop or file picker for plant images.
- [ ] **Real-time Preview:** Display the selected image in a polished `ImageView`.
- [ ] **Local Inference:** Load `best.onnx` and run prediction on the CPU.
- [ ] **Result Dashboard:** Show the Top-1 species name and a confidence progress bar.
- [ ] **History Log:** (Optional) Keep a list of recent predictions during the session.

## 4. UI/UX Design Goals
* **Clean Layout:** Sidebar for controls, large central area for image preview.
* **Responsive:** Use `BorderPane` and `GridPane` to ensure the app looks good on all screen sizes.
* **Visual Feedback:** Loading spinners while the model is "thinking" (even if it's fast).
* **Styling:** Dark/Light mode support using CSS.

## 5. Technical Implementation Steps

### Phase 1: Environment Setup
1.  Initialize a JavaFX project in Antigravity.
2.  Add dependencies to `pom.xml` (or `build.gradle`):
    * `org.openjfx` (JavaFX Graphics/Controls/FXML)
    * `com.microsoft.onnxruntime:onnxruntime` (For model execution)
    * `org.imgscalr:imgscalr-lib` (For easy image resizing to 224x224)

### Phase 2: AI Logic (The ONNX Wrapper)
1.  Create a `PlantPredictor` class.
2.  Implement `loadModel(String path)` using `OrtEnvironment`.
3.  Implement `preprocess(File imageFile)`:
    * Resize to 224x224.
    * Convert to Float array.
    * Normalize values (0.0 to 1.0).
4.  Implement `predict()`:
    * Pass the float tensor to ONNX.
    * Parse the output array to find the index with the highest probability.
    * Map the index to the 47 plant species names.

### Phase 3: UI Development
1.  Build the main layout in FXML or pure Java.
2.  Add a "Select Image" button with a `FileChooser`.
3.  Style the app with a modern CSS theme (e.g., `JMetro` or custom CSS).

### Phase 4: Integration
1.  Connect the UI button to the `PlantPredictor`.
2.  Ensure prediction runs on a background thread (`Task`) so the UI doesn't freeze.
3.  Display the result in a styled `Label`.

## 6. Project Structure
```text
/src
  /main
    /java/com/plantapp
      - Main.java (App Entry)
      - Controller.java (UI Logic)
      - ModelInference.java (ONNX Logic)
    /resources
      - style.css
      - layout.fxml
      - best.onnx (The model file)
      - labels.txt (The 47 species names)
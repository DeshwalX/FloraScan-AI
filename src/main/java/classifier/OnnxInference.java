package classifier;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import org.imgscalr.Scalr;
import java.util.Collections;
import java.util.Map;

public class OnnxInference {

    private final OrtEnvironment env;
    private final OrtSession session;
    private final Map<String, String> speciesMap;

    public OnnxInference() throws Exception {
        // Initialize environment and session
        env = OrtEnvironment.getEnvironment();
        File modelFile = new File("models/best.onnx");
        if (!modelFile.exists()) {
            throw new RuntimeException("Model file not found at " + modelFile.getAbsolutePath());
        }
        session = env.createSession(modelFile.getPath(), new OrtSession.SessionOptions());

        // Load species map
        Gson gson = new Gson();
        File jsonFile = new File("species.json");
        if (!jsonFile.exists()) {
             throw new RuntimeException("species.json not found at " + jsonFile.getAbsolutePath());
        }
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        try (FileReader reader = new FileReader(jsonFile)) {
            speciesMap = gson.fromJson(reader, type);
        }
    }

    public static class Prediction {
        public String species;
        public float confidence;
        public Prediction(String species, float confidence) {
            this.species = species;
            this.confidence = confidence;
        }
    }

    public Prediction predict(String imagePath) throws Exception {
        float[] inputData = preprocessImage(imagePath);
        float[][][][] multiData = new float[1][3][224][224];
        
        int idx = 0;
        for (int c = 0; c < 3; c++) {
            for (int h = 0; h < 224; h++) {
                for (int w = 0; w < 224; w++) {
                    multiData[0][c][h][w] = inputData[idx++];
                }
            }
        }

        try (OnnxTensor tensor = OnnxTensor.createTensor(env, multiData)) {
            String inputName = session.getInputNames().iterator().next();
            OrtSession.Result finalResult = session.run(Collections.singletonMap(inputName, tensor));
            
            float[][] output = (float[][]) finalResult.get(0).getValue();
            finalResult.close();

            int maxIndex = -1;
            float maxProb = -Float.MAX_VALUE;
            // Assuming output shape [1, num_classes]
            for (int i = 0; i < output[0].length; i++) {
                if (output[0][i] > maxProb) {
                    maxProb = output[0][i];
                    maxIndex = i;
                }
            }

            // Normalizing logits using softmax to get confidence
            float sumExp = 0.0f;
            for (int i = 0; i < output[0].length; i++) {
                sumExp += Math.exp(output[0][i]);
            }
            float confidence = (float) (Math.exp(maxProb) / sumExp);

            String speciesLabel = speciesMap.getOrDefault(String.valueOf(maxIndex), "Unknown (" + maxIndex + ")");
            return new Prediction(speciesLabel, confidence);
        }
    }

    private float[] preprocessImage(String imagePath) throws Exception {
        BufferedImage originalImage = ImageIO.read(new File(imagePath));
        if (originalImage == null) {
            throw new Exception("Could not read image: " + imagePath);
        }

        BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, 224, 224);

        // Convert to float array [3, 224, 224], normalize to 0-1
        // Used ImageNet standard normalization: mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]
        float[] result = new float[3 * 224 * 224];
        
        int rOffset = 0;
        int gOffset = 224 * 224;
        int bOffset = 2 * 224 * 224;

        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int rgb = resizedImage.getRGB(x, y);
                float r = ((rgb >> 16) & 0xFF) / 255.0f;
                float g_c = ((rgb >> 8) & 0xFF) / 255.0f;
                float b = (rgb & 0xFF) / 255.0f;

                result[rOffset++] = (r - 0.485f) / 0.229f;
                result[gOffset++] = (g_c - 0.456f) / 0.224f;
                result[bOffset++] = (b - 0.406f) / 0.225f;
            }
        }
        return result;
    }
}

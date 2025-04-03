package com.utc2.facilityui.chatbot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;

public class ChatbotController {

    @FXML private TextField userInput;
    @FXML private TextArea chatOutput;
    @FXML private Button sendButton;

    private final GeminiAPIClient geminiClient = new GeminiAPIClient();

    @FXML
    private void sendMessage() {
        String inputText = userInput.getText().trim();
        if (inputText.isEmpty()) return;

        chatOutput.appendText("Bạn: " + inputText + "\n");
        userInput.clear();

        new Thread(() -> {
            try {
                String response = geminiClient.askGemini(inputText);
                chatOutput.appendText("Chatbot: " + response + "\n");
            } catch (IOException e) {
                chatOutput.appendText("Lỗi: Không thể kết nối đến chatbot.\n");
            }
        }).start();
    }
}
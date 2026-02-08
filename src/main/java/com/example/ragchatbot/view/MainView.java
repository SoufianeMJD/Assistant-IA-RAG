package com.example.ragchatbot.view;

import com.example.ragchatbot.service.ChatService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AppShellConfigurator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.util.*;

@Route("")
@PageTitle("RAG Chatbot")
public class MainView extends VerticalLayout implements AppShellConfigurator {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final ChatService chatService;

    private final Select<String> fileSelector;
    private final VerticalLayout chatHistory;
    private final String sessionId;

    public MainView(VectorStore vectorStore, JdbcTemplate jdbcTemplate, ChatService chatService) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.chatService = chatService;
        this.sessionId = UUID.randomUUID().toString();

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("RAG Chatbot");

        Upload uploadComponent = createUploadComponent();

        fileSelector = new Select<>();
        fileSelector.setLabel("Select Documents for Context");
        fileSelector.setPlaceholder("Choose files...");
        fileSelector.setWidth("400px");
        refreshFileList();

        chatHistory = new VerticalLayout();
        chatHistory.setSpacing(true);
        chatHistory.setPadding(true);
        chatHistory.setWidth("100%");

        Scroller scroller = new Scroller(chatHistory);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setSizeFull();

        TextField questionField = new TextField();
        questionField.setPlaceholder("Type your question...");
        questionField.setWidth("100%");

        Button sendButton = new Button("Send");
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout inputLayout = new HorizontalLayout(questionField, sendButton);
        inputLayout.setWidth("100%");
        inputLayout.setFlexGrow(1, questionField);

        sendButton.addClickListener(e -> {
            String question = questionField.getValue();
            if (question != null && !question.trim().isEmpty()) {
                handleUserQuestion(question);
                questionField.clear();
            }
        });

        questionField.addKeyPressListener(e -> {
            if (e.getKey().getKeys().contains("Enter")) {
                String question = questionField.getValue();
                if (question != null && !question.trim().isEmpty()) {
                    handleUserQuestion(question);
                    questionField.clear();
                }
            }
        });

        add(title, uploadComponent, fileSelector, scroller, inputLayout);
        setFlexGrow(1, scroller);
    }

    private Upload createUploadComponent() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.setAcceptedFileTypes(".txt", ".md", ".pdf");
        upload.setMaxFiles(10);
        upload.setDropLabel(new Span("Drop files here or click to upload (PDF, TXT, MD)"));

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream(fileName);

            try {
                TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(inputStream));
                List<Document> documents = reader.get();

                TokenTextSplitter splitter = new TokenTextSplitter();
                List<Document> chunks = splitter.apply(documents);

                for (Document chunk : chunks) {
                    Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());
                    metadata.put("source", fileName);
                    chunk = Document.builder()
                            .withId(chunk.getId())
                            .withContent(chunk.getContent())
                            .withMetadata(metadata)
                            .build();
                    chunks.set(chunks.indexOf(chunk), chunk);
                }

                vectorStore.accept(chunks);

                Notification notification = Notification.show(
                        "File '" + fileName + "' uploaded and processed successfully!",
                        3000,
                        Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                refreshFileList();

            } catch (Exception ex) {
                Notification notification = Notification.show(
                        "Error processing file: " + ex.getMessage(),
                        5000,
                        Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        return upload;
    }

    private void refreshFileList() {
        try {
            List<String> files = jdbcTemplate.query(
                    "SELECT DISTINCT metadata->>'source' as source FROM vector_store WHERE metadata->>'source' IS NOT NULL",
                    (rs, rowNum) -> rs.getString("source"));

            fileSelector.setItems(files);

        } catch (Exception e) {
            Notification.show("Error loading file list: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void handleUserQuestion(String question) {
        addMessageToChat("You", question, "user-message");

        UI ui = getUI().orElse(null);
        if (ui == null)
            return;

        ui.access(() -> {
            try {
                Set<String> selectedFiles = new HashSet<>();
                String selectedFile = fileSelector.getValue();
                if (selectedFile != null && !selectedFile.isEmpty()) {
                    selectedFiles.add(selectedFile);
                }

                ChatResponse response = chatService.ask(question, sessionId, selectedFiles);
                String answer = response.getResult().getOutput().getContent();

                addMessageToChat("AI", answer, "ai-message");

            } catch (Exception e) {
                addMessageToChat("Error", "Failed to get response: " + e.getMessage(), "error-message");
            }
        });
    }

    private void addMessageToChat(String sender, String message, String styleClass) {
        Div messageDiv = new Div();
        messageDiv.addClassName(styleClass);
        messageDiv.getStyle()
                .set("padding", "10px")
                .set("margin", "5px 0")
                .set("border-radius", "8px")
                .set("max-width", "80%");

        if ("user-message".equals(styleClass)) {
            messageDiv.getStyle()
                    .set("background-color", "#e3f2fd")
                    .set("align-self", "flex-end")
                    .set("margin-left", "auto");
        } else if ("ai-message".equals(styleClass)) {
            messageDiv.getStyle()
                    .set("background-color", "#f5f5f5")
                    .set("align-self", "flex-start");
        } else {
            messageDiv.getStyle()
                    .set("background-color", "#ffebee")
                    .set("align-self", "flex-start");
        }

        Paragraph senderLabel = new Paragraph(sender + ":");
        senderLabel.getStyle().set("font-weight", "bold").set("margin", "0");

        Paragraph messageText = new Paragraph(message);
        messageText.getStyle().set("margin", "5px 0 0 0");

        messageDiv.add(senderLabel, messageText);
        chatHistory.add(messageDiv);

        messageDiv.getElement().executeJs("this.scrollIntoView({behavior: 'smooth', block: 'end'})");
    }
}

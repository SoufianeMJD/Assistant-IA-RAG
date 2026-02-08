# Project Structure

```
rag-chatbot/
├── compose.yaml                                    # Docker Compose for PostgreSQL
├── pom.xml                                         # Maven dependencies
├── README.md                                       # Documentation complète
├── SETUP_GEMINI.md                                 # Guide de démarrage rapide
├── .gitignore                                      # Git exclusions
│
└── src/main/
    ├── java/com/example/ragchatbot/
    │   ├── RagChatbotApplication.java             # Spring Boot entry point
    │   ├── service/
    │   │   └── ChatService.java                   # RAG logic with Spring AI
    │   └── view/
    │       └── MainView.java                      # Vaadin UI
    │
    └── resources/
        └── application.properties                  # Configuration
```

## Files Created

1. **pom.xml** - Maven configuration with Google AI Gemini starter
2. **compose.yaml** - PostgreSQL with pgvector
3. **application.properties** - Gemini API key configuration
4. **RagChatbotApplication.java** - Main application class
5. **ChatService.java** - RAG service with conversation memory and vector search
6. **MainView.java** - Complete Vaadin UI with upload, selection, and chat
7. **README.md** - Full documentation
8. **SETUP_GEMINI.md** - Quick start guide
9. **.gitignore** - Standard exclusions

## Key Features Implemented

### Backend (ChatService)
-  Spring AI ChatClient with Google AI Gemini
-  InMemoryChatMemory for conversation history
-  QuestionAnswerAdvisor for RAG
-  Dynamic filtering by source files
-  SimpleLoggerAdvisor for debugging

### Frontend (MainView)
-  Multi-file upload with Vaadin Upload component
-  TikaDocumentReader for PDF/TXT/MD parsing
-  TokenTextSplitter for chunking
-  Metadata tagging with source filename
-  Select component for file filtering
-  Styled chat interface with message history
-  Real-time UI updates

### Database
-  PostgreSQL 16 with pgvector extension
-  Automatic schema initialization
-  HNSW index for fast similarity search
-  COSINE_DISTANCE metric
-  1536 dimensions (compatible embeddings)

## Next Steps

1. **Get your Gemini API key**:
   - Visit [Google AI Studio](https://aistudio.google.com/apikey)
   - Create API key

2. **Set environment variable**:
   ```powershell
   $env:GEMINI_API_KEY="your-key-here"
   ```

3. **Start PostgreSQL**:
   ```bash
   docker-compose up -d
   ```

4. **Run the application**:
   ```bash
   mvn clean spring-boot:run
   ```

5. **Open in browser**: http://localhost:8080

## Code Quality

-  All imports are correct and complete
-  Proper Spring Boot 3 annotations
-  Standard Maven project structure
-  Ready to compile and run
-  No placeholders or TODOs
-  Works with Gemini 2.0 Flash

## Current Configuration

- **AI Provider**: Google AI Studio (Gemini)
- **Model**: gemini-2.0-flash-exp
- **Authentication**: API Key (simple)
- **Vector Store**: PostgreSQL pgvector
- **UI Framework**: Vaadin 24.3.0

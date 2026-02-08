# RAG Chatbot - Spring Boot 3 + Vaadin + Spring AI

Un chatbot RAG (Retrieval Augmented Generation) complet permettant d'uploader des documents et de discuter avec leur contenu.

## 🎯 Fonctionnalités

- **Upload de documents** : PDF, TXT, MD
- **Vectorisation automatique** : Documents stockés dans PostgreSQL avec pgvector
- **Chat intelligent** : Interface conversationnelle avec mémoire de session
- **Filtrage par source** : Sélection des documents à utiliser comme contexte
- **Interface moderne** : UI Vaadin responsive

## 🛠️ Stack Technique

- **Java 17+**
- **Spring Boot 3.2.1**
- **Vaadin 24.3.0** (Flow)
- **Spring AI 1.0.0-M4** (Google AI Gemini provider)
- **PostgreSQL 16** avec extension pgvector
- **Apache Tika** (via Spring AI Tika Document Reader)

## 📋 Prérequis

- Java 17 ou supérieur
- Maven 3.8+
- Docker et Docker Compose
- Clé API Google AI Studio (Gemini)

## 🚀 Installation et Démarrage

### 1. Cloner le projet

```bash
cd "e:\Desktop Files\SDIA\S3\GenAI\Last Lab"
```

### 2. Configurer la clé API Gemini

Obtenez votre clé API sur [Google AI Studio](https://aistudio.google.com/apikey)

**Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="votre-clé-api-ici"
```

**Windows (CMD):**
```cmd
set GEMINI_API_KEY=votre-clé-api-ici
```

**Linux/Mac:**
```bash
export GEMINI_API_KEY="votre-clé-api-ici"
```

### 3. Démarrer PostgreSQL avec Docker Compose

```bash
docker-compose up -d
```

Cela démarre PostgreSQL avec l'extension pgvector sur le port 5432.

### 4. Lancer l'application

```bash
mvn clean spring-boot:run
```

L'application sera accessible sur : **http://localhost:8080**

## 📖 Utilisation

### 1. Upload de documents

1. Cliquez sur la zone d'upload ou glissez-déposez vos fichiers (PDF, TXT, MD)
2. Les documents sont automatiquement :
   - Lus avec Apache Tika
   - Découpés en chunks
   - Vectorisés et stockés dans PostgreSQL

### 2. Sélection des sources

- Utilisez la liste déroulante "Select Documents for Context"
- Choisissez le(s) document(s) à utiliser comme contexte pour vos questions

### 3. Chat

1. Tapez votre question dans le champ de texte
2. Appuyez sur "Send" ou Entrée
3. L'IA répond en se basant sur les documents sélectionnés

## 🏗️ Architecture

```
src/main/java/com/example/ragchatbot/
├── RagChatbotApplication.java          # Point d'entrée Spring Boot
├── service/
│   └── ChatService.java                # Logique RAG avec Spring AI
└── view/
    └── MainView.java                   # Interface Vaadin
```

### Composants Clés

#### ChatService
- Configuration du `ChatClient` avec advisors :
  - `MessageChatMemoryAdvisor` : Mémoire de conversation
  - `QuestionAnswerAdvisor` : RAG avec VectorStore
  - `SimpleLoggerAdvisor` : Logs pour debugging
- Filtrage dynamique par métadonnées (source)

#### MainView
- Upload de fichiers avec `TikaDocumentReader`
- Découpage avec `TokenTextSplitter`
- Sélection de sources via `Select` component
- Interface de chat avec historique stylisé

## ⚙️ Configuration

Voir `src/main/resources/application.properties` pour :

- Configuration Google AI Gemini (clé API, modèle)
- Paramètres PGVector (dimensions, index type, distance)
- Configuration Docker Compose

### Modèles Gemini disponibles

- `gemini-2.0-flash-exp` (par défaut) - Dernier modèle expérimental
- `gemini-1.5-flash` - Rapide et efficace
- `gemini-1.5-pro` - Plus puissant pour des tâches complexes
- `gemini-1.0-pro` - Version stable

## 🔧 Dépendances Principales

```xml
<!-- Spring AI - Google AI Gemini -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-google-ai-gemini-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-tika-document-reader</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
</dependency>

<!-- Vaadin -->
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-spring-boot-starter</artifactId>
</dependency>
```

## 🐛 Debugging

Les logs sont activés via `SimpleLoggerAdvisor` dans le ChatService. Vérifiez la console pour :
- Requêtes envoyées à Google AI Gemini
- Résultats de recherche vectorielle
- Erreurs de traitement

## 📚 Ressources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Google AI Studio](https://aistudio.google.com/)
- [Gemini API Documentation](https://ai.google.dev/docs)
- [Vaadin Documentation](https://vaadin.com/docs/latest)
- [PGVector Documentation](https://github.com/pgvector/pgvector)

## 🔐 Sécurité

⚠️ **Important** : Ne versionnez JAMAIS votre clé API Gemini !

Ajoutez à votre `.gitignore` :
```
.env
```

## 📝 Notes

- Le schéma de la base de données est créé automatiquement (`initialize-schema=true`)
- Les embeddings utilisent 1536 dimensions (modèle compatible)
- La mémoire de conversation est en RAM (`InMemoryChatMemory`)
- Gemini 2.0 Flash offre un excellent rapport performance/prix

---

**Développé avec ❤️ pour le cours GenAI - S3 SDIA**

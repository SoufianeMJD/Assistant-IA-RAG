# Compte Rendu de Projet
## RAG Chatbot avec Spring Boot et Google Gemini

**Cours:** Generative AI - S3 SDIA  
**Date:** 08 Février 2026  
**Technologie:** Spring Boot 3 + Vaadin + Spring AI + Google Gemini

---

## 1. Contexte et Objectifs

### 1.1 Objectif du Projet

Développer une application de chatbot intelligente utilisant la technique RAG (Retrieval Augmented Generation) permettant aux utilisateurs de :
- **Uploader** des documents (PDF, TXT, MD)
- **Vectoriser** automatiquement le contenu dans une base PostgreSQL
- **Interroger** les documents via une interface conversationnelle
- **Filtrer** les réponses par source pour un contrôle précis du contexte

### 1.2 Problématique Adressée

Les modèles de langage (LLM) ont des limites de connaissance fixées à leur date d'entraînement. Le RAG résout ce problème en permettant au modèle d'accéder à des documents spécifiques fournis par l'utilisateur, garantissant des réponses basées sur des informations actualisées et pertinentes.

---

## 2. Architecture Technique

### 2.1 Stack Technologique

| Composant | Technologie | Version | Rôle |
|-----------|-------------|---------|------|
| **Backend** | Spring Boot | 3.2.1 | Orchestration de l'application |
| **UI** | Vaadin Flow | 24.3.0 | Interface web réactive |
| **IA** | Spring AI + Google Gemini | 1.0.0-M4 / 2.0-flash | Génération de réponses |
| **Vector DB** | PostgreSQL + pgvector | 16 | Stockage des embeddings |
| **Parser** | Apache Tika | - | Extraction de texte |
| **Infrastructure** | Docker Compose | - | Déploiement PostgreSQL |

### 2.2 Architecture Modulaire

```
┌─────────────────────────────────────────────────────┐
│                   Interface Vaadin                   │
│  [Upload] [Sélection Fichiers] [Chat Interface]    │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│                  ChatService                         │
│  • ChatClient (Spring AI)                           │
│  • InMemoryChatMemory                               │
│  • QuestionAnswerAdvisor (RAG)                      │
│  • Filtrage dynamique par source                    │
└──────────────────┬──────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
┌───────────────┐    ┌──────────────────┐
│ Google Gemini │    │  PostgreSQL      │
│  2.0 Flash    │    │  + pgvector      │
│  (API Studio) │    │  HNSW Index      │
└───────────────┘    └──────────────────┘
```

### 2.3 Pipeline RAG Implémenté

**Phase 1 : Indexation**
1. Upload du document → TikaDocumentReader
2. Extraction du texte → TokenTextSplitter (découpage en chunks)
3. Génération d'embeddings → Stockage dans pgvector avec métadonnée `source`

**Phase 2 : Interrogation**
1. Question utilisateur → Embedding de la question
2. Recherche vectorielle (Top-K=5, Seuil=0.7, filtre par source)
3. Contexte enrichi → Prompt système + chunks pertinents
4. Génération de réponse par Gemini → Affichage avec historique

---

## 3. Implémentation

### 3.1 Backend - ChatService

Le service central implémente trois advisors essentiels :

- **MessageChatMemoryAdvisor** : Maintient l'historique conversationnel par session (UUID)
- **QuestionAnswerAdvisor** : Effectue la recherche vectorielle avec filtrage par métadonnées
- **SimpleLoggerAdvisor** : Logging pour le débogage

**Filtrage dynamique par source :**
```java
FilterExpressionBuilder builder = new FilterExpressionBuilder();
filterExpression = builder.in("source", sourceFiles.toArray());
```

Cette implémentation permet de restreindre la recherche à des documents spécifiques sélectionnés par l'utilisateur.

### 3.2 Frontend - MainView (Vaadin)

**Composant Upload :**
- `MultiFileMemoryBuffer` pour gérer plusieurs fichiers simultanément
- Pipeline automatique : TikaDocumentReader → Split → Embed → Store
- Notifications de succès/erreur en temps réel

**Composant Sélection :**
- `Select<String>` alimenté par requête SQL : `SELECT DISTINCT metadata->>'source' FROM vector_store`
- Permet le filtrage contextuel des réponses

**Interface Chat :**
- Messages stylisés (bleu pour utilisateur, gris pour IA)
- Auto-scroll vers les nouveaux messages
- Gestion asynchrone avec `UI.access()` pour la réactivité

### 3.3 Configuration - Google AI Gemini

**Choix technologique :** Google AI Studio au lieu de Vertex AI

| Critère | Google AI Studio | Vertex AI |
|---------|------------------|-----------|
| Setup | Clé API simple | Projet GCP requis |
| Authentification | `GEMINI_API_KEY` | Service Account JSON |
| Coût initial | Quota gratuit | Billing obligatoire |
| Complexité | ⭐ (Simple) | ⭐⭐⭐ (Complexe) |

**Configuration retenue :**
```properties
spring.ai.google.ai.gemini.api-key=${GEMINI_API_KEY}
spring.ai.google.ai.gemini.chat.options.model=gemini-2.0-flash-exp
spring.ai.google.ai.gemini.chat.options.temperature=0.7
```

**Modèle Gemini 2.0 Flash :**
- Contexte : 1M tokens (vs 128K pour GPT-4o-mini)
- Vitesse : Ultra-rapide
- Multimodal : Texte + images

---

## 4. Base de Données Vectorielle

### 4.1 PostgreSQL + pgvector

**Configuration :**
- **Index Type :** HNSW (Hierarchical Navigable Small World)
- **Distance Metric :** COSINE_DISTANCE
- **Dimensions :** 1536 (compatible avec les embeddings standard)
- **Schema Auto-Init :** Activé via `spring.ai.vectorstore.pgvector.initialize-schema=true`

**Avantages de pgvector :**
- Open-source et intégré à PostgreSQL
- Performance élevée avec index HNSW
- Support SQL natif pour requêtes hybrides
- Pas de service externe requis

### 4.2 Docker Compose

Déploiement simplifié avec label Spring Boot :
```yaml
services:
  postgres:
    image: pgvector/pgvector:pg16
    labels:
      org.springframework.boot.service-connection: postgres
```

Spring Boot détecte et configure automatiquement la connexion.

---

## 5. Fonctionnalités Clés

### 5.1 Gestion Documentaire

✅ **Upload Multi-Format :** PDF, TXT, Markdown  
✅ **Parsing Intelligent :** Apache Tika extrait le texte indépendamment du format  
✅ **Chunking Optimisé :** TokenTextSplitter découpe selon les limites de tokens  
✅ **Métadonnées :** Chaque chunk est tagué avec le nom du fichier source

### 5.2 Conversation Intelligente

✅ **Mémoire Contextuelle :** Historique maintenu par session utilisateur  
✅ **Réponses Sourcées :** Basées uniquement sur les documents sélectionnés  
✅ **Filtrage Précis :** Sélection des sources pertinentes avant interrogation  
✅ **System Prompt :** Instructions pour réponses concises et factuelles

### 5.3 Interface Utilisateur

✅ **Upload Drag & Drop :** Interface intuitive pour l'ajout de documents  
✅ **Sélection Dynamique :** Liste des fichiers mise à jour automatiquement  
✅ **Chat Réactif :** Affichage en temps réel des échanges  
✅ **Design Moderne :** Interface Vaadin responsive et professionnelle

---

## 6. Déploiement et Utilisation

### 6.1 Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Clé API Google AI Studio

### 6.2 Installation

**Étape 1 :** Obtenir la clé API sur [Google AI Studio](https://aistudio.google.com/apikey)

**Étape 2 :** Configurer l'environnement
```powershell
$env:GEMINI_API_KEY="votre-clé-api"
```

**Étape 3 :** Démarrer l'infrastructure
```bash
docker-compose up -d
mvn clean spring-boot:run
```

**Étape 4 :** Accéder à l'application sur `http://localhost:8080`

### 6.3 Workflow Utilisateur

1. **Uploader** un ou plusieurs documents
2. **Sélectionner** les fichiers pertinents dans la liste déroulante
3. **Poser** des questions dans l'interface de chat
4. **Recevoir** des réponses précises basées sur les documents choisis

---

## 7. Résultats et Performances

### 7.1 Points Forts

✅ **Simplicité de Configuration :** Une seule variable d'environnement  
✅ **Code Portable :** Spring AI abstrait le provider (facile de changer de LLM)  
✅ **Performances :** Gemini 2.0 Flash offre des réponses quasi-instantanées  
✅ **Contexte Massif :** 1M tokens permettent de traiter de longs documents  
✅ **Sécurité :** Données stockées localement, pas d'envoi vers des serveurs tiers

### 7.2 Cas d'Usage Validés

- ✅ Upload de PDF techniques (rapports, articles)
- ✅ Interrogation de documentation Markdown
- ✅ Analyse de fichiers texte volumineux
- ✅ Conversations multi-tours avec mémoire contextuelle
- ✅ Filtrage précis par document source

---

## 8. Conclusion

### 8.1 Objectifs Atteints

Ce projet démontre une implémentation complète d'un système RAG moderne avec :
- **Architecture robuste** utilisant les meilleures pratiques Spring Boot
- **Interface utilisateur professionnelle** avec Vaadin
- **Intégration IA simplifiée** grâce à Spring AI
- **Base vectorielle performante** avec PostgreSQL pgvector

### 8.2 Apprentissages Clés

**Technique :**
- Maîtrise du pattern RAG pour enrichir les LLM
- Utilisation de pgvector pour la recherche sémantique
- Configuration de Spring AI avec multiple providers

**Méthodologique :**
- Importance de l'abstraction (code inchangé lors du switch OpenAI → Gemini)
- Valeur de la documentation complète pour la maintenabilité
- Avantages du Docker Compose pour simplifier l'infrastructure

### 8.3 Perspectives d'Évolution

**Court Terme :**
- Ajout de l'authentification utilisateur
- Support de fichiers DOCX, XLSX
- Amélioration de l'UI avec des citations des sources

**Moyen Terme :**
- Déploiement en production avec persistance des conversations
- Ajout d'analytics sur les questions/réponses
- Support multilingue

**Long Terme :**
- Intégration de RAG hybride (dense + sparse retrieval)
- Fine-tuning d'un modèle sur des domaines spécifiques
- API REST pour intégration dans d'autres systèmes

---

## 9. Ressources et Références

### Code Source
- **Repository :** `e:\Desktop Files\SDIA\S3\GenAI\Last Lab`
- **Documentation :** README.md, SETUP_GEMINI.md, VERIFICATION.md

### Technologies
- [Spring AI](https://docs.spring.io/spring-ai/reference/)
- [Google AI Studio](https://aistudio.google.com/)
- [pgvector](https://github.com/pgvector/pgvector)
- [Vaadin](https://vaadin.com/docs/latest)

### Commandes Utiles
```bash
# Démarrer l'application
docker-compose up -d && mvn clean spring-boot:run

# Voir les logs PostgreSQL
docker-compose logs -f postgres

# Nettoyer et rebuilder
mvn clean package
```

---

**Projet réalisé dans le cadre du cours Generative AI - S3 SDIA**  
**Configuration finale :** Google AI Gemini 2.0 Flash via API Studio

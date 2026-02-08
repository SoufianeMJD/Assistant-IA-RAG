# Migration OpenAI → Google AI Gemini

Ce document décrit la migration de OpenAI vers **Google AI Studio (Gemini)** avec authentification par clé API.

> **Note**: Ce projet utilise maintenant Google AI Studio au lieu de Vertex AI pour une configuration simplifiée.

---

## 📝 Changements Effectués

### 1. Dépendances Maven (`pom.xml`)

**Avant (OpenAI) :**
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

**Maintenant (Google AI Gemini) :**
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-google-ai-gemini-spring-boot-starter</artifactId>
</dependency>
```

### 2. Configuration (`application.properties`)

**Avant (OpenAI) :**
```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
```

**Maintenant (Google AI Gemini) :**
```properties
spring.ai.google.ai.gemini.api-key=${GEMINI_API_KEY}
spring.ai.google.ai.gemini.chat.options.model=gemini-2.0-flash-exp
spring.ai.google.ai.gemini.chat.options.temperature=0.7
```

### 3. Code Java

✅ **Aucune modification nécessaire !**

Grâce aux abstractions de Spring AI, le code Java reste identique :
- `ChatService.java` - Inchangé
- `MainView.java` - Inchangé

Les interfaces `ChatClient`, `VectorStore`, etc. fonctionnent de manière transparente avec Gemini.

---

## 🔑 Configuration Simplifiée

### Obtenir votre clé API

1. Rendez-vous sur [Google AI Studio](https://aistudio.google.com/apikey)
2. Connectez-vous avec votre compte Google
3. Cliquez sur "Create API Key"
4. Copiez votre clé

### Variables d'Environnement

**Windows PowerShell:**
```powershell
$env:GEMINI_API_KEY="votre-clé-api-ici"
```

**Windows CMD:**
```cmd
set GEMINI_API_KEY=votre-clé-api-ici
```

**Linux/Mac:**
```bash
export GEMINI_API_KEY="votre-clé-api-ici"
```

---

## 🔄 Modèles Disponibles

Modifiez dans `application.properties` :

```properties
# Recommandé (par défaut)
spring.ai.google.ai.gemini.chat.options.model=gemini-2.0-flash-exp

# Alternatives
spring.ai.google.ai.gemini.chat.options.model=gemini-1.5-flash
spring.ai.google.ai.gemini.chat.options.model=gemini-1.5-pro
spring.ai.google.ai.gemini.chat.options.model=gemini-1.0-pro
```

---

## ⚙️ Paramètres Supplémentaires

Vous pouvez ajuster :

```properties
# Température (créativité)
spring.ai.google.ai.gemini.chat.options.temperature=0.7

# Top P (diversité)
spring.ai.google.ai.gemini.chat.options.top-p=0.95

# Top K (nombre de tokens)
spring.ai.google.ai.gemini.chat.options.top-k=40

# Max tokens de sortie
spring.ai.google.ai.gemini.chat.options.max-output-tokens=8192
```

---

## 🧪 Test de la Migration

Après la migration, testez avec :

```bash
# 1. Définir la clé API
$env:GEMINI_API_KEY="your-key"

# 2. Démarrer la base de données
docker-compose up -d

# 3. Lancer l'application
mvn clean spring-boot:run
```

Vérifiez dans les logs :
```
Using Google AI Gemini model: gemini-2.0-flash-exp
```

---

## 📊 Comparaison OpenAI vs Google AI Gemini

| Aspect | OpenAI (GPT-4o-mini) | Google AI Gemini (2.0 Flash) |
|--------|---------------------|------------------------------|
| **Setup** | Clé API simple | Clé API simple ✅ |
| **Coût** | Pay-per-token | Pay-per-token + quota gratuit |
| **Vitesse** | Rapide | Ultra-rapide ⚡ |
| **Contexte** | 128K tokens | 1M tokens 🚀 |
| **Multimodal** | Oui | Oui |
| **Code requis** | Aucun changement grâce à Spring AI | |

---

## ✅ Avantages de Google AI Gemini

1. **Contexte massif** : 1M tokens vs 128K pour OpenAI
2. **Setup ultra-simple** : Juste une clé API, pas de projet GCP
3. **Quota gratuit** : Parfait pour développer et tester
4. **Gemini 2.0 Flash** : Excellente performance
5. **Pas de facturation surprises** : Quotas clairs

---

## 🔒 Sécurité

⚠️ **Ne versionnez JAMAIS votre clé API !**

Vérifiez que `.gitignore` contient :

```
.env
```

---

## 🎯 Pourquoi Google AI Studio au lieu de Vertex AI ?

| Google AI Studio | Vertex AI |
|-----------------|-----------|
| ✅ Clé API simple | ❌ Nécessite projet GCP |
| ✅ Setup en 2 min | ❌ Setup complexe |
| ✅ Parfait pour dev/test | ✅ Production enterprise |
| ✅ Quota gratuit | 💰 Pay only |
| ✅ Pas de billing requis | ❌ Billing obligatoire |

**Pour ce projet de cours, Google AI Studio est parfait !**

---

## 📚 Documentation

- [Google AI Studio](https://aistudio.google.com/)
- [Gemini API Documentation](https://ai.google.dev/docs)
- [Spring AI Google AI Gemini](https://docs.spring.io/spring-ai/reference/)
- [Guide de démarrage rapide](./SETUP_GEMINI.md)

---

**Migration terminée ! Prêt à utiliser avec Gemini 2.0 Flash 🎉**

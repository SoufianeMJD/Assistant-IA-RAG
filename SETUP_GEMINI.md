# Configuration Gemini - Google AI Studio

## ✅ Configuration Simplifiée

Vous pouvez maintenant utiliser votre clé API Gemini directement sans Google Cloud Platform !

## 🔑 Obtenir votre clé API

1. Rendez-vous sur [Google AI Studio](https://aistudio.google.com/apikey)
2. Connectez-vous avec votre compte Google
3. Cliquez sur "Create API Key"
4. Copiez votre clé

## ⚙️ Configuration

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

## 🚀 Démarrage Rapide

```bash
# 1. Définir la clé API
$env:GEMINI_API_KEY="AIza..."

# 2. Démarrer PostgreSQL
docker-compose up -d

# 3. Lancer l'application
mvn clean spring-boot:run
```

## 📊 Modèles Disponibles

Dans `application.properties`, vous pouvez changer le modèle :

```properties
# Modèle par défaut (recommandé)
spring.ai.google.ai.gemini.chat.options.model=gemini-2.0-flash-exp

# Alternatives
spring.ai.google.ai.gemini.chat.options.model=gemini-1.5-flash
spring.ai.google.ai.gemini.chat.options.model=gemini-1.5-pro
spring.ai.google.ai.gemini.chat.options.model=gemini-1.0-pro
```

## 💡 Avantages vs Vertex AI

| Aspect | Google AI Studio | Vertex AI |
|--------|-----------------|-----------|
| **Setup** | ✅ Clé API simple | ❌ Projet GCP requis |
| **Coût** | 💰 Gratuit (quota) | 💰 Pay-per-use |
| **Facilité** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Production** | ✅ OK pour dev/test | ✅ Recommandé |

## 🔥 Gemini 2.0 Flash

Votre modèle Gemini 2.0 Flash offre :
- **Vitesse** : Réponses ultra-rapides
- **Contexte** : 1M tokens
- **Multimodal** : Texte + images
- **Prix** : Très compétitif

## 🔐 Sécurité

⚠️ **Ne versionnez jamais votre clé API !**

Vérifiez que `.gitignore` contient :
```
.env
```

## ✨ Code Inchangé

Grâce à Spring AI, aucun changement de code Java n'est nécessaire ! 

Seuls les fichiers de configuration ont été modifiés :
- ✅ `pom.xml` 
- ✅ `application.properties`

---

**Prêt à utiliser ! 🎉**

# 🖥️ Electronixa — Gestion des Produits

> Application de bureau Java pour la gestion du catalogue de produits d'**Electronixa**, une PME spécialisée dans la distribution de produits électroniques et informatiques.  
> Cette solution remplace la gestion basée sur des fichiers Excel par une application moderne, sécurisée et centralisée.

---

## 📋 Description

**Electronixa Gestion des Produits** est une application desktop développée avec **JavaFX** et **Hibernate**, permettant une gestion complète du catalogue produits avec authentification, gestion des rôles, historique des actions et export de données.

---

## ✨ Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| 🔐 **Authentification** | Connexion sécurisée par login / mot de passe |
| 👥 **Gestion des rôles** | ADMIN (accès complet) / USER (lecture seule) |
| 📦 **CRUD Produits** | Ajouter, Modifier, Supprimer, Lister |
| 🔍 **Recherche en temps réel** | Filtrage par libellé ou par type |
| ± **Ajustement de stock** | Ajouter ou soustraire des unités avec contrainte négative |
| 📜 **Historique des actions** | Traçabilité de chaque AJOUT / MODIFICATION / SUPPRESSION |
| 📄 **Export CSV** | Export compatible Excel avec encodage UTF-8 |
| 🖨️ **Export PDF** | Rapport HTML imprimable → sauvegarde en PDF via navigateur |
| ✅ **Validation des données** | Contrôle des champs vides, quantités négatives, doublons de code |

---

## 🛠️ Technologies utilisées

| Technologie | Version | Rôle |
|---|---|---|
| **Java** | JDK 8 | Langage principal |
| **JavaFX** | 8 | Interface graphique |
| **Hibernate** | 4.3.1.Final | ORM — persistance des données |
| **MySQL** | 5.x / 8.x | Base de données relationnelle |
| **NetBeans** | IDE | Environnement de développement |

---

## 🗂️ Structure du projet

```
Electronixa_Gestion_Produits/
│
├── src/app_gestion_produits/
│   ├── App_Gestion_Produits.java       # Classe principale JavaFX
│   ├── HibernateUtil.java              # Configuration Hibernate / SessionFactory
│   │
│   ├── Produit.java                    # Entité produit
│   ├── Utilisateur.java                # Entité utilisateur
│   ├── ActionLog.java                  # Entité historique des actions
│   │
│   ├── ProduitService.java             # CRUD produits (Hibernate)
│   ├── UtilisateurService.java         # Authentification
│   ├── ActionLogService.java           # Enregistrement et lecture des logs
│   ├── ExportService.java              # Export CSV et PDF
│   │
│   ├── Login.fxml                      # Interface de connexion
│   ├── LoginController.java            # Contrôleur login
│   ├── MainInterface.fxml              # Interface principale
│   └── MainInterfaceController.java    # Contrôleur principal
│
├── hibernate.cfg.xml                   # Configuration base de données Hibernate
├── Gestion_Produit.sql                 # Script SQL d'initialisation
└── README.md
```

---

## ⚙️ Installation et configuration

### Prérequis

- Java JDK 8+
- MySQL Server 5.x ou 8.x
- NetBeans IDE
- Bibliothèques : Hibernate 4.3.1, MySQL Connector/J, JavaFX

### Étape 1 — Base de données

Exécute le script SQL fourni dans MySQL Workbench ou en ligne de commande :

```sql
CREATE DATABASE gestion_produits;
USE gestion_produits;
-- puis exécuter le contenu de Gestion_Produit.sql
```

### Étape 2 — Configuration Hibernate

Ouvre `hibernate.cfg.xml` et renseigne ton mot de passe MySQL :

```xml
<property name="hibernate.connection.password">TON_MOT_DE_PASSE</property>
```

### Étape 3 — Lancer l'application

Ouvre le projet dans NetBeans → **Clean and Build** → **Run**

---

## 👤 Comptes par défaut

| Utilisateur | Mot de passe | Rôle | Accès |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | Complet (CRUD, Export, Historique, Ajustement stock) |
| `user1` | `user123` | USER | Consultation et recherche uniquement |

---

## 🗄️ Modèle de base de données

```
produit
├── id (PK, AUTO_INCREMENT)
├── code (UNIQUE)
├── libelle
├── type
├── quantite_stock
└── disponibilite

utilisateur
├── id (PK, AUTO_INCREMENT)
├── username (UNIQUE)
├── password
├── nom_complet
└── role ('ADMIN' | 'USER')

action_log
├── id (PK, AUTO_INCREMENT)
├── action ('AJOUT' | 'MODIFICATION' | 'SUPPRESSION' | 'AJUSTEMENT STOCK')
├── produit_code
├── produit_libelle
├── utilisateur
├── date_action
└── details
```

---

## 📸 Aperçu

| Écran | Description |
|---|---|
| **Login** | Authentification avec logo Electronixa |
| **Interface principale** | Formulaire produit + tableau filtrable |
| **Historique** | Tableau coloré des actions (vert/bleu/rouge) |
| **Export** | CSV (Excel) + PDF (rapport HTML stylisé) |

---

## 👨‍💻 Auteur

**Soufiane Hammouche** — Projet académique  
Module : *Bases de Données Avancées — JavaFX + Hibernate*

---

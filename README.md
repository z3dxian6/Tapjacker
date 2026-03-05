# TapJacker

TapJacker est une application Android conçue pour **démontrer et tester les vulnérabilités de type Tapjacking** dans les applications mobiles.

Le projet a été développé dans un objectif de **recherche en sécurité mobile, d’apprentissage et de tests d’intrusion autorisés**.

⚠️ Cet outil doit être utilisé **uniquement dans un cadre légal et autorisé**.

---

# Qu'est-ce que le Tapjacking ?

Le **Tapjacking** est une attaque de type **UI redressing** où une application malveillante affiche un **overlay** au-dessus d'une autre application afin de tromper l'utilisateur.

L’utilisateur croit cliquer sur un élément visible alors qu’il interagit en réalité avec une action située **dans l’application en arrière-plan**.

Cette attaque peut permettre par exemple :

* d’activer des permissions sensibles
* de lancer des activités internes d’une application
* de modifier des paramètres
* de déclencher des actions à l’insu de l’utilisateur

TapJacker permet de **simuler ce type d’attaque dans un environnement de test**.

---

# Fonctionnalités

* affichage d’un **overlay Android personnalisable**
* sélection d’une **application cible**
* sélection d’une **activité exportée**
* scan automatique des **exported activities**
* recherche dans les packages installés
* personnalisation de l’overlay

  * couleur
  * texte
  * logo
* délai configurable avant l’attaque
* démonstration visuelle du Tapjacking

---

# Fonctionnement technique

## 1. Récupération des applications installées

L’application utilise `PackageManager` pour récupérer toutes les applications installées sur l’appareil afin de permettre à l’utilisateur de sélectionner une cible.

---

## 2. Scan des activités exportées

TapJacker peut analyser les applications installées et identifier les **activités exportées** accessibles par d’autres applications.

Ces activités sont des **points d’entrée potentiels** exploitables.

---

## 3. Création de l’overlay

L’application utilise le `WindowManager` Android pour afficher un overlay couvrant l’écran :

```java
WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
```

L’overlay est configuré pour :

* couvrir l’écran
* rester visible au-dessus des autres applications
* masquer les interactions réelles

---

## 4. Permissions Android utilisées

L’application nécessite les permissions suivantes :

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```

Permet d’afficher un overlay au-dessus des autres applications.

```xml
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
```

Permet de lister les applications installées.

---

## 5. Lancement de l’activité cible

Une fois l’overlay affiché, l’application lance l’activité cible avec un `Intent` :

```java
Intent intent = new Intent();
intent.setComponent(new ComponentName(packageName, activityName));
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(intent);
```

Cela permet de déclencher une action dans l’application cible pendant que l’utilisateur voit uniquement l’overlay.

---

# Interface utilisateur

L’interface permet de configurer plusieurs paramètres :

* recherche d’application
* sélection du package cible
* sélection d’une activité exportée
* choix de la couleur de l’overlay
* délai avant exécution
* texte personnalisé
* affichage du logo

---

# Démonstration

## Interface principale

![Main Activity](misc/main_activity.png)

## Exemple d'attaque Tapjacking

![Tapjacking Demo](misc/tapjacking_demo.png)

---

# Installation

## Cloner le projet

```bash
git clone https://github.com/z3dxian6/Tapjacker.git
```

---

## Ouvrir avec Android Studio

1. ouvrir Android Studio
2. sélectionner **Open Project**
3. choisir le dossier `TapJacker`
4. construire le projet

---

## Installer l’application

Compiler le projet et installer l’APK sur un appareil Android ou un émulateur.

---

# Utilisation

1. lancer l’application **TapJacker**
2. sélectionner une application cible
3. sélectionner une activité exportée
4. configurer l’overlay
5. définir un délai
6. lancer la démonstration

Une fois exécuté :

* l’overlay est affiché
* l’activité cible est lancée
* l’utilisateur voit uniquement l’overlay

---

# Contre-mesures contre le Tapjacking

Les développeurs Android peuvent se protéger contre cette attaque en utilisant :

```java
setFilterTouchesWhenObscured(true);
```

ou en vérifiant si une vue est obscurcie :

```java
View.isObscured()
```

Autres protections possibles :

* éviter les activités exportées inutiles
* vérifier l’origine des intents
* utiliser `FLAG_SECURE`
* détecter les overlays

---

# Structure du projet

```
TapJacker
│
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/zed/tapjacker
│   │   │   │   └── MainActivity.java
│   │   │   ├── res/layout
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── tapjacker_overlay.xml
│   │   │   └── AndroidManifest.xml
│
├── misc
│   ├── main_activity.png
│   └── tapjacking_demo.png
│
├── README.md
└── LICENSE
```

---

# Avertissement légal

Ce projet est destiné uniquement à :

* la recherche en sécurité
* l’apprentissage de la sécurité Android
* les tests d’intrusion autorisés

Toute utilisation contre un système sans autorisation explicite peut être illégale.

---

# Auteur

**Zoran Tauvry**
Mobile Security • Pentesting

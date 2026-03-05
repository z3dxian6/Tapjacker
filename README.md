TapJacker

TapJacker est une application Android développée à des fins de recherche en sécurité mobile et de tests d'intrusion.
Elle permet de démontrer et d'exploiter des scénarios de Tapjacking en utilisant un overlay interactif au-dessus d'une application cible.

Le projet permet d'illustrer comment une application malveillante peut manipuler l'interface utilisateur afin de tromper un utilisateur et provoquer des interactions involontaires.

⚠️ Cet outil est destiné uniquement à des fins éducatives et de pentest autorisé.

Qu'est-ce que le Tapjacking ?

Le Tapjacking est une attaque de type UI redressing où une application malveillante superpose une interface transparente ou trompeuse au-dessus d'une autre application.

L'utilisateur pense cliquer sur un élément légitime alors qu'il interagit en réalité avec une action cachée dans l'application cible.

Cette technique peut être utilisée pour :

activer des permissions

lancer des activités sensibles

modifier des paramètres

installer des applications

déclencher des actions dans une autre application

TapJacker permet de simuler ce type d'attaque dans un environnement contrôlé.

Fonctionnalités

affichage d'un overlay Android personnalisable

sélection d'une application cible

sélection d'une activité exportée

scan automatique des exported activities

recherche dans les packages installés

personnalisation de l'overlay :

couleur

texte affiché

logo optionnel

temporisation configurable avant l'exécution

démonstration visuelle de l'attaque

Fonctionnement technique

L'application fonctionne en plusieurs étapes.

1. Récupération des applications installées

TapJacker récupère toutes les applications installées via le PackageManager afin de permettre à l'utilisateur de sélectionner une cible.

Cette liste est utilisée pour alimenter le menu déroulant.

MainActivity

2. Scan des activités exportées

L'application peut analyser toutes les applications installées et identifier les activités exportées accessibles par d'autres applications.

Ces activités peuvent ensuite être ciblées par l'attaque.

MainActivity

3. Création d'un overlay

Une vue overlay est injectée dans le système à l'aide du WindowManager.

L'overlay est configuré pour :

couvrir tout l'écran

rester visible

empêcher l'utilisateur de comprendre l'interaction réelle

tapjacker_overlay

Le système utilise le type :

TYPE_APPLICATION_OVERLAY

afin d'afficher l'overlay au-dessus des autres applications.

4. Permission système

L'application nécessite la permission suivante :

SYSTEM_ALERT_WINDOW

afin de pouvoir afficher un overlay au-dessus des autres applications.

AndroidManifest

Elle utilise également :

QUERY_ALL_PACKAGES

pour lister les applications installées.

5. Lancement de l'activité cible

Une fois l'overlay affiché, TapJacker lance l'activité cible via un Intent.

Cela permet de déclencher une interaction dans l'application cible pendant que l'utilisateur voit uniquement l'overlay.

MainActivity

Interface utilisateur

L'interface permet de configurer plusieurs paramètres avant de lancer l'attaque.

Options disponibles :

recherche d'application

sélection du package cible

sélection de l'activité exportée

choix de la couleur de l'overlay

délai avant exécution

texte personnalisé

affichage d'un logo

activity_main

Démonstration
Interface principale

Exemple d'overlay tapjacking

Installation
Cloner le projet
git clone https://github.com/z3dxian6/Tapjacker.git
Ouvrir dans Android Studio

ouvrir Android Studio

sélectionner Open Project

choisir le dossier TapJacker

construire le projet

Installer l'application

Compiler l'application puis installer l'APK sur un appareil Android ou un émulateur.

Utilisation

lancer l'application TapJacker

sélectionner une application cible

sélectionner une activité exportée

configurer l'overlay

définir un délai

lancer la démonstration

Une fois exécuté :

l'overlay est affiché

l'activité cible est lancée

l'utilisateur voit uniquement l'overlay

Contre-mesures

Les développeurs peuvent se protéger contre le Tapjacking en utilisant :

setFilterTouchesWhenObscured(true)

ou en vérifiant l'état de la fenêtre :

View.isObscured()

Autres protections possibles :

désactiver les activités exportées non nécessaires

vérifier l'origine des intents

utiliser FLAG_SECURE

Structure du projet
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
Avertissement légal

Ce projet est fourni uniquement pour :

la recherche en cybersécurité

l'apprentissage de la sécurité Android

les tests d'intrusion autorisés

Toute utilisation de cet outil contre un système sans autorisation explicite peut être illégale.

Auteur

Projet développé par Zoran Tauvry
Pentesting / Mobile Security Research

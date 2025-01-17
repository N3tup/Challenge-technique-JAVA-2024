# Projet Technique Java Drash Bouchez

## Description

Notre projet est une application de chat inspirée de Discord, développée en utilisant Java, Spring Boot et Maven. L'application permet aux utilisateurs de discuter dans des canaux publics et privés, et la creation de compte.

## Fonctionnalités

- Discuter en temps réel dans un serveur public
- Envoyer des messages en privé au utilisateurs connectés
- Réinitialiser le mot de passe
- Voir les membres en ligne

## Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

## Installation

1. Clonez le dépôt :
    ```bash
    git clone https://github.com/N3tup/Challenge-technique-JAVA-2024.git
    ```

2. Accédez au répertoire du projet :
    ```bash
    cd Challenge_Technique_Drach_Bouchez
    ```

3. Compilez le projet avec Maven :
    ```bash
    mvn clean install
    ```

## Exécution

1. Démarrez l'application Spring Boot :
    ```bash
    mvn spring-boot:run
    ```

2. Ouvrez votre navigateur et accédez à `http://localhost:8080`.

## Utilisation

### Réinitialisation du mot de passe

1. Accédez à la page de réinitialisation du mot de passe : `http://localhost:8080/user/reset_password`
2. Entrez votre adresse e-mail et votre nouveau mot de passe.
3. Cliquez sur "Enregistrer le mot de passe".

### Discussion

1. Connectez-vous à l'application.
2. Rejoignez un serveur et sélectionnez un canal.
3. Commencez à envoyer des messages dans le canal sélectionné.

## Technologies utilisées

- Java
- Spring Boot
- Maven
- SockJS et STOMP pour la communication en temps réel
- HTML/CSS/JavaScript pour le frontend

## Contribuer

Les contributions sont les bienvenues ! Veuillez suivre les étapes ci-dessous pour contribuer :

1. Forkez le dépôt.
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/ma-fonctionnalite`).
3. Commitez vos modifications (`git commit -am 'Ajout de ma fonctionnalité'`).
4. Poussez votre branche (`git push origin feature/ma-fonctionnalite`).
5. Ouvrez une Pull Request.

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.
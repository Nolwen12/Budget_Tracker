# Cahier des charges

## Nom de l’application¶
Budget Tracker
## Contexte¶
Nous devons créer une application afin que les utilisateurs puissent gérer leurs dépenses.
## Besoin utilisateur¶
Un utilisateur souhaite suivre ses dépenses personnelles, comprendre dans quelles catégories il dépense le plus et connaître son total mensuel.
## Utilisateurs cibles¶
Précisez à qui s’adresse l’application : étudiants, formateurs, clients, particuliers, administrateurs, sportifs, lecteurs, etc.
## Fonctionnalités principales¶
Ajouter une dépense avec titre, montant, catégorie et date.
Afficher la liste des dépenses.
Supprimer une dépense.
Calculer le total des dépenses.
Filtrer par catégorie.
Valider les montants.
Sauvegarder les dépenses avec Room.
## Fonctionnalités bonus¶
Ajouter un budget mensuel.
Afficher le reste à dépenser.
Ajouter des statistiques par catégorie.
Trier par montant ou date.
Ajouter un écran résumé.
## Données manipulées¶
Décrivez les données principales sous forme de tableau.
Donnée
Champs possibles
Expense
id, title, amount, category, date, note
A voir
id, titre, contenu, catégorie, date
A voir
id, service, date, créneau, statut
A voir
id, titre, montant, catégorie, date

## Écrans prévus¶
Listez les écrans.
Écran
Rôle
Accueil
Présenter l’application
Liste
Afficher les données
Formulaire
Créer ou modifier une donnée
Détail
Afficher une donnée complète

## Choix techniques¶
Indiquez les technologies utilisées.
Room
Kotlin
Android XML
ViewModel
Repository
Exemples : Kotlin, Android XML, RecyclerView, ViewModel, Repository, Room, Firebase Authentication, Firestore, Retrofit.
## Critères de réussite¶
Définissez ce qui permettra de considérer le projet comme réussi : compilation, ajout, liste, suppression, persistance, gestion des erreurs, documentation.
l’application compile ;
le parcours principal fonctionne ;
les données s’affichent dans une liste ;
l’utilisateur peut ajouter une donnée ;
l’utilisateur peut supprimer ou modifier une donnée ;
les erreurs simples sont gérées ;
le projet contient une documentation ;
vous pouvez expliquer vos choix techniques.

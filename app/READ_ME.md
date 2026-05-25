# Nom de l’application
Budget Tracker
## Présentation
Application Android de suivi de budget personnel permettant de gérer ses dépenses
au quotidien et de les organiser par catégorie.

## Besoin utilisateur
Permettre à un utilisateur de suivre ses dépenses facilement et de les catégoriser.

## Fonctionnalités
- Ajouter des dépenses
- Catégoriser les dépenses (Alimentation, Transport, Loisirs, Logement...)
- Afficher la liste des dépenses par catégorie

## Technologies utilisées
- Kotlin
- Room (base de données locale)
- LiveData & ViewModel (architecture MVVM)
- Material Design 3
- KSP
- Coroutines

## Architecture
com.h3hitema.budgettracker/
├── model/ │
├── Expense.kt
│ └── Category.kt
├── repository/
│ ├── ExpenseRepository.kt
│ └── CategoryRepository.kt
├── viewmodel/
│ ├── ExpenseViewModel.kt
│ └── ExpenseViewModelFactory.kt
├── ui/
│ ├── ExpenseActivity.kt
│ ├── ExpenseAddActivity.kt
│ ├── MonthlyBudgetActivity.kt
│ ├── StatisticsActivity.kt
│ └── adapter/
│ └── ExpenseAdapter.kt
├── data/
│ └── local/
│ ├── AppDatabase.kt
│ ├── ExpenseDao.kt
│ ├── ExpenseEntity.kt
│ ├── CategoryDao.kt
│ ├── CategoryEntity.kt
│ └── CategoryWithExpenses.kt
└── mapper/
├── ExpenseMapper.kt
└── CategoryMapper.kt

## Modèles de données

### Expense
- id : Int
- title : String
- amount : Double
- date : Date
- note : String
- categoryId : Long
- categoryName : String

### Category
- id : Int
- wording : String

## Écrans
- **ExpenseActivity** : liste de toutes les dépenses
- **ExpenseAddActivity** : formulaire d'ajout d'une dépense
- **MonthlyBudgetActivity** : définition du budget mensuel maximum
- **StatisticsActivity** : statistiques des dépenses

## Installation et lancement
1. Cloner le projet
2. Ouvrir dans Android Studio
3. Lancer sur un émulateur ou appareil Android (API 21+)

## Tests réalisés
- Ajout d'une dépense
- Affichage de la liste des dépenses
- Navigation entre les écrans

## Captures d'écran
À venir

## Limites connues
- Suppression de dépense non finalisée
- Statistiques non implémentées
- Budget mensuel non finalisé

## Améliorations possibles
- Finaliser la suppression de dépense
- Implémenter les statistiques
- Implémenter le budget mensuel
- Modifier une dépense existante

## Auteurs
- Nolwen Fernandes
- Sebastian Lin

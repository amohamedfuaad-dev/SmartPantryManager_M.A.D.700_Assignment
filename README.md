# Smart Pantry Manager

## App Description

Smart Pantry Manager is an Android application developed to help users
manage pantry ingredients and find recipes based on the ingredients they
currently have available.

The application allows users to add, edit and remove pantry ingredients
and receive recipe suggestions based on strict ingredient matching.

A recipe is suggested only when the required ingredients are available
in the pantry in sufficient quantities. Optional ingredients do not prevent
a recipe from being suggested.

## Database

### SQLite

Smart Pantry Manager uses SQLite as its local database.

SQLite was selected because it is built into Android, does not require a
separate database server, supports persistent local storage and is suitable
for an application that manages pantry and recipe information locally.

The database stores:

- Pantry items
- Recipes
- Recipe ingredients

The application uses a database helper to create and manage these tables
and to perform recipe matching.

## Main Features

- Add pantry ingredients
- Edit pantry ingredients
- Delete pantry ingredients
- View pantry contents
- Search for matching recipes
- Strict required-ingredient matching
- Quantity matching
- Compatible unit matching
- Optional recipe ingredients
- Recipe preparation instructions
- Persistent local storage
- Settings screen
- Mobile-friendly interface

## Recipe Matching

Recipe suggestions use strict matching.

For each recipe, the application checks every required ingredient against
the ingredients stored in the pantry.

A required ingredient must:

1. Exist in the pantry.
2. Have a matching ingredient name.
3. Have a compatible unit.
4. Have enough quantity for the recipe requirement.

Optional ingredients are not required for a recipe to match.

The application also supports compatible weight units such as grams and
kilograms, and compatible volume units such as millilitres and litres.

## Database Version

The database helper creates the required database tables and performs
database upgrades when the application version changes.

## Setup and Run Instructions

1. Open Android Studio.
2. Select **Open** and choose the Smart Pantry Manager project folder.
3. Allow Android Studio to synchronise the Gradle files.
4. Connect an Android device or start an Android Emulator.
5. Select the `app` run configuration.
6. Click **Run**.
7. The application will create or use its local SQLite database when it
   starts.

## Requirements

- Android Studio
- Android SDK
- Java
- Android Emulator or compatible Android device

## Project Structure

The application is organised into separate packages for the main parts
of the application.

### Model

Contains classes representing application data, including:

- PantryItem
- Recipe
- RecipeIngredient

### Data

Contains the SQLite database helper responsible for local data storage
and recipe matching.

### Adapter

Contains RecyclerView adapters used to display pantry items and recipes.

### UI

Contains the Android activities that provide the application's screens
and user interactions.

### Util

Contains utility classes used by the application, including background
database task handling.

## Application Navigation

The main screen provides access to:

- Pantry
- Suggested Recipes
- Settings

The application also provides navigation through the main menu.

## Data Persistence

Pantry information is stored locally using SQLite. This allows pantry
items to remain available when the application is closed and reopened.

Database operations are performed separately from the Android user
interface so that database processing does not unnecessarily block the
main UI thread.
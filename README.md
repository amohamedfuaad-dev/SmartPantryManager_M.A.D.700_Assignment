SMART PANTRY MANAGER 
====================

Java Android application for Mobile App Development 700.

CORE FUNCTIONALITY
------------------
- Pantry Create, Read, Update and Delete (CRUD)
- Persistent local SQLite database
- RecyclerView with custom PantryAdapter and RecipeAdapter
- Suggested Recipes screen
- Strict recipe matching: every required ingredient must exist in the pantry
  in sufficient quantity; optional ingredients may be absent
- Robust ingredient matching for common singular/plural forms
- Compatible weight units: g and kg
- Compatible volume units: ml and l
- Recipe detail with required/optional ingredients and preparation method
- Settings screen with Clear Pantry action
- Toolbar navigation menu on the dashboard
- Background executor for database work to reduce UI blocking

TECHNICAL SETTINGS
------------------
- Java only
- Android Studio
- compileSdk 35
- targetSdk 35
- minSdk 23
- Java 17
- SQLite via SQLiteOpenHelper
- AndroidX AppCompat and RecyclerView
- No Google Maps, GPS or location services

FIRST RUN
---------
1. Open SmartPantryManager in Android Studio.
2. Allow Gradle sync to complete.
3. Ensure Android SDK Platform 35 is installed.
4. Run on an emulator or Android device.
5. The first database creation seeds 20 recipes.


STRICT-MATCHING LOGIC
---------------------
For every recipe, the app checks each required ingredient. A match requires:
1. A pantry ingredient with the same normalised name.
2. Compatible units.
3. Pantry quantity >= required quantity.
Optional recipe ingredients are ignored by the strict qualifying test.



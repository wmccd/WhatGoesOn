### App Structure
This is not a complicated app so it is constructed as a single module app primarily comprised of three layers

* **presentation** (screens and viewmodels)
* **usecases** (logic that is not tied to a specific presentation element)
* **repository** (fetching and sending data to a source: db, datastore or web api)

Virtually all files fall into these categories but there are some exceptions.

### File Types: Presentation Screen
All screens should follow the same pattern (see Templates).

##### Common Elements to all Presentation Screens:

All screens will only have one public function; the name of the Composable function that can be called (and also the name of the file)

All screens should have a dedicated sibling ViewModel file that determines the values that will be displayed on the screen and how the app should react when the user interacts with the screen.

All screens should accept two parameters: 

1. navController: NavHostController. It's navigate() function will be invoked when the user is ready to leave the screen and move to another.
2. A viewModel object. This should be defaulted in the parameter list and there should never be need to explictly create this object.

All screens will have a LaunchedEffect block to detect the navigation events

All screens will have three display states:

1. Loading: display the common Loading UI Composable.
2. Error: display the common Error UI Composable.
3. Content: display the main screen coontent

The display state will be determined by the viewModel object similar to the following: 

```
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> DisplayLoading()
        uiState.error != null -> DisplayError(uiState.error)
        uiState.data != null -> DisplayData(viewModel)
    }
``` 


### File Types: Presentation ViewModel
All ViewModels should follow the same pattern (see Templates).

##### Common Elements to all Presentation ViewModels:

All ViewModels will set up a XXScreenUiState data class similar to the following to determine what type of data should be displayed. Each screen will have it's own UiData class because each screen will have different data to display, but there should always be isLoading and error states.

```
data class Feature1TopScreenUiState(
    val isLoading: Boolean = false,
    val data: XXScreenUiData? = null,
    val error: String? = null
)
```

All ViewModels will set up a XXScreenEvents sealed interface that will be used to determine what sort of action the user has just taken. Each ViewModel will have it's own XXScreenEvents class because each screen will have different interactions.

```
sealed interface Feature1TopScreenEvents{
    data object ButtonClicked: XXScreenEvents
}
``` 

All ViewModels will only have one public function: onEvent()

All ViewModsls will have an `init{}` block that runs when the ViewModel is created. The UiState `isLoading` state will be set to true while the data to display on screen is fetched.


### Other

MyApplication. This is the application file that will be accessible throughout the app, and has been updated to allow access to certain items that are commonly used within the app. These items should not hold any state. They include:

* Logger (logs to logcat)
* Gson object (for converting Json to objects and vice-versa)
* Repository (a smplified way to access data)


### Templates

### Setting Up in Git For the First Time.
In Terminal go to the folder where your code lives and make sure you are in the top level folder.

Run the following to indicate that this folder will be under Git control

```
git init -b main
```

You may be prompted to install some Git tools. If so install and then run the command again.

Update the .gitignore file so that it contains all the entries needed for an Android project.

Run the following to add all the files will be part of Git and committed. This means they are ready to pushed.

```
git add .
git commit -m "First commit"
```

Create a new repository on GitHub website with same name as your app. 

To avoid errors, do **not** initialize the new repository with README, license, or gitignore files.

```
git remote add origin https://github.com/wmccd/WhatGoesOn.git
git branch -M main
git push -u origin main
```

When you push it might say: "*remote: Invalid username or password*"

This means the local Git repository does not have access to the remote Github website.

Previously: In the past you had to supply the email and password of the remote Github website.

Now: You are asked for email address (username) but the password is actually a token generated in Settings->Developer Settings

Once this is done you can run the push command again:

```
git push -u origin main
```
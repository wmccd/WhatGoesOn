### App Structure
This is not a complicated app so it is constructed as a single module app primarily comprised of three layers

* **presentation** (screens and viewmodels)
* **usecases** (logic that is not tied to a specific presentation element)
* **repository** (fetching and sending data to a source: db, datastore or web api)

Virtually all files fall into these categories but there are some exceptions, most obviously the Application class and the MainActivity class.

### File Type: MyApplication

An app can have a file that extends Application. If you choose to do so this should be registered in the AndroidManifest.xml file.

```
android:name=".MyApplication"
```

The application provides an opportunity to instigate any set up that you want to happen as the application starts up. 

In this instance we are setting up global (aka static) objects that can then be accessed throughout the app. It is vital that these objects do not retain any state and will therefore behave the same regardless of where or when they are used in the application.

In this app the following have been set up:

* A Logger object (logs to logcat)
* A Gson object (for converting Json to objects and vice-versa)
* A Repository object (a simplified way to access data sources)

All of the the above will be triggered when the following function is called automatically as the app starts:

```
override fun onCreate()
```

### File Type: MainActivity

In earlier iterations of Android it would be expected that there could/would be an Activity class for each screen. However, it is now standard to have an app that consists of only one (or a few). 

In this application all the UI will be hosted within a single activty (MainActivity) that will be responsible for:

* Displaying the TopBar when each screen is displayed
* Handling the display and behaviour of the back button in the TopBar
* Handling the display and behaviour of the close button in the TopBar
* Handling the display and behavioir of the items in the bottom navigation bar
* Handling the display and behavioir of the the Floating Action Button
* Indicating which theme will be used on the screens
* Listening for a change in the screen being displayed.

In order to help make this screen work well three variables are set up:

1. NavHost - this defines all routes and screens in the app
2. NavHostController - this is used to navigate between screens
3. selectedScreen - this is used to keep track of the currently selected screen
    
All the Activity classes used in the application need to be listed the AndroidManifest.xml file (it's akin to a content's page for the app.

As a single activity app the MainActivity will be denoted as the "main" and "launcher" activity. 

```
<activity
            android:name=".presentation.screens.MainActivity"
            android:exported="true"
            android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```

MainActivity will therefore be automatically launched as the app starts up and all of the the above will be triggered when the following function is called automatically:
`override fun onCreate(savedInstanceState: Bundle?)`

### File Type: NavigationEnum
Setting up the navigation in an app can be complicated and messy. Each screen that displays in the app should be declared in this enumeration, along with the the properties associated with that screen.

The following properties are defined for each screen:

```
enum class NavigationEnum(
    val route: String,
    @StringRes val topBarTitle: Int,
    val topBarNavigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    val topBarCloseIcon: ImageVector = Icons.Default.Close,
    val bottomTabIcon: ImageVector = Icons.Default.Warning,
    val topLevelScreen: Boolean = false,
)
```

The `route` value is used to identify the screen that will be navigated to. The other properties are used to determine the items in the TopBar and BottomBar that will appear when each screen is displayed.


### File Type: Theme

Use this file to override the default colour theme and declare your own (MyAppTheme) with the colours you want. 

```
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
```

Use the Material Design theme builder website to make this effort much simpler: [Material Theme Builder](https://m3.material.io/theme-builder#/custom)


### File Type: Presentation Screen
All screens should follow the same pattern (see Templates).

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


### File Type: Presentation ViewModel
All ViewModels should follow the same pattern (see Templates).

All ViewModels will set up a XXScreenUiState data class similar to the following to determine what type of should be displayed: a loading spinner, an error message, or the expected screen content. 

Each screen will have it's own `XXUiData` class because each screen will have different data to display, but there should always be isLoading and error states.

```
data class Feature1TopScreenUiState(
    val isLoading: Boolean = false,
    val data: XXScreenUiData? = null,
    val error: String? = null
)
```

All ViewModels will set up a XXScreenEvents sealed interface that will be used to determine what sort of action the user has just taken. Each ViewModel will have it's own XXScreenEvents class because each screen will have different interactions.

```
sealed interface XXScreenEvents{
    data object ButtonClicked: XXScreenEvents
}
``` 

All ViewModels will only have one public function: onEvent()

All ViewModsls will have an `init{}` block that runs when the ViewModel is created. The UiState `isLoading` state will be set to true while the data to display on screen is fetched.

### File Type: UseCases

TODO

### File Type: Repository

TODO

### Templates

To help keep things simple and consistent template files have been created and added to the project in the Templates folder. These are normally kept within the Android Studio IDE and not with the app project itself but have been copied here to make sure they don't get lost. 

### Icon Generator
https://icon.kitchen

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
git remote add origin https://github.com/<your Git Id>/<Git Repository Name>.git
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
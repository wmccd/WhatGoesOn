

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
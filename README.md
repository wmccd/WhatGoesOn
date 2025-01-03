

### Setting Up in Git 
git init -b main
git add .
git commit -m "First commit"

Create a new repository on GitHub with same name as your app. 
To avoid errors, do not initialize the new repository with README, license, or gitignore files.

git remote add origin https://github.com/wmccd/WhatGoesOn.git
git branch -M main
git push -u origin main

When you push it might say "remote: Invalid username or password"
This means the local Git repository does not have access to the remote Github website.
Then: In the past you had to supply the email and password of the remote Github website.
Now: You are asked for email address (username) but the password is actually a token generated in Settings->Developer Settings

Once this is done you can run the push command again:
git push -u origin main
# GitHub Repository Setup Guide

## Step 1: Create the Repository on GitHub

1. Go to [https://github.com/new](https://github.com/new)
2. Fill in the repository details:
   - **Repository name**: `lumena`
   - **Description**: Music player for Android - Lumena
   - **Visibility**: Choose Public or Private
   - **DO NOT** initialize with README, .gitignore, or license (we already have these)
3. Click **Create repository**

## Step 2: Push Your Code

After creating the repository, run these commands in your terminal:

```bash
# Make sure you're in the project directory
cd C:\Users\mukht\Desktop\Android\lotus-master

# Push to GitHub (you may be prompted for credentials)
git push -u origin main
```

### Authentication Options:

**Option A: Personal Access Token (Recommended)**
1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with `repo` scope
3. Use the token as password when prompted

**Option B: GitHub CLI**
```bash
gh auth login
git push -u origin main
```

**Option C: SSH (if configured)**
```bash
git remote set-url origin git@github.com:gideongeny/lumena.git
git push -u origin main
```

## Step 3: Verify

After pushing, visit: [https://github.com/gideongeny/lumena](https://github.com/gideongeny/lumena)

Your code should now be visible on GitHub!

## Alternative: Push to Existing Repository

If you want to push to a different repository name, update the remote:

```bash
git remote set-url origin https://github.com/gideongeny/YOUR_REPO_NAME.git
git push -u origin main
```


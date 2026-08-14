# Chrome se APK banane ke liye

1. GitHub par new repository banao.
2. Is ZIP ko extract karke **saari files/folders** repository ke root me upload karo. `.github/workflows/build-apk.yml` bhi upload hona chahiye.
3. GitHub me **Actions** tab kholo.
4. **Build Study Timer APK** workflow select karo.
5. **Run workflow** dabao.
6. Build complete hone par run ke bottom me **Artifacts** se `BhartiyeStudent-Study-Timer-debug` download karo.
7. ZIP artifact ko extract karke `app-debug.apk` install karo.

Workflow GitHub-hosted runner par Gradle se debug APK build karta hai aur APK ko workflow artifact ke roop me upload karta hai.

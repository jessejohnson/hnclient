# hnclient
A Hacker News client written with AAC fanciness 

This project hopes to be an offline Hacker News reader app that presents the user with a daily digest of top HN content, delivered for offline reading.
Right now, it doesn't do offline very well, so I've shelved that to focus on the primary reason for starting the project: to play with Android Architecture components!

### What does it contain?
1. The UI is built with Jetpack compose, hosted in one `MainActivity`
2. Navigation uses the new Navigation Component convenience extensions for Compose UI
3. All network calls are initiated with `WorkManager`
4. There's a `ViewModel` doing view model stuff...
5. Persistence is with Room. Preferences DataStore is included, but not actually used.
6. Site content is downloaded in plaintext with JSoup.
7. Dependencies are injected with Koin

### What's coming next?
In no particular order...
1. [] Build reader mode for better offline experience
2. [] Use `Webview` only when online (this is a compromise)
3. [] Schedule content refreshing
4. [] Use `WorkManager` for database ops. Maybe.
5. [] Add custom themeing and settings so Preferences DataStore dep is actually used
6. [] Wrap `WorkManager` and inject _that_
7. [] Add tests for DI, `Workers`, `ViewModel`, navigation and stuff

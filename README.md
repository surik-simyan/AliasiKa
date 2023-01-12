
# 📱 AliasiKa
Simple alias game bulit for iOS and Android. Used SwiftUI, Jetpack Compose and Kotlin Multiplatform. <br>
![MIT](https://img.shields.io/badge/license-MIT-red?style=for-the-badge)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Swift](https://img.shields.io/badge/swift-F54A2A?style=for-the-badge&logo=swift&logoColor=white) 
![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=ios&logoColor=white)
<br>

![](https://github.com/surik-simyan/AliasiKa/blob/main/media/banner.jpg)

# 🕹️ Gamemodes
Currently, there are 3 available gamemodes.
 - Standard - Default Alias gamemode with 5 words
 - Swipe - Gussing words one by one
 - Stack - Infinite list of words

> Note: Due to little knowledge of SwiftUI, cards swiping is not available in iOS now, but will be implemented in future updates.

# ⚙️ Software part
All game logic is written in pure Kotlin and is common to both operating systems. Moreover, all the UI strings are also shared from the commond code. Below is an illustrative diagram of the common code.
```mermaid
classDiagram
    class TeamsRepository {
    +String teamOneName
    +String teamTwoName
    +String winnerTeam
    +PlayingTeam playingTeam
    
    -Int _teamOneScore
    +Int teamOneScore
    -Int _teamTwoScore
    +Int teamTwoScore
    -String _playingTeamName
    +String playingTeamName
    -changeTeam()
    +changePoints()
    +resetValues()
    }

    class WordsRepository {
        +List~String~ words
        +rotateWords()
        String PEOPLE$
        String WORDS$
    }

    class AbstractGameViewModel {
        <<Abstract>>
        -float playingTime
        +WordsRepository wordsRepository
        -TeamsRepository teamsRepository
        -int _score
        +int score
        +List~String~ words
        -String _remainingTime
        +String remainingTime

        -timerFinished()
        -changeRepoPoints()
        +finishRoundEarly()
        +rotateWords(index)*
        +rotateRepoWords()*
        +wordGuessed(index)*
        +wordUnguessed()*
        +addPoint()
        +minusPoint()
        Int STANDARD_GAMEMODE_WORD_COUNT$
        Int SWIPE_GAMEMODE_WORD_COUNT$
        Int STACK_GAMEMODE_WORD_COUNT$
    }

    class MainViewModel {
        -TeamsRepository teamsRepository
        +Int teamOneScore
        +Int teamTwoScore
        +String playingTeamName
        +Gamemode gamemode
        +float points
        +float time
        +String teamOneName
        +String teamTwoName
        +endGameEarly()
        +isGameEnded() Boolean
        +winnerTeamName() String
        +resetValues()
    }
    


    class StackGameViewModel{
        -List~String~ _standardWords
        +List~String~ standardWords
        -updateStackWords()
    }
    class StandardGameViewModel{
        -List~String~ _stackWords
        +List~String~ stackWords
        -Int guessedWordsCount
        -fillFirstFiveWords()
        -updateStandardWords()
    }
    
    class SwipeGameViewModel {
        -List~String~ _swipeWords
        +List~String~ swipeWords
        -updateSwipeWords()
    }

    AbstractGameViewModel <|-- StackGameViewModel
    AbstractGameViewModel <|-- StandardGameViewModel
    AbstractGameViewModel <|-- SwipeGameViewModel
    

    class PlayingTeam{
        <<Enumeration>>
        TeamOne
        TeamTwo   
    }  

    class Gamemode{
        <<Enumeration>>
        STANDARD
        SWIPE
        STACK 
}
```
# ⚠️ Known bugs
If the iOS application does not start with the error `No such module 'MultiPlatformLibrarySwift'`, please refer to [this](https://github.com/icerockdev/moko-kswift/issues/55#issuecomment-1200139522) issue and try following.
 1. `./gradlew clean` in the root directory
 2. Try to run app in XCode (it's okay if it will fail)
 3. Go to /iosApp and run `pod install`
 4. Try to launch again. <br>
If the app doesn't work again after that, contact me at surik.simyan@gmail.com.

# 📝 Roadmap of updates after KotlinConf Contest
- [ ] Fix card swipes in iOS
- [ ] Improve apps architecture
- [ ] Add custom words with .csv files
- [ ] Add list of wordsets
- [ ] Add sounds
- [ ] Add ingame spontaneous events
- [ ] Translate app to several languages
- [ ] Publish app to AppStore and Google Play (F-Droid optionally)

# 📚 Used libraries and frameworks
**[Kotlin Multiplatform Mobile](https://kotlinlang.org/lp/mobile/)** <br>
**[Koin](https://github.com/InsertKoinIO/koin)** <br>
**[Moko Resources](https://github.com/icerockdev/moko-resources)** <br>
**[Moko MVVM](https://github.com/icerockdev/moko-mvvm)** <br>
**[Moko KSwift](https://github.com/icerockdev/moko-kswift)** <br>
**[SwiftUI-CardStackView](https://github.com/dadalar/SwiftUI-CardStackView)** <br>




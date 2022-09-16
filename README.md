## Description of project

'Quizzzz' is a quiz game, where players can play individually or compete against others, while also increasing
their awareness of the real energy consumption of everyday activities!

## Group members

| Profile Picture | Name | Email |
|---|---|---|
| ![](https://eu.ui-avatars.com/api/?name=Ivan+Moreno) | Ivan Moreno Sarries | i.morenosarries-1@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=Alexander+Nygaard) | Alexander Nygård | A.K.Nygard@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=Eyse+Engbers) | Eyse Engbers | E.J.Engbers@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=Pepijn+Bosma) | Pepijn Bosma | P.Bosma@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=Codrin+Ogreanu) | Codrin Ogreanu | C.Ogreanu@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=Yury+Tremsin) | Yury Tremsin | y.tremsin@student.tudelft.nl |

<!-- Instructions (remove once assignment has been completed -->
<!-- - Add (only!) your own name to the table above (use Markdown formatting) -->
<!-- - Mention your *student* email address -->
<!-- - Preferably add a recognizable photo, otherwise add your GitLab photo -->
<!-- - (please make sure the photos have the same size) --> 

## How to run it

After cloning the repository, go to the project folder. From this [link](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/activity-bank/-/jobs/2444739/artifacts/download?file_type=archive),
download the .zip folder containing the activities in json format. Move the folder to `
{projectDir}/server/src/main/resources` and extract it. Inside it will be another zip file; Rename it to
activities.zip. Now you are ready to run the project!
Using gradlew through either Intellij or the command line, run `bootRun` to start the server, and `run` to start a client.

## Using the application

When started up, the application will ask the user to enter a nickname to be used during the game. This nickname needs
to be between **three** and **twenty** characters long. After this the user will be shown the home screen where they can
choose to click five buttons which will lead them to different screens.

### Singleplayer game:

When pressed the top-left button on the screen labeled *Singleplayer game* a new singleplayer game will start.

The user will be shown a question, and they will have twenty seconds to answer it. The time can be seen at the top of
the screen. The question will end either by the user selecting an answer or by the time running out.

If the answer given by the user was correct, then the score of the user will be incremented by the appropriate amount.
The current score of the user can be seen directly under the time.

After twenty questions the game will end and the user will be shown the leaderboard of singleplayer games.

If the user wants to exit the game at any time they can click the exit button in the top-left corner of the screen which
will lead them back to the home screen.

### Multiplayer game:

The multiplayer game is started by pressing the button labeled *Multiplayer game* on the home screen. You will be
taken to the lobby, where you can see the names of the other players waiting for a game.

At any point, you or any other player can start the game by pressing `Start game`.

Alternatively, you can instead press `Exit`, which will take you back to the home screen.

A multiplayer game is very similar to the singleplayer version, with one major difference: you can use emojis 🤠
By pressing any of the emoji buttons situated on the left-hand side, you can send emojis that are seen by every
player in that game in the chat window, situated on the right-hand side.

### How to play:

For instructions for how to play, press the `How to play` button located on the home screen.

### Global leaderboard:

By pressing the `Global leaderboard` button on the home screen, you can see the scores of the best player, ranked
from highest to lowest.

### Admin interface:

By pressing the `Admin interface` button on the home screen, you are taken to the admin interface.

Using the `Refresh` button, you can see the list of all the activities stored on the server. Using the other two
buttons located at the bottom of the screen, you can delete selected activities, and add new ones.

Additionally, by double-clicking on an attribute of an activity, you can edit it directly.

## How to contribute to it

We are open to merge requests, with the condition that they must be reviewed and approved by at least 2
original group members before being merged into the project.

## Copyright / License (opt.)

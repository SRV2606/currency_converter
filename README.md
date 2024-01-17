

Major Focus: Code Architecture,Code Packaging , Code Documentation, Network Calls ,  Production Level CodeBase With Base Classes, HILT,   clean coding  styles, used CLEAN ARCHITECTURE  with MVVM and latest tech

Tech Stack : Flow, Hilt, MVVM, coroutines , kotlin, higher order fns, lazy initialization,, mappers, retrofit caching, clean arch, api calls , http 



Problem Statement : 
The app will consist of the following screens:
● Points Table
○ This screen will show the points table calculated by the results of all the matches played.
○ The points table will be sorted in descending order by points scored by a player
○ In case of a tie (same points scored by multiple players), sort the players in descending order of
the total score of all the matches played by the player

● Matches Screen
○ Users can click on a player from the Points Screen and land on a detailed screen, where the
details of all the matches played by the player will be present
○ This screen should show the most recent match played at the top and the oldest match at the
bottom.
○ This screen shows the actual score for both the players for the matches where the selected player
was participating (the screenshot attached shows all the matches played by Princess Leia)
○ Use proper colors to identify whether the match was won/lost/drawn by the player. Colors to be
used:
■ Win - Green
■ Loss - Red
■ Draw - White



Clean Arch : 
Domain : usecases, repository, beans 
Data : servermodels, repositoryimpl,mappers ,api serv,
App: ui , viewmodels, hilt injection 







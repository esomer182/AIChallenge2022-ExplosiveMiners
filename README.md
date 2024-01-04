# AI-Challenge-2022-Explosive-miners

AI Challenge is an artificial intelligence competition in Java open to high school students from Catalonia. Individually or as a team, you must implement the strategy for a given game and test your skills against the strategies of other players.

ExplosiveMiners is the game used in the third edition of the AI Challenge, in May 2022. This contest was organized in collaboration with the Catalan Olympiad in Informatics (OIcat). I was the winner of the contest.
I will only **briefly** describe my idea. You can read about the game here: https://www.coliseum.ai/material?lang=en&tournament=explosiveminers

## Idea:
During the first round my strategy focuses on creating a lot of miners from the start to collect resources early. Then, on the middle game, I try to code as many soldiers as possible to protect the miners and the HQ, finally towards the late game I switch to miners again, this time to create an army of explosive miners. Lastly, from the thousandth round onwards, I use the resources to buy victory points.

The miners explode if at any point they kill enemy troops with more value than the friendly troops it is killing (itself plus possibily some other troops).

The soldiers turn into towers for the late game, forming a wall-like structure near the HQ.

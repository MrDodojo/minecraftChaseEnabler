# minecraftChaseEnabler
This is a fabric minecraft mod that enables a hidden command in 1.18 experimental snapshot 6 and 7, the mod also backports the client feature to older versions.
Chase is showcased by a Minecraft developer in [this tweet](https://twitter.com/henrikkniberg/status/1432375731586277376).

## Requirements
Fabric. No fabric api required

## How to use chase
Chase exists of a server and one of more clients.

**/chase me** activates the chase server. (1.18 experimental snapshot 6 and 7 only)

**/chase [server-ip] [server-port]** let the clients connect with a chase server, *server-ip* and *server-port* can be left blank if the server is on the same computer as the client. (any version)

I recommend turning **/gamerule sendCommandFeedback** to **false** and turning Pause on lost focus off with **F3 + P**

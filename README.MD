[PVP Flagging Logo](https://media.forgecdn.net/attachments/797/780/pvp_flagging_logo.png)

<a href="https://discord.gg/TRzEdrndM2"><img src="https://img.shields.io/discord/1104430139275743293.svg?label=&amp;logo=discord&amp;logoColor=ffffff&amp;color=7389D8&amp;labelColor=6A7EC2&amp;style=for-the-badge" alt="" width="129" height="28" /></a>

## Core Features
- Players can flag or unflag themselves to opt into PVP
- Both players must be flagged for PVP to occur
- Admins can create areas for PVP that will auto flag players when they enter the area (with warning as you get close).
- Nametag overlay for visible PVP indicator
- Unflagging yourself is not instant.  Will be unflagged in 60 seconds. This is configurable
- When waiting to be unflagged if you deliver or take player damage your unflag request will be cancelled.
- Logging out auto cancels your unflag request


## Commands
- /pvpFlag [on | off | status] sets your pvp state
- /pvpZone [add | remove | list] allow admins to define PVP zones and the size of the warning area around them.


## Config
- player_login_state = PRESERVE | FLAG | UNFLAG
- player_respawn_state = PRESERVE | FLAG | UNFLAG
- unflag_wait_time_ticks = 1200
- pvp_zone_bounds_check_ticks = 20

![Flagged Player](https://media.forgecdn.net/attachments/797/812/pvp_flagging_image_1.png "Nametag")

![Flag](https://media.forgecdn.net/attachments/797/813/pvp_flagging_image_2.png "Flag")

![Unflag](https://media.forgecdn.net/attachments/797/814/pvp_flagging_image_3.png "Unflag")

![Warn](https://media.forgecdn.net/attachments/797/815/pvp_flagging_image_4.png "Warn")

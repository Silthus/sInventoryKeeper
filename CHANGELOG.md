### Initial Features

* definable item groups inside `item-groups`
* comes with two default item groups: `weapons` and `armor`
* define inventory keeper configurations inside `configs/` *see tests for examples*
* comes with two modes per config `WHITELIST` and `BLACKLIST`
  - `WHITELIST`: drop all items except the ones defined in the config
  - `BLACKLIST`: keep all items except the ones defined in the config
* each config represents a unique permission and applies only if the permission is assigned to the player
* a player can have multiple configs applied to them
* everything is unit tested

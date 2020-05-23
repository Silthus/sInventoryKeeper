# 1.0.0 (2020-05-23)


### Features

* initial commit ([d321b91](https://github.com/Silthus/sInventoryKeeper/commit/d321b91d2cc7fcc2411f1281c67969f8565b2587))

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

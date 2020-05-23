# sInventory

[![Build Status](https://github.com/Silthus/sInventoryKeeper/workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)
[![codecov](https://codecov.io/gh/Silthus/sInventoryKeeper/branch/master/graph/badge.svg)](https://codecov.io/gh/Silthus/sInventoryKeeper)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/Silthus/sInventoryKeeper?include_prereleases&label=release)](../../releases)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

A Spigot plugin that enables you to let your players keep a selection of their inventory on death. You can create multiple configs and assign different permissions for each config.

> This can be useful for servers that want their different donors to keep different parts of their inventory.

- [Features](#features)
- [Installation](#installation)
- [Configuration](#configuration)
  - [Item Groups](#item-groups)
  - [Inventory Keeper Configs](#inventory-keeper-configs)
- [Commands](#commands)

## Features

- Group items as `item-groups`  and use them inside the `inventory-keeper-configs`.
  - comes with two default groups: `weapons` and `armor`
- Create multiple `inventory-keeper-configs` for different ranks, groups, etc.
  - each config gets its own unique permission and is only applied to players that have that permission
  - configs can be mixed and matched
- Two config modes: `WHITELIST` and `BLACKLIST` for maximum control
  - `WHITELIST`: drop all items except the ones defined in the config
  - `BLACKLIST`: keep all items except the ones defined in the config
- Players can have multiple configs assigned to them
- over 85% unit test coverage

## Installation

Get the [latest release](../../releases/latest) and drop it into your `plugins/` directory.
Restart the server and take a look at the generated default configs inside the `sInventoryKeeper` plugin folder.

## Configuration

There are two types of configs `item-groups` and `inventory-keeper-configs`. You technically don't need to define item groups and can just use the keeper configs.
But to make your life easier it is adviced to **create common item groups** and reuse them inside the keeper configs.

### Item Groups

Item Groups are defined inside the `item-groups/` directory. You can find two examples (`weapons.yaml` and `armor.yaml`) and create more yourself.
The item group config only contains a `items` section and a **`name` which must be unique**.

> If you leave the `name` empty, the plugin will choose a unique name based on the location of the config.
> You can always change this later, but must adapt your `inventory-keeper-configs` as well.

```yaml
# delete this line to default to a unique name based on the location of the config
name: armor
# define your items that belong to this group here
# you don't need to prefix them with minecraft:
items:
- minecraft:leather_helmet
- minecraft:leather_chestplate
- minecraft:leather_leggings
- minecraft:leather_boots
- minecraft:chainmail_helmet
- minecraft:chainmail_chestplate
- minecraft:chainmail_leggings
- minecraft:chainmail_boots
- minecraft:iron_helmet
- minecraft:iron_chestplate
- minecraft:iron_leggings
- minecraft:iron_boots
- diamond_helmet
- diamond_chestplate
- diamond_leggings
- diamond_boots
- golden_helmet
- golden_chestplate
- golden_leggings
- golden_boots
```

### Inventory Keeper Configs

The meat of the plugin are the `inventory-keeper-configs` located inside the `configs/` folder.
Each config will have its own unique permission that can be assigned to players.
The permission always consists of a prefix (`sinventorykeeper.config.`) and the unique name of the file based of its path.

If you have a config named `example.yaml` inside the `configs/` directory, its permission will be: `sinventorykeeper.config.example`.

> Tip: the permissions for each config will be written in the log on startup.

A config looks like this.

```yaml
# >ou can define two different modes that will control how this config gets loaded.
# Allowed values (case sensitive): WHITELIST or BLACKLIST
# WHITELIST: only the defined items are kept
# BLACKLIST: all items excluding the defined items are kept
mode: WHITELIST
# Set this to false if you want to disable the config.
enabled: true
# Define a list of item groups that should be kept or dropped (depending on the mode).
# You can create custom item groups inside the item-groups/ folder.
item_groups:
- armor
# Define a list of items that should be kept or dropped (depending on the mode).
items:
- stone
- dirt
```

## Commands

Currently there are now commands. You need to restart the server for the configs to take effect.

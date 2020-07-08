# [3.0.0](https://github.com/Silthus/sInventoryKeeper/compare/v2.1.0...v3.0.0) (2020-07-08)


### Bug Fixes

* filter all inventory contents - including armor ([aae1123](https://github.com/Silthus/sInventoryKeeper/commit/aae11237de4d3228f01f729ec7d148a899609ba6)), closes [#6](https://github.com/Silthus/sInventoryKeeper/issues/6)
* worn armor and shields are duplicated on death ([01c0187](https://github.com/Silthus/sInventoryKeeper/commit/01c018714dd187b7663bbaacd6319202ee19bef5)), closes [#6](https://github.com/Silthus/sInventoryKeeper/issues/6)


### Features

* add `sinventorykeeper.keep-all` permission that automatically keeps all items regardless of the config ([5135b42](https://github.com/Silthus/sInventoryKeeper/commit/5135b4258d76a73c73610d484fa5ade43b93954f)), closes [#2](https://github.com/Silthus/sInventoryKeeper/issues/2)
* allow configuration of combination_mode and messages ([32c04e6](https://github.com/Silthus/sInventoryKeeper/commit/32c04e6443885b5a63f1ed839ba20340efce96cb)), closes [README.md#changes-in-v3](https://github.com/README.md/issues/changes-in-v3) [#3](https://github.com/Silthus/sInventoryKeeper/issues/3)
* OPs now need permissions by default ([5d75747](https://github.com/Silthus/sInventoryKeeper/commit/5d757479cbb0c23146b826da7303bf289df8e3df)), closes [#2](https://github.com/Silthus/sInventoryKeeper/issues/2)


### BREAKING CHANGES

* OPs are no longer automatically ignored by the plugin. Set `ignore_op: true` inside the `config.yml` to revert this change.
* players can only have one kind of a filter attached to them. All configs that apply to a single player must all be `WHITELIST` or `BLACKLIST`.

# [2.1.0](https://github.com/Silthus/sInventoryKeeper/compare/v2.0.1...v2.1.0) (2020-06-25)


### Features

* **build:** split build and test into separate jobs ([1da718b](https://github.com/Silthus/sInventoryKeeper/commit/1da718b6b31ca68d8e6224487fb22463f7cf5447))
* update to spigot/minecraft 1.16.1 ([90f7c41](https://github.com/Silthus/sInventoryKeeper/commit/90f7c41ba8cb5a1fdea044926b0684831cdf1699))

## [2.0.1](https://github.com/Silthus/sInventoryKeeper/compare/v2.0.0...v2.0.1) (2020-06-10)


### Bug Fixes

* shade acf-bukkit into jar ([aebd50c](https://github.com/Silthus/sInventoryKeeper/commit/aebd50c0e2f8a1855bc18444e92837edd2996e47))

# [2.0.0](https://github.com/Silthus/sInventoryKeeper/compare/v1.0.4...v2.0.0) (2020-06-09)


### Bug Fixes

* **config:** do not load disabled configs ([7a1dd97](https://github.com/Silthus/sInventoryKeeper/commit/7a1dd9731cb1aee91d66537e4fec2cace7c62dcf))


### Features

* **api:** add option to register custom InventoryFilters ([71a39cd](https://github.com/Silthus/sInventoryKeeper/commit/71a39cd13d64b496ff0ab17289a737ab2d9ffe37))
* **cmd:** add `/sik reload` command ([5b87fcf](https://github.com/Silthus/sInventoryKeeper/commit/5b87fcf9bbad027e783b1657f140d10dcdf1739a))


* feat!: use slib as dependency injection framework ([c0c7719](https://github.com/Silthus/sInventoryKeeper/commit/c0c77191214a7f41afc57f8e39e62bd0f2469bb0))


### BREAKING CHANGES

* requires at least [sLib 1.2.4](https://github.com/Silthus/sLib/releases/tag/v1.2.4)

## [1.0.4](https://github.com/Silthus/sInventoryKeeper/compare/v1.0.3...v1.0.4) (2020-05-24)


### Bug Fixes

* **release:** set GH_URL to set github api url ([eaec5f3](https://github.com/Silthus/sInventoryKeeper/commit/eaec5f3b008f787eba078df8a55f509043bdc6a6))

## [1.0.3](https://github.com/Silthus/sInventoryKeeper/compare/v1.0.2...v1.0.3) (2020-05-23)


### Bug Fixes

* **build:** remove node cache ([27a827e](https://github.com/Silthus/sInventoryKeeper/commit/27a827e3393776f1b9304dd3e91592294c343591))

## [1.0.2](https://github.com/Silthus/sInventoryKeeper/compare/v1.0.1...v1.0.2) (2020-05-23)


### Bug Fixes

* **permission:** change permission prefix to `sinventorykeeper.config.` ([8e81049](https://github.com/Silthus/sInventoryKeeper/commit/8e81049720ef5af4bdb04d17663880e3f2a57a90))

## [1.0.1](https://github.com/Silthus/sInventoryKeeper/compare/v1.0.0...v1.0.1) (2020-05-23)


### Bug Fixes

* **release:** do not publish shadowJar ([7630a74](https://github.com/Silthus/sInventoryKeeper/commit/7630a7444d11c80f7d4b0f88722b83c861133c9c))

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

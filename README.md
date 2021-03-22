Broken Wings
============

Broken Wings is a creative flight blocker. It works per-dimension.

The default settings block flight in dimension 7, the Twilight Forest. It can be configured as a whitelist or a blacklist for dimensions to allow flight in, or to blanket-ban all creative flight in all dimensions. You can also choose to block elytra flight.

**This work is dedicated to the public domain.**

## Compiling

Hey, this is me from the future, in 2021! It looks like whatever version of ForgeGradle this used oes not work with Forge's weird jar-in-jar system or something so it explodes at runtime because Wings can't find llibrary-core.

* Download LLibrary off of curse.
* Crack it open in a zip file program and pull out `llibrary-core-<version>.jar`
* Put it in `run/mods`.
* Happy hacking.

## Adding extra conditions

Maybe you want to have an item that lets you bypass the no-flight rule, but only in certain dimensions or with certain metadata values associated with the item.

The syntax for the `whitelistArmor` and `whitelistInventory` config options is as follows:

* `modid:item_name`, to allow this item to unconditionally bypass no-flight rules
* `modid:item_name dimension:7`, to allow this item to bypass no-flight rules in dimension 7
* `modid:item_name meta:10`, to allow this item to bypass no-flight rules if it has metadata 10
* `modid:item_name dimension:7 meta:10`, to allow this item to bypass no-flight rules in dimension 7 if it has metadata 10

Or, in other words:

* to always add an item to the whitelist, write the item ID
* to add extra conditions to the whitelistiness of an item, write the item ID, a space, and then the extra conditions
* to add a dimension condition, write `dimension:` and then the numeric dimension ID
* to add a metadata condition, write `meta:` and then the metadata

Some notes:

* you can only add one of each condition per row in the config file (i.e. `some:thing meta:0 meta:1 meta:2` doesn't work)
  * but it is OK to repeat item IDs in the config file
* you can use a range of consecutive values by putting a tilde character `~` in between, (i.e. `some:thing meta:0~2`)
  * both ends of the range are inclusive
  * you cannot use a non-consecutive range, but see above note (just add another entry)
* Broken Wings only does anything at all in the dimensions you tell it to using the other config options; adding `dimension:` conditions to an item does not change that.
* Dimension conditions don't play too nicely with the tooltip feature; it won't show anything like "you can use this item to fly in another dimension" or anything like that. Maybe I'll add it later - it's kinda tricky due to how things are set up rn - if you need it really bad, open an issue.
  * You can always disable my tooltips in the config file and make better ones with CraftTweaker.
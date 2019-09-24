# TOC
* [About](#about)
* [Utilities](#utilities)
  + [Item NBT](#item-nbt)
  + [Entity NBT](#entity-nbt)
  + [Tile entity NBT](#tile-entity-nbt)
  + [NBT parser](#nbt-parser)
* [Usage](#usage)
* [Examples](#examples)
    - [Mark an item](#mark-an-item)
    - [When pigs can fly](#when-pigs-can-fly)
    - [Locking chests](#locking-chests)
- [Changelog](#changelog)
  * [Breaking changes in 1.0.2](#breaking-changes-in-102)
    + [All utils](#all-utils)
    + [ItemNBTUtil](#itemnbtutil)


# MiniNBT
A small NBT library to allow the modification of NBT tags across different Minecraft versions.  
Currently supported are 1.8.8 up to 1.14.4, but very likely also future and *maybe* lower versions.

## About
This is a small standalone library providing an easy way to interact with NBT (Notch's Binary Format).
It is currently capable of reading and modifying `Item`, `Entity` and `TileEntity` NBT.


## Utilities

### Item NBT
You can modify item NBT with the `ItemNBTUtil`.  
Supported are:
  - Setting a tag
  - Getting a tag

*Custom NBT on items is preserved, so you can store whatever you want.*

### Entity NBT
You can edit entity NBT (`NoGravity` and whatever other tag you like). Many of those settings are now exposed through Bukkit methods though.  
*Custom tags are never read by Minecraft and therefore impossible.*

### Tile entity NBT
`TileEntities` are blocks with some special data, like furnaces, chests, and so on.
It can be used to change the name of a furnace/chest or similar after it was placed, or do some other trickery.

*Custom tags are never read by Minecraft and therefore impossible.*

### NBT parser
This allows you to parse a String to a NBTTagCompound. It uses the `MojangsonParser` internally, but you will need to use the `NbtParser` class.

## Usage
This utility follows the exact same structure as the Minecraft tags do, so you can just change your imports and things might work. If not, it shouldn't be hard to figure out the small differences.  
The `NBTTagCompound` can serve as a good starting point.

## Examples
#### Mark an item
```java
ItemStack item = player.getInventory().getItemInMainHand();

NBTTagCompound compound = ItemNBTUtil.getTag(item); // fetch the data
String dataKey = "My custom data"; // the key to store it under
if(compound.hasKey(dataKey)){
    player.sendMessage(ChatColor.GOLD + "Got: "  + ChatColor.GREEN + compound.getString(dataKey));
} else {
    compound.setString(dataKey, String.join(" ", args)); // set it to command arguments
    item = ItemNBTUtil.setNBTTag(compound, item);  // setNBTTag returns a new item and does not modify it
    player.getInventory().setItemInMainHand(item); // overwrite the item to apply the tag
}
```

#### When pigs can fly
```java
Entity pig = player.getNearbyEntities(5, 5, 5).stream()
        .filter(it -> it instanceof Pig)
        .findFirst().get();
NBTTagCompound compound = EntityNBTUtil.getNbtTag(pig);
System.out.println(compound); // prints all data
compound.setBoolean("NoGravity", !compound.getBoolean("NoGravity"));
EntityNBTUtil.setNbtTag(pig, compound); // overwrites the entity's data
```

#### Locking chests
```java
Block block = player.getLocation().getBlock();
if(block.getType() == Material.AIR){
    block = block.getRelative(BlockFace.DOWN);
}
NBTTagCompound compound = TileEntityNBTUtil.getNbtTag(block.getState());
System.out.println(compound); // prints all data
compound.setString("Lock", "Test"); // locks it to "Test"
TileEntityNBTUtil.setNbtTag(block.getState(), compound); // applies the changes
```

# Changelog

## Breaking changes in 1.0.2
It should be ABI/API compatible except for the changes outlined below.
### All utils
* `ReflectionException` will now be thrown if initialization or any other operation fails.

### ItemNBTUtil
* `getTag` now throws an exception if the tag was no compound or is unknown instead of returning an empty tag.
  This will probably not impact you at all, but it still is a change in behaviour.

# MiniNBT
A small NBT library to allow the modification of NBT tags across different versions

## About
This is a small library, originally written for [PercieveCore](https://github.com/PerceiveDev/PerceiveCore) and adapted to be a standalone utility.  
It provides an easy way to interact with NBT (Notch's Binary Format), but only the interaction with item NBT is really tested ;)

### Item NBT
You can modify item NBT with the `ItemNBTUtil`.  
Supported are:
  - Setting a tag
  - Getting a tag

*The custom NBT on items is preserved.*

### Entity NBT
You can also modify entity NBT (`EntityNBTUtil`), but I think there are methods for that in Spigot now.  

*Custom tags are never set and therefore impossible.*

### Tile entity NBT
This allows you to modify the NBT of `TileEntities` (`TileEntityNBTUtil`), like a chest.  
It can be used to change the name of a furnace/chest or similiar after it was placed, or do some other trickery.

*Custom tags are never set and therefore impossible.*

### NBT parser
This allows you to parse a String to a NBTTagCompound. It uses the `MojangsonParser` internally, but you will need to use the `NbtParser` class.

## Usage
Quite easy. I have _nearly_ exactly recreated the original NBT classes, so you can use it in the same way you would use the original.  

You can refer to the format on the MinecraftWiki pages and you will find any class you find there here too. With the same name.

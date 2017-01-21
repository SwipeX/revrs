This is a tool with the central purpose of debugging and exploring the loaded classes in a JVM. 

The intention of this code is to be open source. The point is to learn more about reverse engineering.

The intended feature (In order of importance) set includes:

* Hierarchy Tree View
    * Editable (Insert/Remove/Move) 

* Real Time Value Debugging
    * Value watches
    * Threshold values (alert)
    * On screen debugging allowed

* Object Explorer
    * Stems from RTD
    * Explores any non-array object
    * No limit on depth

* Array Explorer (for any size dimension)
    * Provides RTD access to object explorer for arrays
    * Should be able to filter subsets in array

* Member Identification Utility
    * Easy to use / clean framework
    * Standard format (I/O both) [CSV likely]
    * Work in-line with RTD for debugging (refactor)
    * Output Support

* Real Time Injection (note: able to be hot-swapped with changes)
    * For use in conjunction with MIU 
    * Keep initial classes, hot-swap when needed
    _There are really two options for this. One is to restart the jar after re-injecting, so reflection may be the most beneficial here. However, http://hotswapagent.org/ could also be used as it allows for you to specify a custom vm that allows for full class structure swapping. This will not be considered a priority atm._

* Member Transform Support
    * Would be for callback / deob
    * Not essential for the initial purpose
    
    
Things revrs is *NOT*:
   * An IDE.  While it will support MIU, it will not provide a means to do so, you can code in your IDE of choice and point to the files.
   * A substitute for an updater. revrs will assist you in locating necessary hooks and values, however, you must do the work and maintain updates.
   * A teacher. You will still need to gain knowledge and learn on your own or from another person, this is not an instructional teaching software.
   
Uses a condensed version of ASM5 adapted by myself and @tsedlar.
https://github.com/SwipeX/ASM-5-Condensed
        
    


# Enigma: (Yet Another Enigma) Emulator

This is a bit of old code I dug out recently.

It's possibly the first Java GUI app I ever wrote and is an Enigma encryption machine emulator capable of either 3 or 4 rotor operation.

There's no attempt here to replicate any kind of look and feel of the original. Instead there's a very plain and functional GUI and plenty of options for reading/writing files and configuration settings via menus.

To compile the source go into the /src directory and just do 'make all'.

That will create an EnigmaApp.jar which is placed in the root directory.

In that root directory, ./regressMe will run the full regression suite of tests (or rather texts in this case).

You can fire up the emulator to run interactively using

    java -jar EnigmaApp.jar

<img src="https://github.com/revaldinho/cpc_ram_expansion/blob/master/Enigma.png" alt="Macintosh Screenshot" width="640">

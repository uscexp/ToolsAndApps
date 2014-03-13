Sms2Gba - DrSMS rom injector

What is DrSMS?

Excerpt Reesy:
'DrSMS is a Sega Master System (SMS) emulator for the Gameboy Advanced. It is written in 100% ARM ASM and
compiled using the Goldroad compiler. I have written the Z80 core from scratch using a few ideas from TheHiVE,
which makes it pretty nifty. The Z80 is the main CPU for the SMS just in case you were wondering what it is.'

Information taken from 'http://uk.geocities.com/dave_rees_uk/drSMS.htm'
For more Info on DrSMS and more Reesy stuff see 'http://uk.geocities.com/dave_rees_uk/index.htm'.

What is Sms2Gba?

Sms2Gba is a rom injector for the DrSMS Emulator. With the DrSMS-Rom (sms.bin) and a selection of
SMS-Roms you can build a GameBoy Advance Rom which you can flash on a Flash-Card (for more info on that
see your Flash manufacturer manual or website) and play it with your GameBoy Advance or you can play it
with a GameBoy Advance Emulator.

Why did I do Sms2Gba?

Because I wanted to support the great work Reesy did with his DrSMS emulator, so he hasn't to waste his
time with building that injector stuff and can concentrate to advance with his emulator. Well my skills
with assembler on GBA are very, very limited ( == 0) and also my intrest in learning assembler for
the GBA is very, very limited ( == 0). So when Reesy offered that job for the injector on his website
I thought thats the way I can support his work and here is it, my way to say "thank you" to Reesy.

Delivery:

Sms2Gba is delivered within a zip-archive. This archive contains the following files:

readme.txt (this file)
license.txt (software license)
Sms2Gba.jar (java archive)
Sms2Gba.bat (Windows batch file)
Sms2Gba.sh (Unix shell script)

Requirements:

Because Sms2Gba is implemented in Java (http://java.sun.com) you need the Java Runtime Environment (J2SE) v1.3
(http://java.sun.com/j2se/1.3/download.html) or higher.

Installation:

Unpack the zip-archive to a location of your choice. If you retain the file structure within
the archive it will create a directory called 'Sms2Gba' which contains all the programm files.

Running Sms2Gba:

Windows OS:

If your JRE is correct installed you can start the program just with double clicking on the Sms2Gba.jar
file. Otherwise you can use the Sms2Gba.bat file to start the program.

Unix OS:

Make the Sms2Gba.sh file executable and use it to start the program.

All OS:

At the commandline type 'java -jar Sms2Gba.jar' and enter to start the program.

Features:

Add SMS roms directly or from archive files. Supported are: zip, jar, tar, gz, bz2.
Create a GBA rom from the DrSMS rom and the inserted SMS roms.
It is possible to test a created GBA rom directly with your GBA emulator. To do this just select
your GBA emulator in the 'GBA Emu' field and if you want you can add some parameters for the emulator
after the emulator program name (if needed). Than just choose 'Execute emulator'.

Licence:

The software is distributed under a freeware licence in binary form without source. Please read the
'license.txt' file for detailed information on the license which you accept by using this software!

Feedback:

You can email your feedback (also positiv feedback) and bug reports to 'sms2gba@moe99.cjb.net'.

If you find a bug please email it to me with detailed description and info:
- your OS
- your JRE version
- Sms2Gba version (see about dialog)
- stack trace (will appear in cmd-shell, only available if you start with 'java' and not with 'javaw' or doubleclick (WinOS))
- process description: how does the error appear

History:

Version 0.4:

- Real first public release.
- Game Gear rom support added.

Version 0.3:

- First public release.

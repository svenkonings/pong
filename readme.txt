Plaats de .jar en de bestanden uit de C map in dezelfde map
compileer c met: 
	gcc -std=c11 -lwiringPi -shared -O3 -I/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include -I/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include/linux Gpio.c -o libGpio.so
Voer de .jar uit met:
	sudo java -Djava.library.path=. -jar <bestandsnaam>.jar
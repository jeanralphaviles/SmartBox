MMCU=atxmega128a1u
PARTNO=x128a1u
PROGRAMMER=atmelice_pdi

CC=avr-gcc
FLASHER=avrdude
LINKER=avr-objcopy

SRCS=$(wildcard *.c)
OBJECTS=$(SRCS:.c=.o)
EXECUTABLE=main
IMAGE=$(EXECUTABLE).hex

CFLAGS=-Wall -Wno-cpp -Os -std=gnu11 -mmcu=$(MMCU)
LINKERFLAGS=-O ihex $(EXECUTABLE) $(IMAGE)

FLASHERFLAGS=-p $(PARTNO) -c $(PROGRAMMER) -U flash:w:$(IMAGE):i -b 1115200

all: $(IMAGE)

flash: all
	sudo $(FLASHER) $(FLASHERFLAGS)

$(IMAGE): $(EXECUTABLE)
		$(LINKER) $(LINKERFLAGS)

$(EXECUTABLE): $(OBJECTS)
		$(CC) $(CFLAGS) $(OBJECTS) -o $@

clean:
		rm -rf *.o $(EXECUTABLE) $(IMAGE)

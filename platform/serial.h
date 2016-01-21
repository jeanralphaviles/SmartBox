#ifndef SERIAL_H
#define SERIAL_H

#include "helpers.h"

#if F_CPU == 32000000
  #define BSCALE -4
  #define BSEL 3317
#elif F_CPU == 2000000
  #define BSCALE -7
  #define BSEL 1539
#endif

#include <stdio.h>

extern volatile char last_serial;

// Initializes USART, sets stdout to serial_putchar.
void init_usart(void);

// Puts a character to the file.
int serial_putchar(char c, FILE* stream);

#endif

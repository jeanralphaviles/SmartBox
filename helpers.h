#ifndef HELPERS_H
#define HELPERS_H

#define  F_CPU        32000000
#include <util/delay.h>

// Delay for a time.
void delay_ms(int ms);

// Set the system interrupt mask.
void intmask_set(uint8_t mask);

// Enable system interrupts.
void enable_int(void);

// Disable system interrupts.
void disable_int(void);

// Set the system sleep mode to use.
void sleep_mode_set(uint8_t mode);

// Enable the sleep system.
void enable_sleep();

// Set the processor to sleep, awoken by interrupts.
void sleep();

// Disable the sleep system.
void disable_sleep();

// Set the System Clock to 32Mhz
void clk_32(void);

#endif

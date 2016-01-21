#include "helpers.h"

#include <avr/interrupt.h>
#include <avr/io.h>
#include <avr/sleep.h>

void delay_ms(int ms) {
  while (ms--) {
    // Workaround for preprocessor issue
    _delay_ms(1);
  }
}

void intmask_set(uint8_t mask) {
  PMIC_CTRL = mask;
}

void enable_int(void) {
  sei();
}

void disable_int(void) {
  cli();
}

void sleep_mode_set(uint8_t mode) {
  set_sleep_mode(mode);
}

void enable_sleep(void) {
  sleep_enable();
}

void sleep(void) {
  sleep_cpu();
}

void disable_sleep(void) {
  sleep_disable();
}

void clk_32(void) {
  OSC_CTRL |= OSC_RC32MEN_bm; // Turn on 32MHz crystal
  while(!(OSC_STATUS & OSC_RC32MRDY_bm)) {
    // Wait for crystal to stabilize.
  }
  CCP = CCP_IOREG_gc; // Remove register protections.
  CLK_CTRL = CLK_SCLKSEL_RC32M_gc; // Select 32MHz crystal
}


#include "serial.h"

#include <avr/interrupt.h>
#include <avr/io.h>

volatile char last_serial = '\0';
static FILE serial_outc = FDEV_SETUP_STREAM(serial_putchar, NULL,
                                            _FDEV_SETUP_WRITE);
void init_usart(void) {
  PORTC_DIRSET = PIN3_bm;  // Tx to output
  PORTC_OUTSET = PIN3_bm;  // Tx default output
  PORTC_DIRCLR = PIN2_bm;  // Rx to input
  PORTC_OUTCLR = PIN2_bm;  // Rx to default output
  PORTQ_DIRSET = PIN3_bm | PIN1_bm;  // USB mapping
  PORTQ_OUTSET = PIN1_bm;  // Disable USB bride

  USARTC0_BAUDCTRLA = BSEL & USART_BSEL_gm; // BSEL
  USARTC0_BAUDCTRLB = (((BSCALE << USART_BSCALE_gp) & USART_BSCALE_gm) |
      ((BSEL >> 8) & 0x0F));

  USARTC0_CTRLA = USART_RXCINTLVL_HI_gc ; // High level interrupts for Rx
  USARTC0_CTRLC = USART_CHSIZE_8BIT_gc; // 8 bit Transmission size.
  USARTC0_CTRLB = USART_RXEN_bm | USART_TXEN_bm; // Enable Rx, Tx
  stdout = &serial_outc;
}

int serial_putchar(char c, FILE* stream) {
  if (c == '\n') {
    serial_putchar('\r', stream);
  }
  while ((USARTC0_STATUS & USART_DREIF_bm) == 0) {
    // Data register not yet empty.
  }
  USARTC0_DATA = c;
  return 0;
}

ISR(USARTC0_RXC_vect) {
  last_serial = USARTC0_DATA;
}


#include "serial.h"

#include <avr/interrupt.h>
#include <avr/io.h>

volatile char last_serial = '\0';
static FILE serial_outc = FDEV_SETUP_STREAM(serial_putchar, NULL,
                                            _FDEV_SETUP_WRITE);
void init_usart(void) {
  PORTD_DIRSET = 0x08;  // Tx to output
  PORTD_OUTSET = 0x08;  // Tx default output
  PORTD_DIRCLR = 0x04;  // Rx to input
  PORTQ_DIRSET = 0x0A;  // PortD -> USB
  PORTQ_OUTCLR = 0x0A;  // Default output

  USARTD0_BAUDCTRLA = BSEL & USART_BSEL_gm; // BSEL
  USARTD0_BAUDCTRLB = (((BSCALE << USART_BSCALE_gp) & USART_BSCALE_gm) |
      ((BSEL >> 8) & 0x0F));

  USARTD0_CTRLA = USART_RXCINTLVL_HI_gc ; // High level interrupts for Rx
  USARTD0_CTRLB = USART_RXEN_bm | USART_TXEN_bm; // Enable Rx, Tx
  USARTD0_CTRLC = USART_CHSIZE_8BIT_gc; // 8 bit Transmission size.
  stdout = &serial_outc;
}

int serial_putchar(char c, FILE* stream) {
  if (c == '\n') {
    serial_putchar('\r', stream);
  }
  while ((USARTD0_STATUS & USART_DREIF_bm) == 0) {
    // Data register not yet empty.
  }
  USARTD0_DATA = c;
  return 0;
}

ISR(USARTD0_RXC_vect) {
  last_serial = USARTD0_DATA;
}


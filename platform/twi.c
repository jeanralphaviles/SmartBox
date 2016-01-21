#include "twi.h"

#include <avr/io.h>
#include <stdbool.h>
#include <stdio.h>
#include <util/twi.h>

#include "helpers.h"

void init_twi(TWI_t* port) {
  // 200us timeout on TWI actions
  port->MASTER.CTRLB = TWI_MASTER_TIMEOUT_200US_gc;
  // Set baud rate as defined in header
  port->MASTER.BAUD = BAUD(F_CPU, F_TWI);
  // Enable TWI
  port->MASTER.CTRLA = TWI_MASTER_ENABLE_bm;
  // Inform TWI that the bus is idle
  port->MASTER.STATUS = TWI_MASTER_BUSSTATE_IDLE_gc;
}

bool twi_write_reg(TWI_t* port, uint8_t address, uint8_t reg,
                   const uint8_t* buffer, uint8_t buff_size) {
  uint8_t timeout = 0;
  while ((port->MASTER.STATUS & TWI_MASTER_BUSSTATE_gm) !=
          TWI_MASTER_BUSSTATE_IDLE_gc) {
    // Waiting for TWI bus to become idle
    if (timeout++ > TWI_IDLE_TIMEOUT_MS) {
      // Timeout
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
    delay_ms(1);
  }

  // Broadcast device address
  port->MASTER.ADDR = address << 1;
  while ((port->MASTER.STATUS & TWI_MASTER_WIF_bm) == 0) {
    // Waiting for TWI transmission to complete
  }
  if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
    // Lost arbitration or illegal bus state error
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  }

  // Broadcast register address
  port->MASTER.DATA = reg;
  while ((port->MASTER.STATUS & TWI_MASTER_WIF_bm) == 0) {
    // Waiting for TWI transmission to complete
  }
  if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
    // Lost arbitration or illegal bus state error
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  }

  // Send buffer
  for (int i = 0; i < buff_size; ++i) {
    port->MASTER.DATA = buffer[i];
    while ((port->MASTER.STATUS & TWI_MASTER_WIF_bm) == 0) {
      // Waiting for TWI transmission to complete
    }
    if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
      // Lost arbitration or illegal bus state error
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
  }
  port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
  return true;
}

bool twi_read_reg(TWI_t* port, uint8_t address, uint8_t reg, uint8_t* buffer,
               uint8_t buff_size) {
  uint8_t timeout = 0;
  while ((port->MASTER.STATUS & TWI_MASTER_BUSSTATE_gm) !=
          TWI_MASTER_BUSSTATE_IDLE_gc) {
    // Waiting for TWI bus to become idle
    if (timeout++ > TWI_IDLE_TIMEOUT_MS) {
      // Timeout
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
    delay_ms(1);
  }

  // Broadcast device address
  port->MASTER.ADDR = address << 1;
  while ((port->MASTER.STATUS & TWI_MASTER_WIF_bm) == 0) {
    // Waiting for TWI transmission to complete
  }
  if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
    // Lost arbitration or illegal bus state error
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  }

  // Broadcast register address
  port->MASTER.DATA = reg;
  while ((port->MASTER.STATUS & TWI_MASTER_WIF_bm) == 0) {
    // Waiting for TWI transmission to complete
  }
  if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
    // Lost arbitration or illegal bus state error
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  }

  // Send stop bit
  port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;

  // Setup read
  port->MASTER.ADDR = ((address << 1) | 0x01);
  while ((port->MASTER.STATUS & (TWI_MASTER_RIF_bm | TWI_MASTER_ARBLOST_bm |
          TWI_MASTER_BUSERR_bm)) == 0) {
    // Waiting for TWI transmission to complete.
  }
  if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
    // Lost arbitration or illegal bus state error
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  }

  // Read buff_size bytes
  for (int i = 0; i < buff_size; ++i) {
    while ((port->MASTER.STATUS & (TWI_MASTER_RIF_bm | TWI_MASTER_ARBLOST_bm |
            TWI_MASTER_BUSERR_bm)) == 0) {
      // Waiting for a byte to be received
      PORTD_OUT = 0xFF;
    }
    if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
      // Lost arbitration or illegal bus state error
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
    // Read received data
    buffer[i] = port->MASTER.DATA;
    if (i < buff_size - 1) {
      // Not done receiving, send ACK
      port->MASTER.CTRLC = TWI_MASTER_CMD_RECVTRANS_gc;
    } else {
      // Done receiving, send NACK
      port->MASTER.CTRLC = TWI_MASTER_ACKACT_bm | TWI_MASTER_CMD_STOP_gc;
    }
  }
  port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
  return true;
}

bool twi_read(TWI_t* port, uint8_t address, uint8_t* buffer,
              uint8_t buff_size) {
  uint8_t timeout = 0;
  while ((port->MASTER.STATUS & TWI_MASTER_BUSSTATE_gm) !=
          TWI_MASTER_BUSSTATE_IDLE_gc) {
    // Waiting for TWI bus to become idle
    if (timeout++ > TWI_IDLE_TIMEOUT_MS) {
      // Timeout
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
    delay_ms(1);
  }

  // Setup Read
  timeout = 0;
  port->MASTER.ADDR = (address << 1) | 0x01;
  while ((port->MASTER.STATUS & (TWI_MASTER_RIF_bm | TWI_MASTER_ARBLOST_bm |
          TWI_MASTER_BUSERR_bm)) == 0) {
    // Waiting for TWI transmission to complete
    if (timeout++ > TWI_IDLE_TIMEOUT_MS) {
      // Timeout
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
    delay_ms(1);
  }
  if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  } else if (port->MASTER.STATUS & TWI_MASTER_RXACK_bm) {
    port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
    return false;
  }

  // Read buff_size bytes
  for (int i = 0; i < buff_size; ++i) {
    timeout = 0;
    while ((port->MASTER.STATUS & (TWI_MASTER_RIF_bm | TWI_MASTER_ARBLOST_bm |
            TWI_MASTER_BUSERR_bm)) == 0) {
      // Waiting for a byte to be received
     if (timeout++ > TWI_IDLE_TIMEOUT_MS) {
        // Timeout
        port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
        return false;
      }
      delay_ms(1);
    }
    if (port->MASTER.STATUS & (TWI_MASTER_ARBLOST_bm | TWI_MASTER_BUSERR_bm)) {
      port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
      return false;
    }
    buffer[i] = port->MASTER.DATA;
    if (i < buff_size - 1) {
      // Not done receiving, send ACK
      port->MASTER.CTRLC = TWI_MASTER_CMD_RECVTRANS_gc;
    } else {
      // Done receiving, send NACK
      port->MASTER.CTRLC = TWI_MASTER_ACKACT_bm | TWI_MASTER_CMD_STOP_gc;
    }
  }
  port->MASTER.CTRLC = TWI_MASTER_CMD_STOP_gc;
  return true;
}

void twi_scan(TWI_t* port) {
  printf("Scanning for TWI devices\r\n");
  for (uint8_t i = 0; i < 256; ++i) {
    if (twi_read(port, i, NULL, 0)) {
      printf("Device found at 0x%02X\r\n", i);
    }
  }
  printf("Scan Complete\r\n");
}


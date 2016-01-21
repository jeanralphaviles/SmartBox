#ifndef TWI_H_
#define TWI_H_

#include <avr/io.h>
#include <stdbool.h>

#define F_TWI 100000
#define TWI_IDLE_TIMEOUT_MS 10

#define BAUD(F_SYS, F_TWI) (F_SYS / (2 * F_TWI) + 5)

// Initializes TWI on given port
void init_twi(TWI_t* port);

// Writes a buffer a TWI register. Returns true if successful, false if not.
bool twi_write_reg(TWI_t* port, uint8_t address, uint8_t reg,
                   const uint8_t* buffer, uint8_t buff_size);

// Reads a buffer from a TWI register. Returns true if successful, false if not.
bool twi_read_reg(TWI_t* port, uint8_t address, uint8_t reg, uint8_t* buffer,
                  uint8_t buff_size);

// Reads a buffer from a TWI device. Returns true if successful, false if not.
bool twi_read(TWI_t* port, uint8_t address, uint8_t* buffer,
              uint8_t buff_size);

// Scans for TWI devices, outputs to stdout.
void twi_scan(TWI_t* port);

#endif

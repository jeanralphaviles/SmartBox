#include "camera.h"

#include <avr/io.h>
#include <stdbool.h>
#include <stdio.h>

#include "helpers.h"
#include "twi.h"

bool init_camera(TWI_t* port) {
  bool status;
  // Initialization Register / Data Pairs
  uint8_t payload[6][2] = {
    {0x30, 0x01},
    {0x30, 0x08},
    {0x06, 0x90},
    {0x08, 0xC0},
    {0x1A, 0x40}
  };
  for (uint8_t i = 0; i < 6; ++i) {
    status = twi_write_reg(port, CAMERA, payload[i][0], &payload[i][1], 1);
    if (status == false) {
      printf("Error in report 0x%02X, 0x%02X\n", payload[i][0], payload[i][1]);
      return false;
    }
    delay_ms(10);
  }
  delay_ms(100);
  return true;
}

bool read_camera(TWI_t* port, uint16_t payload[4][2]) {
  uint8_t buffer[16];
  // Read from the Camera
  bool status = twi_read_reg(port, CAMERA, 0x36, buffer, 16);
  if (status == false) {
    printf("Error reading camera!\n");
    return false;
  }
  // Translate the camera data into a series of coordinates.
  uint16_t temp;
  for (int i = 0; i < 4; ++i) {
    temp = buffer[3*i + 3];
    payload[i][0] = buffer[3*i + 1] + ((temp & 0x30) << 4);
    payload[i][1] = buffer[3*i + 2] + ((temp & 0xC0) << 2);
  }
  return true;
}

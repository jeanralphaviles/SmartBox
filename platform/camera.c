#include "camera.h"

#include <avr/io.h>
#include <stdbool.h>
#include <stdio.h>

#include "helpers.h"
#include "twi.h"

bool init_camera(TWI_t* port) {
  bool status;
  // Initialization Register / Data Pairs
  uint8_t* payload[6] = {
    (uint8_t[]) {1, 0x30, 0x01},
    (uint8_t[]) {1, 0x30, 0x08},
#if CAMERA_SENSITIVITY == SENSITIVITY_LEE
    // Dr. Johnny Lee
    (uint8_t[]) {9, 0x00, 0x07, 0x00, 0x00, 0x71, 0x01, 0x00, 0x72, 0x00, 0x20},
    (uint8_t[]) {2, 0x1A, 0x1F, 0x03},
#elif CAMERA_SENSITIVITY == SENSITIVITY_1
    // Wii Sensitivity 1
    (uint8_t[]) {9, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x71, 0x01, 0x00, 0x64, 0x00, 0xfe},
    (uint8_t[]) {2, 0x1A, 0xfd, 0x05},
#elif CAMERA_SENSITIVITY == SENSITIVITY_2
    // Wii Sensitivity 2
    (uint8_t[]) {9, 0x00, 0x02, 0x00, 0x00, 0x71, 0x01, 0x00, 0x96, 0x00, 0xb4},
    (uint8_t[]) {2, 0x1A, 0xb3, 0x04},
#elif CAMERA_SENSITIVITY == SENSITVITY_3
    // Wii Sensitivity 3
    (uint8_t[]) {9, 0x00, 0x02, 0x00, 0x00, 0x71, 0x01, 0x00, 0xaa, 0x00, 0x64},
    (uint8_t[]) {2, 0x1A, 0x63, 0x03},
#elif CAMERA_SENSITIVITY == SENSITIVITY_4
    // Wii Sensitivity 4
    (uint8_t[]) {9, 0x00, 0x02, 0x00, 0x00, 0x71, 0x01, 0x00, 0xc8, 0x00, 0x36},
    (uint8_t[]) {2, 0x1A, 0x35, 0x03},
#elif CAMERA_SENSITIVITY == SENSITVITY_5
    // Wii Sensitivity 5
    (uint8_t[]) {9, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x71, 0x01, 0x00, 0x64, 0x00, 0xfe},
    (uint8_t[]) {2, 0x1A, 0xfd, 0x05},
#endif
    (uint8_t[]) {1, 0x33, 0x03},
    (uint8_t[]) {1, 0x30, 0x08}
  };
  for (int i = 0; i < 6; ++i) {
    status = twi_write_reg(port, CAMERA, payload[i][1], &payload[i][2], payload[i][0]);
    if (status == false) {
      return false;
    }
    delay_ms(100);
  }
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

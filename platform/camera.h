#ifndef CAMERA_H_
#define CAMERA_H_

#include <avr/io.h>
#include <stdbool.h>

#define CAMERA 0x58

#define CAMERA_SENSITIVITY 2

// Initializes the infrared camera
bool init_camera(TWI_t* port);

// Reads 4 IR (x, y) coordinate pairs into payload.
bool read_camera(TWI_t* port, uint16_t payload[4][2]);

#endif

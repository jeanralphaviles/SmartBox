#ifndef CAMERA_H_
#define CAMERA_H_

#include <avr/io.h>
#include <stdbool.h>

#define CAMERA 0x58

#define CAMERA_SENSITIVITY 2

#define SENSITIVITY_LEE 0
#define SENSITIVITY_1 1
#define SENSITIVITY_2 2
#define SENSITIVITY_3 3
#define SENSITIVITY_4 4
#define SENSITIVITY_5 5

// Initializes the infrared camera
bool init_camera(TWI_t* port);

// Reads 4 IR (x, y) coordinate pairs into payload.
bool read_camera(TWI_t* port, uint16_t payload[4][2]);

#endif

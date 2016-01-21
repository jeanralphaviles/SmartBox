#include <avr/io.h>

#include "camera.h"
#include "helpers.h"
#include "serial.h"
#include "twi.h"

void init(void);

int main(int argc, char** argv) {
  init();
  bool status;
  uint16_t coordinates[4][2];
  while (1) {
    status = read_camera(&TWIC, coordinates);
    if (status == true) {
      for (int i = 0; i < 4; ++i) {
        printf("%d: %u %u \n", i, coordinates[i][0], coordinates[i][1]);
      }
      printf("\n");
    }
    delay_ms(10);
  }
}

void init(void) {
  bool status;
  clk_32();
  init_usart();
  init_twi(&TWIC);
  do {
    status = init_camera(&TWIC);
  } while (status == false);
}

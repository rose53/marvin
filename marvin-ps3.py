#!/usr/bin/python

import pygame
import sys
from time import sleep
from socket import socket

pygame.init()

CH1_AXIS = 2
CH3_AXIS = 1
CH4_AXIS = 0

ch1 = 0
ch3 = 0
ch4 = 0

sock = socket()
sock.connect(('localhost', 8000))

j = None

stop = True

try:
	while True:
		pygame.joystick.init()
		if pygame.joystick.get_count() > 0 :
			break;
		pygame.joystick.quit()
		sleep(5)
	
	j = pygame.joystick.Joystick(0)
	j.init()
	
	while j.get_button(3) == 0:

		pygame.event.pump()	
		nch1 = j.get_axis(CH1_AXIS) * 100
		nch3 = -1 * j.get_axis(CH3_AXIS) * 100
		nch4 = j.get_axis(CH4_AXIS) * 100
		if nch1 != ch1 or nch3 != ch3 or nch4 != ch4 :
			ch1 = nch1
			ch3 = nch3
			ch4 = nch4
			sock.send('mecanum ' + str(int(round(ch1))) + ' ' + str(int(round(ch3))) + ' ' + str(int(round(ch4))) + '\n')

		if j.get_button(7) != 0:
			sock.send('pan_dec' + '\n')

		if j.get_button(5) != 0:
			sock.send('pan_inc' + '\n')

		if j.get_button(6) != 0:
			sock.send('tilt_dec' + '\n')

		if j.get_button(4) != 0:
			sock.send('tilt_inc' + '\n')
			
		sleep(0.1)
except KeyboardInterrupt:
	sock.send('bye\n')
	sock.close()	
	if not j is None:
		j.quit()
	sys.exit()

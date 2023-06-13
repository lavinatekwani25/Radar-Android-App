import zmq
import random
import time

context = zmq.Context()
socket = context.socket(zmq.PUB)
socket.bind("tcp://192.168.43.46.:5555")

while True:
    amplitude = random.random()
    angle = random.randint(0, 360)
    message = f"{amplitude},{angle}"
    socket.send_string(message)
    time.sleep(0.1)

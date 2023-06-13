import paho.mqtt.client as mqtt
import csv

# Define MQTT broker information
broker_address = "localhost"  # Replace with your broker's address
broker_port = 1883  # Replace with your broker's port
topic = "amplitude_angle_data"

# Read CSV file
csv_file = "C:/Users/lavin/AndroidStudioProjects/SAMPLERADARAPP/app/assets/peak_values_20230217141328.csv"  # Replace with your CSV file path
data = []
with open(csv_file, 'r') as file:
    reader = csv.reader(file)
    for row in reader:
        data.append(row)

# Connect to MQTT broker
client = mqtt.Client()
client.connect(broker_address, broker_port)

# Publish data to MQTT topic
for row in data:
    message = f"{row[0]}, {row[1]}"  # Assuming the CSV file has amplitude in column 0 and angle in column 1
    client.publish(topic, message)
    print(f"Published: {message}")

# Disconnect from MQTT broker
client.disconnect()


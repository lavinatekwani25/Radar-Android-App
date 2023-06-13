import paho.mqtt.client as mqtt
import json
import csv

# Define the MQTT broker and topic
broker_address = "192.168.43.46"
topic = "radar_data"

# Read data from CSV file
# Open CSV file in text mode
with open(r'C:\Users\lavin\AndroidStudioProjects\SAMPLERADARAPP\app\assets\peak_values_20230217141328.csv', 'r') as csvfile:
    # Create CSV reader
    reader = csv.reader(csvfile)
    
    # Skip header row
    next(reader)
    
    # Iterate over rows
    for row in reader:
        amplitude = float(row[0])  # Use the first column as amplitude
        angle = float(row[1])      # Use the second column as angle
        
        # Use the values as needed
        print("Amplitude:", amplitude)
        print("Angle:", angle)

        # Convert the row data to a dictionary
        data = {
            'amplitude': amplitude,
            'angle': angle
        }

        # Convert the dictionary to a JSON string
        json_data = json.dumps(data)

        # Create an MQTT client and connect to the broker
        client = mqtt.Client("RaspberryPi")
        client.connect(broker_address)

        # Publish the JSON data to the topic
        client.publish(topic, json_data)

        # Disconnect from the MQTT broker
        client.disconnect()


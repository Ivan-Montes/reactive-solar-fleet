#!/bin/bash
set -e 
set -x

KAFKA_PORT="${1}"
KAFKA_BROKER="kafka:${KAFKA_PORT}"

check_kafka() {

    kcat -L -b $KAFKA_BROKER -t __consumer_offsets -J > /dev/null 2>&1
    return $?
    
}

checking_kafka_loop() {

    local max_attempts=5
    local attempt=0
    local wait_time=15
    
    echo "Waiting for Kafka is ready..."
    
    until check_kafka; do
    
       attempt=$((attempt + 1))
        if [ ${attempt} -ge ${max_attempts} ]; then
            echo "Kafka is not ready after ${max_attempts} tries. Exit."
            return 1
        fi
        echo "Kafka is not ready. Waiting ${wait_time} secs..."
        sleep ${wait_time}
        
    done
    
    echo "Kafka is ready."
    
}

 create_topic(){
 
 	local topic=$1
 	kafka-topics.sh --create --if-not-exists --bootstrap-server $KAFKA_BROKER --replication-factor 1 --partitions 1 --topic "$topic"
 
 }

create_all_topics() { 
    
    local topics=("spacecraft.created" "spacecraft.updated" "spacecraft.deleted" "position.created" "position.updated" "position.deleted" "shipclass.created" "shipclass.updated" "shipclass.deleted" "crewmember.created" "crewmember.updated" "crewmember.deleted")

	for topic in "${topics[@]}"; do
	    create_topic "$topic"
	done
	
}

send_event() {

    local topic=$1
    local json=$2
    echo "$json" | kcat -P -b $KAFKA_BROKER -t "$topic" -H "__TypeId__=dev.ime.domain.event.Event"
    
}

send_all_events() {

    local timestamp=$(date +%s.0)

    echo "Sending events..."
    send_event "position.created" '{"eventId": "1cc78fe8-0000-0000-0000-000000000001","eventCategory": "Position","eventType": "position.created","eventTimestamp": '"$timestamp"',"eventData": {"positionId": "15ad29cc-0000-0000-0000-000000000001","positionName": "Apothecary","positionDescription": "Person who in the past made and sold medicines"}}'
    send_event "position.created" '{"eventId": "1cc78fe8-0000-0000-0000-000000000002","eventCategory": "Position","eventType": "position.created","eventTimestamp": '"$timestamp"',"eventData": {"positionId": "15ad29cc-0000-0000-0000-000000000002","positionName": "Resident","positionDescription": "User or habitant"}}'
    send_event "position.created" '{"eventId": "1cc78fe8-0000-0000-0000-000000000003","eventCategory": "Position","eventType": "position.created","eventTimestamp": '"$timestamp"',"eventData": {"positionId": "15ad29cc-0000-0000-0000-000000000003","positionName": "Official","positionDescription": "Employee, worker of the company, person with authority..."}}'
    send_event "position.created" '{"eventId": "1cc78fe8-0000-0000-0000-000000000004","eventCategory": "Position","eventType": "position.created","eventTimestamp": '"$timestamp"',"eventData": {"positionId": "15ad29cc-0000-0000-0000-000000000004","positionName": "Regular","positionDescription": "Any individuals from the Outer Tower chosen to climb the tower in the Inner Tower"}}'
    send_event "position.created" '{"eventId": "1cc78fe8-0000-0000-0000-000000000005","eventCategory": "Position","eventType": "position.created","eventTimestamp": '"$timestamp"',"eventData": {"positionId": "15ad29cc-0000-0000-0000-000000000005","positionName": "Irregular","positionDescription": "On rare occasions, extraordinary people are able to open the doors and enter the Tower. Such people are called Irregulars"}}'
	
	send_event "shipclass.created" '{"eventId": "2cc78fe8-0000-0000-0000-000000000001","eventCategory": "Shipclass","eventType": "shipclass.created","eventTimestamp": '"$timestamp"',"eventData": {"shipclassId": "25ad29cc-0000-0000-0000-000000000001","shipclassName": "Spear","shipclassDescription": "Multipurpose"}}'
    send_event "shipclass.created" '{"eventId": "2cc78fe8-0000-0000-0000-000000000002","eventCategory": "Shipclass","eventType": "shipclass.created","eventTimestamp": '"$timestamp"',"eventData": {"shipclassId": "25ad29cc-0000-0000-0000-000000000002","shipclassName": "Lighting","shipclassDescription": "Fast as light"}}'
    send_event "shipclass.created" '{"eventId": "2cc78fe8-0000-0000-0000-000000000003","eventCategory": "Shipclass","eventType": "shipclass.created","eventTimestamp": '"$timestamp"',"eventData": {"shipclassId": "25ad29cc-0000-0000-0000-000000000003","shipclassName": "Colussus","shipclassDescription": "For gargantuan ships"}}'
    send_event "shipclass.created" '{"eventId": "2cc78fe8-0000-0000-0000-000000000004","eventCategory": "Shipclass","eventType": "shipclass.created","eventTimestamp": '"$timestamp"',"eventData": {"shipclassId": "25ad29cc-0000-0000-0000-000000000004","shipclassName": "Voyager","shipclassDescription": "Exploration tasks"}}'
    
    send_event "spacecraft.created" '{"eventId": "3cc78fe8-0000-0000-0000-000000000001","eventCategory": "Spacecraft","eventType": "spacecraft.created","eventTimestamp": '"$timestamp"',"eventData": {"spacecraftId": "35ad29cc-0000-0000-0000-000000000001","spacecraftName": "Jahad Killer","shipclassId": "25ad29cc-0000-0000-0000-000000000001"}}'
    send_event "spacecraft.created" '{"eventId": "3cc78fe8-0000-0000-0000-000000000002","eventCategory": "Spacecraft","eventType": "spacecraft.created","eventTimestamp": '"$timestamp"',"eventData": {"spacecraftId": "35ad29cc-0000-0000-0000-000000000002","spacecraftName": "Shinsu","shipclassId": "25ad29cc-0000-0000-0000-000000000001"}}'
    send_event "spacecraft.created" '{"eventId": "3cc78fe8-0000-0000-0000-000000000003","eventCategory": "Spacecraft","eventType": "spacecraft.created","eventTimestamp": '"$timestamp"',"eventData": {"spacecraftId": "35ad29cc-0000-0000-0000-000000000003","spacecraftName": "Wild Green Eyes","shipclassId": "25ad29cc-0000-0000-0000-000000000002"}}'
    send_event "spacecraft.created" '{"eventId": "3cc78fe8-0000-0000-0000-000000000004","eventCategory": "Spacecraft","eventType": "spacecraft.created","eventTimestamp": '"$timestamp"',"eventData": {"spacecraftId": "35ad29cc-0000-0000-0000-000000000004","spacecraftName": "Forbbiden City","shipclassId": "25ad29cc-0000-0000-0000-000000000003"}}'
    send_event "spacecraft.created" '{"eventId": "3cc78fe8-0000-0000-0000-000000000005","eventCategory": "Spacecraft","eventType": "spacecraft.created","eventTimestamp": '"$timestamp"',"eventData": {"spacecraftId": "35ad29cc-0000-0000-0000-000000000005","spacecraftName": "Brave Hitorigoto","shipclassId": "25ad29cc-0000-0000-0000-000000000004"}}'


}

checking_kafka_loop
send_all_events

echo "File init-kafka-sh processed"

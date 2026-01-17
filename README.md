# Collaborative Pixel Art 

A multi-user drawing application where people can paint on a shared canvas together in real-time. Everyone sees everyone else's cursor, color choices, and pixel changes as they happen.


## What This Project Does
Think of Google Docs, but for pixel art. Multiple users connect to the same canvas and can:

- Draw pixels with their chosen colors
- See other users' cursors moving around
- Watch pixels appear as others draw them
- Join ongoing sessions and see the current artwork
- Change brush colors and see others do the same

The project started as a single user pixel art app and evolved into a distributed system with two different networking approaches.

## The challenge

When multiple people edit the same canvas simultaneously, problems arise:
- State synchronization: How does everyone see the same picture?
- Event ordering: If someone changes their brush color, then draws, everyone needs to see those events in the right order
- New joiners: How do latecomers get the current canvas state?
- Performance: Sending cursor positions constantly could overload the network

## Two different approches

This project implements the same collaborative canvas using two completely different networking technologies, each with different tradeoffs.

### Approach 1: RabbitMQ (Message Broker)

How it works: Think of a central post office that delivers copies of every message to everyone.
- Every user connects to a RabbitMQ server (the "broker")
- When someone draws a pixel, they send a message to the broker
- The broker makes copies and sends them to all other users
- Each user processes incoming messages and updates their local canvas
- message types:
  - join - "Hi, I'm here!"
  - move - "My cursor is at position X,Y"
  - draw - "I painted pixel X,Y with color C"
  - colorChange - "I switched to color C"
  - leave - "I'm leaving"

Pros: Simple, always responsive, no single point of control
Cons: If everyone disconnects, the artwork is lost; concurrent actions might appear in slightly different orders to different users

### Approach 2: Java RMI (Leader Coordination)
How it works: One user becomes the "leader" who coordinates everything; others register as followers.
- First person to start becomes the leader
- Everyone else connects to the leader and registers a "callback" (like giving the leader your phone number)
- When anyone does something, they tell the leader
- The leader updates their own canvas, then calls everyone's callbacks to notify them
- If the leader leaves, the remaining users elect a new leader

### Which Approach Is Better?
It depends on your priorities:

- RabbitMQ (AP system): Prioritizes availability and responsiveness. Users can keep drawing even if network issues occur, but might temporarily see slightly different states that eventually converge.
- Java RMI (CP system): Prioritizes consistency. Everyone always sees identical states, but the system depends on the leader being available.
For casual collaborative art, RabbitMQ's approach is usually better, users prefer continuous responsiveness over perfect consistency, imo.

## Running the project

### RabbitMQ 

1. Install and start RabbitMQ locally (for a lightweight setup)
```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```
2. Run the application it connects to localhost automatically
```bash
mvn exec:java -Dexec.mainClass="pcd.ass_single.part2.mom.PixelArtMain" 
```
3. Launch multiple instances to collaborate

### Java RMI v
1. Start the rmi registry
```bash
rmiregistry
```
2. Launch the first instance (becomes leader)
```bash
mvn exec:java -Dexec.mainClass="pcd.ass_single.part2.rmi.PixelArtMain" 
```
3. Launch addition instances (become a normal peer)

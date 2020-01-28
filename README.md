# Frybits-Geohash
A pure Kotlin/Multiplatform geohashing library.

What is a [Geohash](https://en.wikipedia.org/wiki/Geohash)? TL;DR - It is an encoded representation of a geographic location as a string.

**Current Supported Platforms:**
- Java 8
- Android API 14+ (It's the Java library with some extra extension functions)

## Project Status
This project is actively being worked on, but the `Geohash` class and extension functions are completed. The following are a list of tasks and their current status:

- Geohash algorithm - `DONE`
- Neighbor searches - `DONE`
- Parent/Children geohashes - `DONE`
- BoundingBox/Circular queries - `INCOMPLETE`
- Distribution - `INCOMPLETE`
- CI/Automated testing - `INCOMPLETE`
- README/Wiki - `IN PROGRESS`
- iOS setup - `INCOMPLETE`
- Example projects:
  - Java - `INCOMPLETE`
  - Javascript - `INCOMPLETE`
  - Android - `INCOMPLETE`
  - iOS - `INCOMPLETE`
  - Native `INCOMPLETE`
  
## Download

Coming soon!

## Usage

Kotlin

```kotlin
// Generating a simple geohash
val geohash = Geohash(31.0123, 115.0123, 10)
println(geohash.geohash) // wt9b3jt7sz

// Get a neighbor
val north = geohash.neighborAt(Direction.NORTH)
println(north.geohash) // wt9b3jt7ub

// Get all neighbors
val neighbors = geohash.surroundingGeohashes(includeSelf = true) // Include self in the list of neighbors
println(neighbors) // Prints all neighbors starting from NORTH and going clockwise

// Getting children geohashes
geohash.children().forEach { g -> 
    println(g.geohash) // Prints all children of "wt9b3jt7sz"
}
```

Java

```java
// Generating a simple geohash
Geohash geohash = Geohash(31.0123, 115.0123, 10);
System.out.println(geohash.getGeohash()); // wt9b3jt7sz

// Get a neighbor
Geohash north = geohash.getNeighbor(Direction.NORTH);
System.out.println(north.getGeohash()); // wt9b3jt7ub

// Get all neighbors
List<Geohash> neighbors = geohash.getSurroundingGeohashes(true); // Include self in the list of neighbors
System.out.println(neighbors); // Prints all neighbors starting from NORTH and going clockwise

// Getting children geohashes
for (Geohash g : geohash.children()) {
    System.out.println(g.getGeohash()); // Prints all children of "wt9b3jt7sz"
}
```

> *Others coming soon*

## License
```MIT License
   
   Copyright (c) 2020 Pablo Baxter
   
   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:
   
   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
```

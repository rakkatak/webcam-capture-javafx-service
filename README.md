# Webcam Video Recording Example

- Accesses default webcam and produces a video recording

## [Webcam Capture](http://webcam-capture.sarxos.pl/)
- API allows you to use your built-in or external webcam directly from Java
- Supports multiple video capturing frameworks
- The WebcamDriver interface which has been already implemented in several capturing drivers build on top of well-known frameworks used to work with multimedia and cameras
- The default driver implementation wraps the [OpenImaj](http://openimaj.org/) library that has cool features like face recognition

## Relevance
### School Messenger
- The web UI currently does not allow you to create a movie and upload without leaving the interface
### Safe Arrival
- Showing a the classroom
- Potentially providing a number of faces recognized in the image

## Personal Research
# JavaFX
JavaFX is a set of graphics and media packages that includes media streaming APIs, which operate consistently across diverse platforms

# Lamda Expressions:
- look a lot like a method declaration; you can consider lambda expressions as anonymous methods (methods without a name)
- do not inherit any names from a supertype or introduce a new level of scoping
- syntax:
    - A comma-separated list of formal parameters enclosed in parentheses
    - The arrow token
    - A body, which consists of a single expression or a statement block
i.e. ```p -> p.getGender() == Person.Sex.MALE
            && p.getAge() >= 18  
            && p.getAge() <= 25```

# javax.sound
Provides interfaces and classes for capture, processing, and playback of sampled audio data

### How To Use

Simply run [FXCamTest](https://github.com/sarxos/webcam-capture/blob/master/webcam-capture-examples/webcam-capture-javafx-service/src/main/java/FXCamTest.java).
i.e.
- mvn clean package
- cd target
- java -cp WebCamTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar FXCamTest

#### Future enhancements

1. Line up audio with video
2. Integration into School Messenger





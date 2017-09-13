# Webcam Capture Live Streaming Example

- Leverages JavaFX and an open source project called [Webcam Capture](http://webcam-capture.sarxos.pl/)

## Webcam Capture
- The WebCam Capture API allows you to use your build-in or external webcam directly from Java. It's designed to support multiple capturing farmeworks.
- The WebCam Capture supports multiple frameworks.
- The WebcamDriver interface which has been already implemented in several capturing drivers build on top of well-known frameworks used to work with multimedia and cameras.
- The default driver implementation wraps the [OpenImaj](http://openimaj.org/) library that has cool features like face recognition

## Relevance
- The web UI for School Messenger currently does not allow you to create a movie and upload without leaving the interface
- Webcams could be used for other applications such as Safe Arrival showing a the classroom and potentially providing a number of faces recognized in the image

This is a tweak to the example created by [james-d](https://github.com/james-d) in response to [this question](http://stackoverflow.com/questions/42816859/run-javafx-and-swing-application-at-the-same-time) on Stack Overflow and put in [this gist](https://gist.github.com/james-d/f826c9f38d53628114124a56fb7c4557).

James found that, in general, the FX application thread is prone to hanging if you interact with the webcam much at all on that thread. E.g. ```webcam.close()``` or ```Webcam.getWebcams()``` seem to cause deadlock (on Mac OS X Sierra on a Macbook Pro with built-in camera) and created a couple of wrapper classes that wrap the Webcam as a JavaFX service and a corresponding view.

## Personal Research
# JavaFX
JavaFX is a set of graphics and media packages that enables developers to develope applications that operate consistently across diverse platforms. There are JavaFX APIs for media streaming, web rendering, and user interface styling.

# LamdaExpressions

### How To Use

1. Simply run [FXCamTest](https://github.com/sarxos/webcam-capture/blob/master/webcam-capture-examples/webcam-capture-javafx-service/src/main/java/FXCamTest.java).
i.e.
mvn clean package
cd target
java -cp WebCamTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar FXCamTest

#### Notes

1. Not a production quiality code.
2. Java 8 is required to build it.





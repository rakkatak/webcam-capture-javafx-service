import java.awt.Dimension;
import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class WebCamService extends Service<Image> {

	private final Webcam cam ;
	
	private final WebcamResolution resolution ;

	private IMediaWriter writer ;
	
	public WebCamService(Webcam cam, WebcamResolution resolution) {
		this.cam = cam ;
		this.resolution = resolution;

		// Webcam properties
		cam.setCustomViewSizes(new Dimension[] {resolution.getSize()});
		cam.setViewSize(resolution.getSize());
	}
	
	public WebCamService(Webcam cam) {
		this(cam, WebcamResolution.QVGA);
	}
	
	@Override
	public Task<Image> createTask() {
		return new Task<Image>() {
			@Override
			protected Image call() throws Exception {

				try {
					// open the webcam
					cam.open();
					// start time is used for the frame timing calculation
					long start = System.currentTimeMillis();
					int frameCounter = 0;
					// Run while the task state is not cancelled
					while (!isCancelled()) {
						System.out.println("Capture frame " + frameCounter);
						if (cam.isImageNew()) {
							BufferedImage bimg = cam.getImage();
							// update buffered image value to be streamed
							updateValue(SwingFXUtils.toFXImage(bimg, null));

							// Write to file
							if (writer!=null) {
								BufferedImage image = ConverterFactory.convertToType(cam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
								IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

								IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
								frame.setKeyFrame(frameCounter == 0);
								frame.setQuality(0);

								writer.encodeVideo(0, frame);
							}
						}
						frameCounter++;
					}

					System.out.println("Cancelled, closing writer");
					writer.close();
					System.out.println("Cancelled, closing cam");
					cam.close();
					System.out.println("Cam closed");

					return getValue();
				} finally {
					cam.close();
				}
			}
			
		};
	}

	public void setWriter(IMediaWriter writer) {
		this.writer = writer;

		// Write video stream
		Dimension size = resolution.getSize();
		this.writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
	}
	

	public int getCamWidth() {
		return resolution.getSize().width ;
	}
	
	public int getCamHeight() {
		return resolution.getSize().height ;
	}
		
}


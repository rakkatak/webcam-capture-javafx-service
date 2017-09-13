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
	
	public WebCamService(Webcam cam, WebcamResolution resolution, IMediaWriter writer) {
		this.cam = cam ;
		this.resolution = resolution;
		this.writer = writer;
		Dimension size = resolution.getSize();

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

		cam.setCustomViewSizes(new Dimension[] {resolution.getSize()});
		cam.setViewSize(resolution.getSize());
	}
	
	public WebCamService(Webcam cam, IMediaWriter writer) {
		this(cam, WebcamResolution.QVGA, writer);
	}
	
	@Override
	public Task<Image> createTask() {
		return new Task<Image>() {
			@Override
			protected Image call() throws Exception {

				try {

					long start = System.currentTimeMillis();

					cam.open();
					int i = 0;
					while (!isCancelled()) {
						System.out.println("Capture frame " + i);
						if (cam.isImageNew()) {
							BufferedImage bimg = cam.getImage();
							updateValue(SwingFXUtils.toFXImage(bimg, null));

							// Write to file
							BufferedImage image = ConverterFactory.convertToType(cam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
							IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

							IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
							frame.setKeyFrame(i == 0);
							frame.setQuality(0);

							writer.encodeVideo(0, frame);
						}
						i++;
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
	

	public int getCamWidth() {
		return resolution.getSize().width ;
	}
	
	public int getCamHeight() {
		return resolution.getSize().height ;
	}
		
}


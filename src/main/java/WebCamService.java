import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import com.xuggle.xuggler.video.IConverter;

public class WebCamService extends Service<Image> {

	private final Webcam cam ;
	
	private final WebcamResolution resolution ;
	
	public WebCamService(Webcam cam, WebcamResolution resolution) {
		this.cam = cam ;
		this.resolution = resolution;
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

					// Write video to file
					File file = new File("output.ts");

					IMediaWriter writer = ToolFactory.makeWriter(file.getName());
					Dimension size = WebcamResolution.QVGA.getSize();

					writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

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

					writer.close();
					System.out.println("Cancelled, closing cam");
					cam.close();
					System.out.println("Cam closed");

					System.out.println("Video recorded in file: " + file.getAbsolutePath());
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


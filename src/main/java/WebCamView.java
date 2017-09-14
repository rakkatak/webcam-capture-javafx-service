import com.xuggle.mediatool.ToolFactory;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import com.xuggle.mediatool.IMediaWriter;

import java.io.File;

public class WebCamView {

	private final ImageView imageView ;
	private final WebCamService service ;
	private final Region view ;
	
	private final Label statusPlaceholder ;

	private IMediaWriter writer ;
	
	public WebCamView(WebCamService service) {
		this.service = service ;

		this.imageView = new ImageView();
		imageView.setPreserveRatio(true);
		// make the cam behave like a mirror:
		imageView.setScaleX(-1);
		
		this.statusPlaceholder = new Label();
		this.view = new Region() {
			{
				service.stateProperty().addListener((obs, oldState, newState) -> {
					switch (newState) {
					case READY:
						statusPlaceholder.setText("Initializing");
						getChildren().setAll(statusPlaceholder);
						break ;
					case SCHEDULED:
						// Write video to file
						File file = new File("output_"+System.currentTimeMillis()+".ts");
						writer = ToolFactory.makeWriter(file.getName());
						service.setWriter(writer);
						statusPlaceholder.setText("Waiting");
						getChildren().setAll(statusPlaceholder);
						break ;
					case RUNNING:
						imageView.imageProperty().unbind();
						// Here the image view binds to the value property set in the service
						imageView.imageProperty().bind(service.valueProperty());
						getChildren().setAll(imageView);
						break ;
					case CANCELLED:
						imageView.imageProperty().unbind();
						imageView.setImage(null);
						statusPlaceholder.setText("Stopped");
						getChildren().setAll(statusPlaceholder);
						System.out.println("Processed cancel in view");
						System.out.println("Cancelled, closing writer");
						writer.close();
						break ;
					case FAILED:
						imageView.imageProperty().unbind();
						statusPlaceholder.setText("Error");
						getChildren().setAll(statusPlaceholder);
						service.getException().printStackTrace();
						break ;
					case SUCCEEDED:
						// unreachable...
						imageView.imageProperty().unbind();
						statusPlaceholder.setText("");
						getChildren().clear();
					}
					requestLayout();
				});
			}
			
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				double w = getWidth();
				double h = getHeight();
				if (service.isRunning()) {
					imageView.setFitWidth(w);
					imageView.setFitHeight(h);
					imageView.resizeRelocate(0, 0, w, h);
				} else {
					double labelHeight = statusPlaceholder.prefHeight(w);
					double labelWidth = statusPlaceholder.prefWidth(labelHeight);
					statusPlaceholder.resizeRelocate((w - labelWidth)/2, (h-labelHeight)/2, labelWidth, labelHeight);
				}
			}
			
			@Override
			protected double computePrefWidth(double height) {
				return service.getCamWidth();
			}
			@Override
			protected double computePrefHeight(double width) {
				return service.getCamHeight();
			}
		};
	}
	
	public WebCamService getService() {
		return service ;
	}
	
	public Node getView() {
		return view ;
	}

}


package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.ei.client.TkStrikeSoftwareUpdatesClient;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.su.om.TkStrikeHasNewVersionResponseDto;
import com.xtremis.daedo.tkstrike.tools.su.om.TkStrikeOSReleaseInfoDto;
import com.xtremis.daedo.tkstrike.tools.utils.Base64ImageUtils;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


@Component
public class ConfigurationSoftwareUpdateController extends TkStrikeBaseController {

	@FXML
	private Node rootView;

	@FXML
	private Pane pnContainer;

	@FXML
	private ProgressIndicator pi;

	@FXML
	private Label lblCurrentVersion;

	@FXML
	private Label lblHasNewVersions;

	@FXML
	private Label lblThisIsLastVersion;

	@FXML
	private Pane pnNewVersion;

	@Value("${tkStrike.current.version}")
	private String currentVersion;

	@Value("${tkStrike.current.build}")
	private String currentBuild;

	@Value("${tkStrike.softwareUpdates.url}")
	private String softwareUpdatesUrl;

	@Autowired
	private TkStrikeSoftwareUpdatesClient tkStrikeSoftwareUpdatesClient;

	public void doCheckForUpdates() {
		TkStrikeExecutors.executeInThreadPool(new Runnable() {

			@Override
			public void run() {
				try {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationSoftwareUpdateController.this.pnNewVersion.getChildren().clear();
							ConfigurationSoftwareUpdateController.this.pnContainer.setVisible(false);
							ConfigurationSoftwareUpdateController.this.pi.setVisible(true);
						}
					});
					final TkStrikeHasNewVersionResponseDto tkStrikeHasNewVersionResponseDto = ConfigurationSoftwareUpdateController.this.tkStrikeSoftwareUpdatesClient
							.hasNewVersion();
					if(tkStrikeHasNewVersionResponseDto != null)
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								if(tkStrikeHasNewVersionResponseDto.getHasNewVersion().booleanValue() && tkStrikeHasNewVersionResponseDto
										.getTkStrikeVersionBuildDto() != null && tkStrikeHasNewVersionResponseDto
												.getTkStrikeVersionBuildDto().getTkStrikeOSReleaseInfoDtos() != null) {
									ConfigurationSoftwareUpdateController.this.lblThisIsLastVersion.setVisible(false);
									ConfigurationSoftwareUpdateController.this.lblHasNewVersions.setVisible(true);
									ConfigurationSoftwareUpdateController.this.pnNewVersion.setVisible(true);
									String newVersion = tkStrikeHasNewVersionResponseDto.getTkStrikeVersionBuildDto().getVersion() + " ("
											+ tkStrikeHasNewVersionResponseDto.getTkStrikeVersionBuildDto().getBuildNumber() + ")";
									for(TkStrikeOSReleaseInfoDto tkStrikeOSReleaseInfoDto : tkStrikeHasNewVersionResponseDto
											.getTkStrikeVersionBuildDto().getTkStrikeOSReleaseInfoDtos()) {
										VBox vBox = new VBox(10.0D);
										vBox.setPadding(new Insets(5.0D));
										vBox.setStyle("-fx-border-width: 1; -fx-border-color: grey;");
										vBox.setAlignment(Pos.CENTER);
										Label lblVersion = new Label(newVersion);
										lblVersion.getStyleClass().addAll(new String[] {"label-subTitle"});
										vBox.getChildren().add(lblVersion);
										ImageView imageView = new ImageView();
										imageView.setImage(Base64ImageUtils.decodeToFXImage(tkStrikeOSReleaseInfoDto.getOsIconImage()));
										imageView.setFitHeight(40.0D);
										imageView.setFitWidth(40.0D);
										vBox.setCursor(Cursor.HAND);
										vBox.onMouseClickedProperty().set(new EventHandler<MouseEvent>() {

											@Override
											public void handle(MouseEvent mouseEvent) {
												if(mouseEvent.getClickCount() == 1)
													try {
														ConfigurationSoftwareUpdateController.this.getHostServices().showDocument(
																tkStrikeOSReleaseInfoDto.getDownloadURL());
													} catch(Exception e) {
														e.printStackTrace();
													}
											}
										});
										vBox.getChildren().add(imageView);
										ConfigurationSoftwareUpdateController.this.pnNewVersion.getChildren().add(vBox);
									}
								} else {
									ConfigurationSoftwareUpdateController.this.lblThisIsLastVersion.setVisible(true);
									ConfigurationSoftwareUpdateController.this.lblHasNewVersions.setVisible(false);
									ConfigurationSoftwareUpdateController.this.pnNewVersion.setVisible(false);
								}
								ConfigurationSoftwareUpdateController.this.pnContainer.setVisible(true);
								ConfigurationSoftwareUpdateController.this.pi.setVisible(false);
							}
						});
				} catch(TkStrikeServiceException e) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ConfigurationSoftwareUpdateController.this.showErrorDialog(ConfigurationSoftwareUpdateController.this.getMessage(
									"title.default.error"), ConfigurationSoftwareUpdateController.this.getMessage("message.error.softwareUpdates"));
							ConfigurationSoftwareUpdateController.this.pnContainer.setVisible(true);
							ConfigurationSoftwareUpdateController.this.pi.setVisible(false);
						}
					});
				}
			}
		});
	}

	@Override
	public void onWindowShowEvent() {
		super.onWindowShowEvent();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ConfigurationSoftwareUpdateController.this.pnContainer.setVisible(true);
				ConfigurationSoftwareUpdateController.this.pi.setVisible(false);
			}
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.lblCurrentVersion.setText("TkStike " + this.currentVersion + " (" + this.currentBuild + ")");
	}

	@Override
	public Node getRootView() {
		return this.rootView;
	}
}

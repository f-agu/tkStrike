package com.xtremis.daedo.tkstrike.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.xtremis.daedo.tkstrike.ui.controller.CommonScoreboardController;
import com.xtremis.daedo.tkstrike.ui.scene.control.TkStrikeCombatHit;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;


public final class TkStrikeScoreboardGraphicDetailTypeUtil {

	private static final Logger _log = Logger.getLogger(TkStrikeScoreboardGraphicDetailTypeUtil.class);

	private static final TkStrikeScoreboardGraphicDetailTypeUtil INSTANCE = new TkStrikeScoreboardGraphicDetailTypeUtil();

	private static final Image IMAGE_HEAD_HIT = new Image(CommonScoreboardController.class.getResourceAsStream("/images/headGearLittle.png"));

	private static final Image IMAGE_BODY_HIT = new Image(CommonScoreboardController.class.getResourceAsStream("/images/trunkLittle.png"));

	private static final Image IMAGE_PUNCH_HIT = new Image(TkStrikeCombatHit.class.getResourceAsStream("/images/punchLittle.png"));

	private static final Color HEAD_COLOR = Color.web("#b01365");

	private static final Color BODY_COLOR = Color.web("#47b4d6");

	private static final Color PUNCH_COLOR = Color.web("#b58d11");

	public static TkStrikeScoreboardGraphicDetailTypeUtil getInstance() {
		return INSTANCE;
	}

	public void changeScoreboardGraphicDetailType(TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType) throws IOException {
		if(scoreboardGraphicDetailType != null) {
			TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
			File targetFile = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + "tkStrike-ext.properties");
			if( ! targetFile.exists())
				targetFile.createNewFile();
			_log.info("changeScoreboardGraphicDetailType File tkStrike-ext.properties = " + targetFile.getAbsolutePath());
			_log.info("changeScoreboardGraphicDetailType New Value = " + scoreboardGraphicDetailType.toString());
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(targetFile));
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			properties.setProperty("tkStrike.scoreboard.graphicDetailType", scoreboardGraphicDetailType.toString());
			FileOutputStream fis = new FileOutputStream(targetFile);
			properties.store(fis, (String)null);
			fis.flush();
			fis.close();
		}
	}

	public Node getNode4BodyImpact(TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType, Double height) {
		if(TkStrikeScoreboardGraphicDetailType.LOW_GRAPHIC_DETAIL.equals(scoreboardGraphicDetailType)) {
			Rectangle rectangle = new Rectangle();
			rectangle.setWidth(height.doubleValue());
			rectangle.setHeight(height.doubleValue());
			rectangle.setFill(BODY_COLOR);
			return rectangle;
		}
		ImageView imgView = new ImageView();
		imgView.setPreserveRatio(true);
		imgView.setFitHeight(height.doubleValue());
		imgView.setImage(IMAGE_BODY_HIT);
		return imgView;
	}

	public Node getNode4HeadImpact(TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType, Double height) {
		if(TkStrikeScoreboardGraphicDetailType.LOW_GRAPHIC_DETAIL.equals(scoreboardGraphicDetailType)) {
			Circle circle = new Circle();
			circle.setRadius(height.doubleValue() / 2.0D);
			circle.setFill(HEAD_COLOR);
			return circle;
		}
		ImageView imgView = new ImageView();
		imgView.setPreserveRatio(true);
		imgView.setFitHeight(height.doubleValue());
		imgView.setImage(IMAGE_HEAD_HIT);
		return imgView;
	}

	public Node getNode4PunchImpact(TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType, Double height) {
		if(TkStrikeScoreboardGraphicDetailType.LOW_GRAPHIC_DETAIL.equals(scoreboardGraphicDetailType)) {
			double base = height.doubleValue() / 2.0D;
			Polygon polygon = new Polygon();
			polygon.getPoints().addAll(new Double[] {Double.valueOf(base * - 1.0D), Double.valueOf(base),
					Double.valueOf(base), Double.valueOf(base),
					Double.valueOf(0.0D), Double.valueOf(base * - 1.0D)});
			polygon.setFill(PUNCH_COLOR);
			return polygon;
		}
		ImageView imgView = new ImageView();
		imgView.setPreserveRatio(true);
		imgView.setFitHeight(height.doubleValue());
		imgView.setImage(IMAGE_PUNCH_HIT);
		return imgView;
	}
}

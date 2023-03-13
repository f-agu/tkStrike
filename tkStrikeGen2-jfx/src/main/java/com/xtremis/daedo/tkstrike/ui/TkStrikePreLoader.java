package com.xtremis.daedo.tkstrike.ui;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TkStrikePreLoader extends Preloader {
  public void start(Stage stage) throws Exception {
    StackPane stSplash = new StackPane();
    stSplash.setAlignment(Pos.CENTER);
    ImageView splash = new ImageView(new Image(getClass().getResourceAsStream("/images/TkStrikeBlack.png")));
    splash.setFitWidth(200.0D);
    splash.setPreserveRatio(true);
    stSplash.getChildren().addAll((Object[])new Node[] { (Node)splash });
    stSplash.setAlignment(Pos.CENTER);
    stSplash.setStyle("-fx-padding: 30; -fx-background-color: #000000; -fx-border-width:1; -fx-border-color: black;");
    stSplash.setEffect((Effect)new DropShadow());
    Scene splashScene = new Scene((Parent)stSplash);
    stage.initStyle(StageStyle.UNDECORATED);
    stage.setScene(splashScene);
    stage.show();
  }
}

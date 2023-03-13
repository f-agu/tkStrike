package com.xtremis.daedo.tkstrike.utils;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public final class TkStrikeUIUtils {
  private static final Color colorKyongGoStart = Color.web("#D2D000");
  
  private static final Color colorKyongGoMedium = Color.web("#FFFF00");
  
  public static final LinearGradient lgKyongGo = new LinearGradient(0.0D, 0.0D, 1.0D, 0.0D, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0.0D, colorKyongGoStart), new Stop(1.0D, colorKyongGoMedium) });
  
  private static final Color colorGameJeonStart = Color.web("#A00000");
  
  private static final Color colorGameJeonMedium = Color.web("#FF0000");
  
  private static final Color colorDisabled = Color.web("#1A1A1A");
  
  public static final Color colorKyongGo = Color.web("#eee33f");
  
  public static final Color colorGameJeon = Color.RED;
  
  public static final Color colorHave6GameJeons = Color.BLUE;
  
  public static void updatePenalties(Integer penaltiesNumber, Integer maxGamJeomsAllowed, Circle c1, Circle c2, Circle c3, Circle c4, Circle c5) {
    c1.setFill((penaltiesNumber.intValue() >= 1) ? (Paint)colorGameJeon : (Paint)colorDisabled);
    if (penaltiesNumber.intValue() >= 6) {
      int steps = maxGamJeomsAllowed.intValue() / 5 - 1;
      int strokeStepWidth = Double.valueOf(c1.getRadius() / steps).intValue();
      int currStep = (penaltiesNumber.intValue() % 5 == 0) ? ((penaltiesNumber.intValue() - 1) / 5) : (penaltiesNumber.intValue() / 5);
      c1.setStroke((Paint)colorHave6GameJeons);
      c1.setStrokeWidth((strokeStepWidth * currStep));
    } else {
      c1.setStrokeWidth(0.0D);
      c1.setStroke(null);
    } 
    c2.setFill(((penaltiesNumber.intValue() >= 2 && penaltiesNumber.intValue() < 6) || (penaltiesNumber.intValue() > 0 && penaltiesNumber.intValue() % 5 == 0) || (penaltiesNumber.intValue() >= 5 && penaltiesNumber.intValue() - 5 * penaltiesNumber.intValue() / 5 >= 2)) ? (Paint)colorGameJeon : (Paint)colorDisabled);
    c3.setFill(((penaltiesNumber.intValue() >= 3 && penaltiesNumber.intValue() < 6) || (penaltiesNumber.intValue() > 0 && penaltiesNumber.intValue() % 5 == 0) || (penaltiesNumber.intValue() >= 5 && penaltiesNumber.intValue() - 5 * penaltiesNumber.intValue() / 5 >= 3)) ? (Paint)colorGameJeon : (Paint)colorDisabled);
    c4.setFill(((penaltiesNumber.intValue() >= 4 && penaltiesNumber.intValue() < 6) || (penaltiesNumber.intValue() > 0 && penaltiesNumber.intValue() % 5 == 0) || (penaltiesNumber.intValue() >= 5 && penaltiesNumber.intValue() - 5 * penaltiesNumber.intValue() / 5 >= 4)) ? (Paint)colorGameJeon : (Paint)colorDisabled);
    c5.setFill(((penaltiesNumber.intValue() >= 5 && penaltiesNumber.intValue() < 6) || (penaltiesNumber.intValue() > 0 && penaltiesNumber.intValue() % 5 == 0) || (penaltiesNumber.intValue() >= 5 && penaltiesNumber.intValue() - 5 * penaltiesNumber.intValue() / 5 >= 5)) ? (Paint)colorGameJeon : (Paint)colorDisabled);
  }
}

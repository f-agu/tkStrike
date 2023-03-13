package com.xtremis.daedo.tkstrike.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public interface TkStrikeKeyCombinationsHelper {
  public static final KeyCombination keyCombENTER = (KeyCombination)new KeyCodeCombination(KeyCode.ENTER, new KeyCombination.Modifier[0]);
  
  public static final KeyCombination keyCombESCAPE = (KeyCombination)new KeyCodeCombination(KeyCode.ESCAPE, new KeyCombination.Modifier[0]);
  
  public static final KeyCombination keyCombConfig = (KeyCombination)new KeyCodeCombination(KeyCode.C, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombRingManager = (KeyCombination)new KeyCodeCombination(KeyCode.M, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombRingManagerPrevMatch = (KeyCombination)new KeyCodeCombination(KeyCode.P, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombRingManagerNextMatch = (KeyCombination)new KeyCodeCombination(KeyCode.N, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombFinalResult = (KeyCombination)new KeyCodeCombination(KeyCode.F, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombHardwareTest = (KeyCombination)new KeyCodeCombination(KeyCode.T, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombKyeShi = (KeyCombination)new KeyCodeCombination(KeyCode.K, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombDoctor = (KeyCombination)new KeyCodeCombination(KeyCode.D, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombExternalScreen = (KeyCombination)new KeyCodeCombination(KeyCode.E, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombShowBackupSystem = (KeyCombination)new KeyCodeCombination(KeyCode.J, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSIMULATOR = (KeyCombination)new KeyCodeCombination(KeyCode.S, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombALLOW_NETWORK_ERRORS = (KeyCombination)new KeyCodeCombination(KeyCode.N, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombGRAPHIC_DETAIL = (KeyCombination)new KeyCodeCombination(KeyCode.G, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombVideoRequestBlue = (KeyCombination)new KeyCodeCombination(KeyCode.B, new KeyCombination.Modifier[] { KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombVideoRequestRed = (KeyCombination)new KeyCodeCombination(KeyCode.R, new KeyCombination.Modifier[] { KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombAddGamJeom2Blue = (KeyCombination)new KeyCodeCombination(KeyCode.B, new KeyCombination.Modifier[0]);
  
  public static final KeyCombination keyCombAddGamJeom2Red = (KeyCombination)new KeyCodeCombination(KeyCode.R, new KeyCombination.Modifier[0]);
  
  public static final KeyCombination keyCombRemoveGamJeom2Blue = (KeyCombination)new KeyCodeCombination(KeyCode.B, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN });
  
  public static final KeyCombination keyCombRemoveGamJeom2Red = (KeyCombination)new KeyCodeCombination(KeyCode.R, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN });
  
  public static final KeyCombination keyCombP1 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT1, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP2 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT2, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP3 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT3, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP4 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT4, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP5 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT5, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP6 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT6, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP7 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT7, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP8 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT8, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP9 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT9, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombP0 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT0, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM1 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT1, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM2 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT2, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM3 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT3, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM4 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT4, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM5 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT5, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM6 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT6, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM7 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT7, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM8 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT8, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM9 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT9, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombM0 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT0, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP1 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD1, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP2 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD2, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP3 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD3, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP4 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD4, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP5 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD5, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP6 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD6, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP7 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD7, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP8 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD8, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP9 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD9, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumP0 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD0, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM1 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD1, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM2 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD2, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM3 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD3, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM4 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD4, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM5 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD5, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM6 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD6, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM7 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD7, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM8 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD8, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM9 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD9, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombNumM0 = (KeyCombination)new KeyCodeCombination(KeyCode.NUMPAD0, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombViewDifferentialScore = (KeyCombination)new KeyCodeCombination(KeyCode.D, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombUndoPointGap = (KeyCombination)new KeyCodeCombination(KeyCode.P, new KeyCombination.Modifier[] { KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN });
  
  public static final KeyCombination keyCombColorGroupSelectionVisible = (KeyCombination)new KeyCodeCombination(KeyCode.G, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectBlueG1 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT1, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectBlueG2 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT2, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectBlueG3 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT3, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectBlueG4 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT4, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectBlueG5 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT5, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectBlueG6 = (KeyCombination)new KeyCodeCombination(KeyCode.DIGIT6, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectRedG1 = (KeyCombination)new KeyCodeCombination(KeyCode.Q, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectRedG2 = (KeyCombination)new KeyCodeCombination(KeyCode.W, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectRedG3 = (KeyCombination)new KeyCodeCombination(KeyCode.E, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectRedG4 = (KeyCombination)new KeyCodeCombination(KeyCode.R, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectRedG5 = (KeyCombination)new KeyCodeCombination(KeyCode.T, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
  
  public static final KeyCombination keyCombSelectRedG6 = (KeyCombination)new KeyCodeCombination(KeyCode.Y, new KeyCombination.Modifier[] { KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN });
}

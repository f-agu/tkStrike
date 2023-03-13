package com.xtremis.daedo.tkstrike.ui.scene.listener;

import java.text.DecimalFormat;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

public class CustomTimeLostFocusListener implements ChangeListener<Boolean> {
  private final TextField textField;
  
  private final String decimalPattern;
  
  public CustomTimeLostFocusListener(TextField theTextField, String decimalPattern) {
    this.textField = theTextField;
    this.decimalPattern = decimalPattern;
  }
  
  public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
    if (!t1.booleanValue()) {
      DecimalFormat df = new DecimalFormat(this.decimalPattern);
      if (StringUtils.isNotBlank(this.textField.getText()) && StringUtils.isNumeric(this.textField.getText()))
        this.textField.setText(df.format(Integer.parseInt(this.textField.getText()))); 
    } 
  }
}

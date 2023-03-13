package com.xtremis.daedo.tkstrike.utils;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class NodeConversionUtils {
  public static String getHexFromNodeIdString(String nodeId) {
    String res = "";
    String firstChar = nodeId.substring(0, 1);
    if (StringUtils.isAlpha(firstChar)) {
      res = new String(Hex.encodeHex(firstChar.getBytes(StandardCharsets.US_ASCII)));
      int restNumber = Integer.parseInt(nodeId.substring(1, nodeId.length()));
      if (restNumber < 10)
        res = res + Integer.toHexString(0); 
      res = res + Integer.toHexString(restNumber);
    } else {
      try {
        int intNumber = Integer.parseInt(nodeId);
        res = Integer.toHexString(intNumber);
        res = StringUtils.leftPad(res, 4, "0");
      } catch (NumberFormatException numberFormatException) {}
    } 
    return res;
  }
  
  public static String getNodeIdStringFromHex(String hexString) {
    String res = "";
    if (StringUtils.isNotBlank(hexString) && hexString
      .length() == 4) {
      String block1 = hexString.substring(0, 2);
      String block2 = hexString.substring(2, 4);
      String block1AlphaString = getAlphaStringFromHexStringAlphanumeric(block1);
      if (block1AlphaString != null) {
        res = block1AlphaString + df.format(Integer.parseInt(block2, 16));
      } else {
        res = "" + Integer.parseInt(block1 + block2, 16);
      } 
    } 
    return res;
  }
  
  public static String getAlphaStringFromHexStringAlphanumeric(String hexString) {
    try {
      String str = new String(Hex.decodeHex(hexString.toCharArray()));
      return StringUtils.isAlpha(str) ? str : null;
    } catch (DecoderException decoderException) {
      return null;
    } 
  }
  
  private static final DecimalFormat df = new DecimalFormat("000");
}

package com.xtremis.daedo.tkstrike.utils;

import com.xtremis.daedo.tkstrike.om.xls.DataTableRoundInfoItem;
import com.xtremis.daedo.tkstrike.om.xls.MatchHistoryItem;
import com.xtremis.daedo.tkstrike.om.xls.MatchInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

public class XlsExporterUtil {
  private static final SimpleDateFormat sdfMatchDate = new SimpleDateFormat("MM/dd/yyyy");
  
  private static final SimpleDateFormat sdfMatchTime = new SimpleDateFormat("HH:mm");
  
  private static final SimpleDateFormat sdfRoundAction = new SimpleDateFormat("mm:ss");
  
  private static final String STYLE_TITLE = "title";
  
  private static final String STYLE_MATCH_INFO_HEADER = "matchInfoHeader";
  
  private static final String STYLE_MATCH_INFO_VALUE = "matchInfoValue";
  
  private static final String STYLE_LEGEND_HEADER = "legendHeader";
  
  private static final String STYLE_LEGEND_VALUE = "legendValue";
  
  private static final String STYLE_FILL_BLUE_BOLD = "fillBlueBold";
  
  private static final String STYLE_FILL_BLUE = "fillBlue";
  
  private static final String STYLE_FILL_RED_BOLD = "fillRedBold";
  
  private static final String STYLE_FILL_RED = "fillRed";
  
  private static final String STYLE_FILL_GREEN = "fillGreen";
  
  private static final String STYLE_FILL_GREY = "fillGrey";
  
  private static final String STYLE_FILL_YELLOW = "fillYellow";
  
  private static final String STYLE_FILL_YELLOW_TEXT_BLUE = "fillYellowTextBlue";
  
  private static final String STYLE_FILL_YELLOW_TEXT_RED = "fillYellowTextRed";
  
  private static final String STYLE_TEXT_BLUE = "textBlue";
  
  private static final String STYLE_TEXT_RED = "textRed";
  
  private static final String STYLE_CELL = "cell";
  
  private static final String STYLE_CELL_BOLD = "boldCell";
  
  enum CustomCellStyle {
    STYLE_TITLE, STYLE_MATCH_INFO_HEADER, STYLE_MATCH_INFO_VALUE, STYLE_LEGEND_HEADER, STYLE_LEGEND_VALUE, STYLE_FILL_BLUE_BOLD, STYLE_FILL_BLUE, STYLE_FILL_RED_BOLD, STYLE_FILL_RED, STYLE_FILL_GREEN, STYLE_FILL_GREY, STYLE_FILL_YELLOW, STYLE_FILL_YELLOW_TEXT_BLUE, STYLE_FILL_YELLOW_TEXT_RED, STYLE_TEXT_BLUE, STYLE_TEXT_RED, STYLE_CELL, STYLE_CELL_BOLD;
  }
  
  static String[] actions = new String[] { 
      "Action", "Start round", "Stop round", "Time out*", "Match resume", "Kye-shi match pause", "Kye-shi match resume", "Blue/Red punch judge", "Blue/Red punch point", "Blue/Red body hit", 
      "Blue/Red body point", "Blue/Red head hit", "Blue/Red head point", "Blue/Red tech judge", "Blue/Red tech point", "Blue/Red gam-jeom", "Blue/Red video replay", "Add blue/red body points/head points/gam-jeom", "Remove blue/red body points/head points/gam-jeom" };
  
  static String[] timeOutSituations = new String[] { "*Time out situation", "Gam-jeom", "Kye-shi", "Call for meeting", "Technical issue", "IVR", "Call for doctor" };
  
  public static void generateXLS(File xlsTargetFile, MatchInfo matchInfo, Map<Integer, DataTableRoundInfoItem> dataTablesItems, List<MatchHistoryItem> matchHistoryItems) {
    HSSFWorkbook wb = new HSSFWorkbook();
    Map<String, CellStyle> cellStyles = createStyles(wb);
    HSSFSheet hSSFSheet = wb.createSheet("Match Info");
    hSSFSheet.setColumnWidth(0, 2560);
    hSSFSheet.setColumnWidth(1, 12800);
    hSSFSheet.setColumnWidth(2, 10240);
    Row row = hSSFSheet.createRow(0);
    Cell cell = row.createCell(0);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("1");
    cell = row.createCell(1);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Match Info Template");
    row = hSSFSheet.createRow(2);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Match Information");
    cell.setCellStyle(cellStyles.get("matchInfoHeader"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("data");
    cell.setCellStyle(cellStyles.get("matchInfoHeader"));
    int i;
    for (i = 3; i <= 20; i++) {
      row = hSSFSheet.createRow(i);
      cell = row.createCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(getLegendCellTitle(i));
      cell.setCellStyle(cellStyles.get("matchInfoValue"));
      cell = row.createCell(2);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(getLegendCellValue(i, matchInfo));
      cell.setCellStyle(cellStyles.get("matchInfoValue"));
    } 
    hSSFSheet = wb.createSheet("Data table");
    hSSFSheet.setColumnWidth(0, 2560);
    for (i = 1; i < 22; i++)
      hSSFSheet.setColumnWidth(i, (i == 1) ? 7680 : 5120); 
    row = hSSFSheet.createRow(0);
    cell = row.createCell(0);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("2");
    cell = row.createCell(1);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Match summary template");
    hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$B$1:$D$1"));
    int lastRow = 2;
    ArrayList<DataTableRoundInfoItem> theList = new ArrayList<>(dataTablesItems.values());
    theList.sort(new Comparator<DataTableRoundInfoItem>() {
          public int compare(DataTableRoundInfoItem o1, DataTableRoundInfoItem o2) {
            return o1.getRound().compareTo(o2.getRound());
          }
        });
    for (DataTableRoundInfoItem dataTableRoundInfo : theList) {
      row = hSSFSheet.createRow(lastRow);
      cell = row.createCell(1);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue(dataTableRoundInfo.getRoundStr());
      cell = row.createCell(2);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Punch");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$C$" + (lastRow + 1) + ":$D$" + (lastRow + 1)));
      cell = row.createCell(4);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Body kick");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + (lastRow + 1) + ":$F$" + (lastRow + 1)));
      cell = row.createCell(6);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Head kick");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$G$" + (lastRow + 1) + ":$H$" + (lastRow + 1)));
      cell = row.createCell(8);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Turning body kick");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$I$" + (lastRow + 1) + ":$J$" + (lastRow + 1)));
      cell = row.createCell(10);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Turning head kick");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$K$" + (lastRow + 1) + ":$L$" + (lastRow + 1)));
      cell = row.createCell(12);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Gam-jeom");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$M$" + (lastRow + 1) + ":$N$" + (lastRow + 1)));
      cell = row.createCell(14);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Video replay");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$O$" + (lastRow + 1) + ":$P$" + (lastRow + 1)));
      cell = row.createCell(16);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Point plus by desk");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$Q$" + (lastRow + 1) + ":$R$" + (lastRow + 1)));
      cell = row.createCell(18);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("Points minuns by desk");
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$S$" + (lastRow + 1) + ":$T$" + (lastRow + 1)));
      cell = row.createCell(20);
      cell.setCellStyle(cellStyles.get("boldCell"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue("TOTAL");
      lastRow++;
      row = hSSFSheet.createRow(lastRow);
      int startColumn = 2;
      for (int c = 0; c < 9; c++) {
        cell = row.createCell(c + startColumn);
        cell.setCellStyle(cellStyles.get("cell"));
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Points");
        cell = row.createCell(c + startColumn + 1);
        cell.setCellStyle(cellStyles.get("cell"));
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Action");
        startColumn++;
      } 
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + lastRow + ":$B$" + (lastRow + 1)));
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$U$" + lastRow + ":$U$" + (lastRow + 1)));
      lastRow++;
      row = hSSFSheet.createRow(lastRow);
      cell = row.createCell(1);
      cell.setCellStyle(cellStyles.get("fillBlueBold"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue(dataTableRoundInfo.getBlueName() + "-" + dataTableRoundInfo.getBlueNoc());
      addDataTableCell(row, 2, (dataTableRoundInfo.getBluePunchPoint().intValue() > 0) ? dataTableRoundInfo.getBluePunchPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 3, (dataTableRoundInfo.getBluePunchAction().intValue() > 0) ? dataTableRoundInfo.getBluePunchAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 4, (dataTableRoundInfo.getBlueBodyKickPoint().intValue() > 0) ? dataTableRoundInfo.getBlueBodyKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 5, (dataTableRoundInfo.getBlueBodyKickAction().intValue() > 0) ? dataTableRoundInfo.getBlueBodyKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 6, (dataTableRoundInfo.getBlueHeadKickPoint().intValue() > 0) ? dataTableRoundInfo.getBlueHeadKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 7, (dataTableRoundInfo.getBlueHeadKickAction().intValue() > 0) ? dataTableRoundInfo.getBlueHeadKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 8, (dataTableRoundInfo.getBlueTurningBodyKickPoint().intValue() > 0) ? dataTableRoundInfo.getBlueTurningBodyKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 9, (dataTableRoundInfo.getBlueTurningBodyKickAction().intValue() > 0) ? dataTableRoundInfo.getBlueTurningBodyKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 10, (dataTableRoundInfo.getBlueTurningHeadKickPoint().intValue() > 0) ? dataTableRoundInfo.getBlueTurningHeadKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 11, (dataTableRoundInfo.getBlueTurningHeadKickAction().intValue() > 0) ? dataTableRoundInfo.getBlueTurningHeadKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 12, (dataTableRoundInfo.getBlueGamJeomPoint().intValue() > 0) ? dataTableRoundInfo.getBlueGamJeomPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 13, (dataTableRoundInfo.getBlueGamJeomAction().intValue() > 0) ? dataTableRoundInfo.getBlueGamJeomAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 14, (dataTableRoundInfo.getBlueVideoReplayPoint().intValue() > 0) ? dataTableRoundInfo.getBlueVideoReplayPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 15, (dataTableRoundInfo.getBlueVideoReplayAction().intValue() > 0) ? dataTableRoundInfo.getBlueVideoReplayAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 16, (dataTableRoundInfo.getBluePointPlusDeskPoint().intValue() > 0) ? dataTableRoundInfo.getBluePointPlusDeskPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 17, (dataTableRoundInfo.getBluePointPlusDeskAction().intValue() > 0) ? dataTableRoundInfo.getBluePointPlusDeskAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 18, (dataTableRoundInfo.getBluePointMinusDeskPoint().intValue() > 0) ? dataTableRoundInfo.getBluePointMinusDeskPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 19, (dataTableRoundInfo.getBluePointMinusDeskAction().intValue() > 0) ? dataTableRoundInfo.getBluePointMinusDeskAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 20, dataTableRoundInfo.getBlueTotal(), cellStyles.get("cell"));
      lastRow++;
      row = hSSFSheet.createRow(lastRow);
      cell = row.createCell(1);
      cell.setCellStyle(cellStyles.get("fillRedBold"));
      cell.setCellType(CellType.STRING);
      cell.setCellValue(dataTableRoundInfo.getRedName() + "-" + dataTableRoundInfo.getRedNoc());
      addDataTableCell(row, 2, (dataTableRoundInfo.getRedPunchPoint().intValue() > 0) ? dataTableRoundInfo.getRedPunchPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 3, (dataTableRoundInfo.getRedPunchAction().intValue() > 0) ? dataTableRoundInfo.getRedPunchAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 4, (dataTableRoundInfo.getRedBodyKickPoint().intValue() > 0) ? dataTableRoundInfo.getRedBodyKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 5, (dataTableRoundInfo.getRedBodyKickAction().intValue() > 0) ? dataTableRoundInfo.getRedBodyKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 6, (dataTableRoundInfo.getRedHeadKickPoint().intValue() > 0) ? dataTableRoundInfo.getRedHeadKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 7, (dataTableRoundInfo.getRedHeadKickAction().intValue() > 0) ? dataTableRoundInfo.getRedHeadKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 8, (dataTableRoundInfo.getRedTurningBodyKickPoint().intValue() > 0) ? dataTableRoundInfo.getRedTurningBodyKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 9, (dataTableRoundInfo.getRedTurningBodyKickAction().intValue() > 0) ? dataTableRoundInfo.getRedTurningBodyKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 10, (dataTableRoundInfo.getRedTurningHeadKickPoint().intValue() > 0) ? dataTableRoundInfo.getRedTurningHeadKickPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 11, (dataTableRoundInfo.getRedTurningHeadKickAction().intValue() > 0) ? dataTableRoundInfo.getRedTurningHeadKickAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 12, (dataTableRoundInfo.getRedGamJeomPoint().intValue() > 0) ? dataTableRoundInfo.getRedGamJeomPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 13, (dataTableRoundInfo.getRedGamJeomAction().intValue() > 0) ? dataTableRoundInfo.getRedGamJeomAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 14, (dataTableRoundInfo.getRedVideoReplayPoint().intValue() > 0) ? dataTableRoundInfo.getRedVideoReplayPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 15, (dataTableRoundInfo.getRedVideoReplayAction().intValue() > 0) ? dataTableRoundInfo.getRedVideoReplayAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 16, (dataTableRoundInfo.getRedPointPlusDeskPoint().intValue() > 0) ? dataTableRoundInfo.getRedPointPlusDeskPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 17, (dataTableRoundInfo.getRedPointPlusDeskAction().intValue() > 0) ? dataTableRoundInfo.getRedPointPlusDeskAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 18, (dataTableRoundInfo.getRedPointMinusDeskPoint().intValue() > 0) ? dataTableRoundInfo.getRedPointMinusDeskPoint() : null, cellStyles.get("cell"));
      addDataTableCell(row, 19, (dataTableRoundInfo.getRedPointMinusDeskAction().intValue() > 0) ? dataTableRoundInfo.getRedPointMinusDeskAction() : null, cellStyles.get("cell"));
      addDataTableCell(row, 20, dataTableRoundInfo.getRedTotal(), cellStyles.get("cell"));
      lastRow++;
      lastRow++;
    } 
    hSSFSheet = wb.createSheet("Match history");
    hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$B$1:$D$1"));
    hSSFSheet.setColumnWidth(0, 3072);
    hSSFSheet.setColumnWidth(1, 2048);
    hSSFSheet.setColumnWidth(2, 7680);
    int j;
    for (j = 3; j <= 16; j++)
      hSSFSheet.setColumnWidth(j, 3840); 
    row = hSSFSheet.createRow(0);
    cell = row.createCell(0);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("3");
    cell = row.createCell(1);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Match history template");
    row = hSSFSheet.createRow(2);
    cell = row.createCell(7);
    cell.setCellStyle(cellStyles.get("fillBlueBold"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("CHUNG");
    cell = row.createCell(12);
    cell.setCellStyle(cellStyles.get("fillRedBold"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("HONG");
    hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$H$3:$L$3"));
    hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$M$3:$Q$3"));
    row = hSSFSheet.createRow(3);
    cell = row.createCell(1);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Round");
    cell = row.createCell(2);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Action");
    cell = row.createCell(3);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Timing (M:S)");
    cell = row.createCell(4);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Time (H:M:S)");
    cell = row.createCell(5);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Source");
    cell = row.createCell(6);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Score");
    cell = row.createCell(7);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Point");
    cell = row.createCell(8);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Hit");
    cell = row.createCell(9);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J1");
    cell = row.createCell(10);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J2");
    cell = row.createCell(11);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J3");
    cell = row.createCell(12);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Point");
    cell = row.createCell(13);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Hit");
    cell = row.createCell(14);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J1");
    cell = row.createCell(15);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J2");
    cell = row.createCell(16);
    cell.setCellStyle(cellStyles.get("boldCell"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J3");
    matchHistoryItems.sort(new Comparator<MatchHistoryItem>() {
          public int compare(MatchHistoryItem o1, MatchHistoryItem o2) {
            return o1.getOrder().compareTo(o2.getOrder());
          }
        });
    lastRow = 4;
    for (MatchHistoryItem historyItem : matchHistoryItems) {
      row = hSSFSheet.createRow(lastRow);
      fillHistoryItemIntoRow(historyItem, wb, row, (DataFormat)wb.createDataFormat(), cellStyles);
      lastRow++;
    } 
    hSSFSheet = wb.createSheet("Legend");
    hSSFSheet.setColumnWidth(0, 3072);
    hSSFSheet.setColumnWidth(1, 7680);
    hSSFSheet.setColumnWidth(2, 7680);
    row = hSSFSheet.createRow(0);
    cell = row.createCell(0);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("4");
    cell = row.createCell(1);
    cell.setCellStyle(cellStyles.get("title"));
    cell.setCellType(CellType.STRING);
    cell.setCellValue("LEGEND");
    lastRow = 2;
    hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$B$3:$C$3"));
    lastRow = generateSourceCells(lastRow, (Sheet)hSSFSheet, cellStyles);
    lastRow++;
    for (j = 0; j < actions.length; j++) {
      String action = actions[j];
      row = hSSFSheet.createRow(lastRow);
      cell = row.createCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(action);
      cell.setCellStyle(cellStyles.get((j == 0) ? STYLE_LEGEND_HEADER : STYLE_LEGEND_VALUE));
      lastRow++;
      hSSFSheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + lastRow + ":$C$" + lastRow));
    } 
    lastRow++;
    for (j = 0; j < timeOutSituations.length; j++) {
      row = hSSFSheet.createRow(lastRow);
      cell = row.createCell(1);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(timeOutSituations[j]);
      cell.setCellStyle(cellStyles.get((j == 0) ? STYLE_LEGEND_HEADER : STYLE_LEGEND_VALUE));
      lastRow++;
    } 
    FileOutputStream fos = null;
    try {
      xlsTargetFile.createNewFile();
      fos = new FileOutputStream(xlsTargetFile);
      wb.write(fos);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (fos != null)
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }  
    } 
  }
  
  private static void fillHistoryItemIntoRow(MatchHistoryItem historyItem, HSSFWorkbook wb, Row row, DataFormat df, Map<String, CellStyle> cellStyles) {
    Cell cell = row.createCell(1);
    CustomCellStyle firstCellStyle = getCustomCellStyle(historyItem, 1);
    cell.setCellStyle(createCellStyle(wb, CustomCellStyle.STYLE_FILL_GREEN.equals(firstCellStyle) ? firstCellStyle : ((historyItem.getRoundNumber().intValue() % 2 == 0) ? CustomCellStyle.STYLE_CELL : CustomCellStyle.STYLE_FILL_GREY), null, cellStyles));
    if (historyItem.getRoundStr() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getRoundStr());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(2);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 2), null, cellStyles));
    if (historyItem.getAction() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getAction());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(3);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 3), null, cellStyles));
    if (historyItem.getTiming() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(sdfRoundAction.format(historyItem.getTiming()));
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(4);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 4), "hh:mm:ss", cellStyles));
    if (historyItem.getTime() != null) {
      cell.setCellType(CellType.NUMERIC);
      cell.setCellValue(historyItem.getTime());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(5);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 5), null, cellStyles));
    if (historyItem.getSource() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getSource());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(6);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 6), null, cellStyles));
    if (historyItem.getScore() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getScore());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(7);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 7), null, cellStyles));
    if (historyItem.getBluePoint() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getBluePoint().toString());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(8);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 8), null, cellStyles));
    if (historyItem.getBlueHit() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getBlueHit().toString());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(9);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 9), null, cellStyles));
    if (historyItem.getBlueJudge1() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getBlueJudge1());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(10);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 10), null, cellStyles));
    if (historyItem.getBlueJudge2() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getBlueJudge2());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(11);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 11), null, cellStyles));
    if (historyItem.getBlueJudge3() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getBlueJudge3());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(12);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 12), null, cellStyles));
    if (historyItem.getRedPoint() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getRedPoint().toString());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(13);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 13), null, cellStyles));
    if (historyItem.getRedHit() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getRedHit().toString());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(14);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 14), null, cellStyles));
    if (historyItem.getRedJudge1() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getRedJudge1());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(15);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 15), null, cellStyles));
    if (historyItem.getRedJudge2() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getRedJudge2());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
    cell = row.createCell(16);
    cell.setCellStyle(createCellStyle(wb, getCustomCellStyle(historyItem, 16), null, cellStyles));
    if (historyItem.getRedJudge3() != null) {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(historyItem.getRedJudge3());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
  }
  
  private static CustomCellStyle getCustomCellStyle(MatchHistoryItem item, int cellNumber) {
    if (item.getAction().toLowerCase().startsWith("start round") || item.getAction().toLowerCase().startsWith("end round"))
      return CustomCellStyle.STYLE_FILL_GREEN; 
    if (item.getAction().toLowerCase().contains("blue"))
      return (item.getBluePoint() != null && cellNumber <= 11) ? CustomCellStyle.STYLE_FILL_YELLOW_TEXT_BLUE : CustomCellStyle.STYLE_TEXT_BLUE; 
    if (item.getAction().toLowerCase().contains("red"))
      return (item.getRedPoint() != null && (cellNumber <= 6 || cellNumber >= 12)) ? CustomCellStyle.STYLE_FILL_YELLOW_TEXT_RED : CustomCellStyle.STYLE_TEXT_RED; 
    return CustomCellStyle.STYLE_CELL;
  }
  
  private static String getLegendCellTitle(int row) {
    String res = "";
    switch (row) {
      case 3:
        res = "Match number";
        break;
      case 4:
        res = "Date";
        break;
      case 5:
        res = "Weight";
        break;
      case 6:
        res = "Phase";
        break;
      case 7:
        res = "Division";
        break;
      case 8:
        res = "Trunk protector hit level";
        break;
      case 9:
        res = "Head protector hit level";
        break;
      case 10:
        res = "Start time";
        break;
      case 11:
        res = "End time";
        break;
      case 12:
        res = "Blue name";
        break;
      case 13:
        res = "Red name";
        break;
      case 14:
        res = "Blue country";
        break;
      case 15:
        res = "Red country";
        break;
      case 16:
        res = "Blue NOC";
        break;
      case 17:
        res = "Red NOC";
        break;
      case 18:
        res = "Match duration";
        break;
      case 19:
        res = "Doctor";
        break;
      case 20:
        res = "Instant video replay";
        break;
    } 
    return res;
  }
  
  private static String getLegendCellValue(int row, MatchInfo matchInfo) {
    String res = "";
    switch (row) {
      case 3:
        res = matchInfo.getMatchNumber();
        break;
      case 4:
        res = sdfMatchDate.format(new Date(matchInfo.getMatchStartTimestamp().longValue()));
        break;
      case 5:
        res = matchInfo.getWeightName();
        break;
      case 6:
        res = matchInfo.getPhaseName();
        break;
      case 7:
        res = matchInfo.getDivisionName();
        break;
      case 8:
        res = (matchInfo.getBodyMinLevel() != null) ? ("" + matchInfo.getBodyMinLevel()) : null;
        break;
      case 9:
        res = (matchInfo.getHeadMinLevel() != null) ? ("" + matchInfo.getHeadMinLevel()) : null;
        break;
      case 10:
        res = sdfMatchTime.format(new Date(matchInfo.getMatchStartTimestamp().longValue()));
        break;
      case 11:
        res = sdfMatchTime.format(new Date(matchInfo.getMatchEndTimestamp().longValue()));
        break;
      case 12:
        res = matchInfo.getBlueName();
        break;
      case 13:
        res = matchInfo.getRedName();
        break;
      case 14:
        res = matchInfo.getBlueFlagName();
        break;
      case 15:
        res = matchInfo.getRedFlagName();
        break;
      case 16:
        res = matchInfo.getBlueFlagAbbreviation();
        break;
      case 17:
        res = matchInfo.getRedFlagAbbreviation();
        break;
      case 18:
        res = DurationFormatUtils.formatDuration(matchInfo.getMatchDuration().toMillis(), "mm:ss");
        break;
      case 19:
        res = "" + matchInfo.getCallVideoReplayTimes();
        break;
      case 20:
        res = "" + matchInfo.getCallDoctorTimes();
        break;
    } 
    return res;
  }
  
  private static void addDataTableCell(Row row, int newCol, Integer theValue, CellStyle cellStyle) {
    Cell cell = row.createCell(newCol);
    cell.setCellStyle(cellStyle);
    if (theValue != null) {
      cell.setCellType(CellType.NUMERIC);
      cell.setCellValue(theValue.intValue());
    } else {
      cell.setCellType(CellType.BLANK);
    } 
  }
  
  private static int generateSourceCells(int firstRow, Sheet sheet, Map<String, CellStyle> cellStyles) {
    Row row = sheet.createRow(firstRow);
    Cell cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Source");
    cell.setCellStyle(cellStyles.get("legendHeader"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("S");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("System");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("BP");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Body protector");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("HP");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Head protector");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("J");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Judge");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("RJ");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Review jury");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("RC");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Red coach");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("BC");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Blue coach");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("CR");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Centrer referee");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    row = sheet.createRow(firstRow);
    cell = row.createCell(1);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("BH");
    cell.setCellStyle(cellStyles.get("legendValue"));
    cell = row.createCell(2);
    cell.setCellType(CellType.STRING);
    cell.setCellValue("Body hit");
    cell.setCellStyle(cellStyles.get("legendValue"));
    firstRow++;
    return firstRow;
  }
  
  private static CellStyle createCellStyle(HSSFWorkbook wb, CustomCellStyle baseStyle, String dataFormatPattern, Map<String, CellStyle> createdStyles) {
    HSSFCellStyle hSSFCellStyle;
    HSSFFont hSSFFont18, hSSFFont17, hSSFFont16, hSSFFont15, hSSFFont14, hSSFFont13, hSSFFont12, hSSFFont11, hSSFFont10, hSSFFont9, hSSFFont8, hSSFFont7, hSSFFont6, hSSFFont5, hSSFFont4, hSSFFont3, hSSFFont2, hSSFFont1;
    CellStyle style = null;
    if (StringUtils.isNotBlank(dataFormatPattern)) {
      if (createdStyles.containsKey(baseStyle.toString() + "-Formatted"))
        return createdStyles.get(baseStyle.toString() + "-Formatted"); 
    } else if (createdStyles.containsKey(baseStyle.toString())) {
      return createdStyles.get(baseStyle.toString());
    } 
    HSSFDataFormat hSSFDataFormat = wb.createDataFormat();
    switch (baseStyle) {
      case STYLE_TITLE:
        hSSFFont18 = wb.createFont();
        hSSFFont18.setFontHeightInPoints((short)14);
        hSSFFont18.setBold(true);
        hSSFFont18.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setFont((Font)hSSFFont18);
        break;
      case STYLE_MATCH_INFO_HEADER:
        hSSFFont17 = wb.createFont();
        hSSFFont17.setFontHeightInPoints((short)12);
        hSSFFont17.setBold(true);
        hSSFFont17.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(setColor(wb, IndexedColors.LAVENDER.getIndex(), (byte)99, (byte)-86, (byte)-2).getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont17);
        break;
      case STYLE_MATCH_INFO_VALUE:
        hSSFFont16 = wb.createFont();
        hSSFFont16.setFontHeightInPoints((short)12);
        hSSFFont16.setBold(false);
        hSSFFont16.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont16);
        break;
      case STYLE_LEGEND_HEADER:
        hSSFFont15 = wb.createFont();
        hSSFFont15.setFontHeightInPoints((short)12);
        hSSFFont15.setBold(true);
        hSSFFont15.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(setColor(wb, IndexedColors.CORNFLOWER_BLUE.getIndex(), (byte)-94, (byte)-67, (byte)-112).getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont15);
        break;
      case STYLE_LEGEND_VALUE:
        hSSFFont14 = wb.createFont();
        hSSFFont14.setFontHeightInPoints((short)12);
        hSSFFont14.setBold(false);
        hSSFFont14.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont14);
        break;
      case STYLE_FILL_BLUE_BOLD:
        hSSFFont13 = wb.createFont();
        hSSFFont13.setFontHeightInPoints((short)12);
        hSSFFont13.setBold(true);
        hSSFFont13.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont13);
        hSSFCellStyle.setWrapText(true);
        break;
      case STYLE_FILL_BLUE:
        hSSFFont12 = wb.createFont();
        hSSFFont12.setFontHeightInPoints((short)12);
        hSSFFont12.setBold(false);
        hSSFFont12.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont12);
        hSSFCellStyle.setWrapText(true);
        break;
      case STYLE_FILL_RED_BOLD:
        hSSFFont11 = wb.createFont();
        hSSFFont11.setFontHeightInPoints((short)12);
        hSSFFont11.setBold(true);
        hSSFFont11.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont11);
        hSSFCellStyle.setWrapText(true);
        break;
      case STYLE_FILL_RED:
        hSSFFont10 = wb.createFont();
        hSSFFont10.setFontHeightInPoints((short)12);
        hSSFFont10.setBold(false);
        hSSFFont10.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont10);
        hSSFCellStyle.setWrapText(true);
        break;
      case STYLE_FILL_GREEN:
        hSSFFont9 = wb.createFont();
        hSSFFont9.setFontHeightInPoints((short)12);
        hSSFFont9.setBold(false);
        hSSFFont9.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont9);
        break;
      case STYLE_FILL_GREY:
        hSSFFont8 = wb.createFont();
        hSSFFont8.setFontHeightInPoints((short)12);
        hSSFFont8.setBold(false);
        hSSFFont8.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont8);
        break;
      case STYLE_FILL_YELLOW:
        hSSFFont7 = wb.createFont();
        hSSFFont7.setFontHeightInPoints((short)12);
        hSSFFont7.setBold(false);
        hSSFFont7.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont7);
        break;
      case STYLE_FILL_YELLOW_TEXT_BLUE:
        hSSFFont6 = wb.createFont();
        hSSFFont6.setFontHeightInPoints((short)12);
        hSSFFont6.setBold(false);
        hSSFFont6.setColor(IndexedColors.BLUE.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont6);
        break;
      case STYLE_FILL_YELLOW_TEXT_RED:
        hSSFFont5 = wb.createFont();
        hSSFFont5.setFontHeightInPoints((short)12);
        hSSFFont5.setBold(false);
        hSSFFont5.setColor(IndexedColors.RED.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont5);
        break;
      case STYLE_TEXT_BLUE:
        hSSFFont4 = wb.createFont();
        hSSFFont4.setFontHeightInPoints((short)12);
        hSSFFont4.setBold(false);
        hSSFFont4.setColor(IndexedColors.BLUE.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont4);
        break;
      case STYLE_TEXT_RED:
        hSSFFont3 = wb.createFont();
        hSSFFont3.setFontHeightInPoints((short)12);
        hSSFFont3.setBold(false);
        hSSFFont3.setColor(IndexedColors.RED.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont3);
        break;
      case STYLE_CELL:
        hSSFFont2 = wb.createFont();
        hSSFFont2.setFontHeightInPoints((short)12);
        hSSFFont2.setBold(false);
        hSSFFont2.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont2);
        break;
      case STYLE_CELL_BOLD:
        hSSFFont1 = wb.createFont();
        hSSFFont1.setFontHeightInPoints((short)12);
        hSSFFont1.setBold(true);
        hSSFFont1.setColor(IndexedColors.BLACK.getIndex());
        hSSFCellStyle = wb.createCellStyle();
        hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
        hSSFCellStyle.setBorderTop(BorderStyle.THIN);
        hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
        hSSFCellStyle.setBorderRight(BorderStyle.THIN);
        hSSFCellStyle.setFont((Font)hSSFFont1);
        break;
    } 
    if (StringUtils.isNotBlank(dataFormatPattern)) {
      hSSFCellStyle.setDataFormat(hSSFDataFormat.getFormat(dataFormatPattern));
      createdStyles.put(baseStyle.toString() + "-Formatted", hSSFCellStyle);
    } else {
      createdStyles.put(baseStyle.toString(), hSSFCellStyle);
    } 
    return (CellStyle)hSSFCellStyle;
  }
  
  private static Map<String, CellStyle> createStyles(HSSFWorkbook wb) {
    Map<String, CellStyle> styles = new HashMap<>();
    HSSFFont hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)14);
    hSSFFont.setBold(true);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    HSSFCellStyle hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("title", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(true);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(setColor(wb, IndexedColors.LAVENDER.getIndex(), (byte)99, (byte)-86, (byte)-2).getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("matchInfoHeader", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("matchInfoValue", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(true);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(setColor(wb, IndexedColors.CORNFLOWER_BLUE.getIndex(), (byte)-94, (byte)-67, (byte)-112).getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("legendHeader", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("legendValue", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(true);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    hSSFCellStyle.setWrapText(true);
    styles.put("fillBlueBold", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    hSSFCellStyle.setWrapText(true);
    styles.put("fillBlue", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(true);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    hSSFCellStyle.setWrapText(true);
    styles.put("fillRedBold", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    hSSFCellStyle.setWrapText(true);
    styles.put("fillRed", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLUE.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("textBlue", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.RED.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("textRed", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("fillGreen", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("fillGrey", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("fillYellow", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLUE.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("fillYellowTextBlue", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.RED.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("fillYellowTextRed", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(true);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("boldCell", hSSFCellStyle);
    hSSFFont = wb.createFont();
    hSSFFont.setFontHeightInPoints((short)12);
    hSSFFont.setBold(false);
    hSSFFont.setColor(IndexedColors.BLACK.getIndex());
    hSSFCellStyle = wb.createCellStyle();
    hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
    hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
    hSSFCellStyle.setBorderTop(BorderStyle.THIN);
    hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
    hSSFCellStyle.setBorderRight(BorderStyle.THIN);
    hSSFCellStyle.setFont((Font)hSSFFont);
    styles.put("cell", hSSFCellStyle);
    return styles;
  }
  
  public static HSSFColor setColor(HSSFWorkbook workbook, short newIndex, byte r, byte g, byte b) {
    HSSFPalette palette = workbook.getCustomPalette();
    HSSFColor hssfColor = null;
    try {
      hssfColor = palette.findColor(r, g, b);
      if (hssfColor == null) {
        palette.setColorAtIndex(newIndex, r, g, b);
        hssfColor = palette.getColor(newIndex);
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return hssfColor;
  }
}

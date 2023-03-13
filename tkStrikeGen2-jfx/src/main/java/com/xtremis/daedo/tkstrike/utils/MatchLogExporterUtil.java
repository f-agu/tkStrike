package com.xtremis.daedo.tkstrike.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.xtremis.daedo.tkstrike.om.CommonMatchLogDto;
import com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto;
import com.xtremis.daedo.tkstrike.om.xls.DataTableRoundInfoItem;
import com.xtremis.daedo.tkstrike.om.xls.MatchHistoryItem;
import com.xtremis.daedo.tkstrike.om.xls.MatchInfo;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;

import au.com.bytecode.opencsv.CSVWriter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


@Component
public class MatchLogExporterUtil implements InitializingBean {

	private final SimpleDateFormat sdf4FileFullDate = new SimpleDateFormat("yyyyMMddHHmmss");

	private final SimpleDateFormat sdfFullDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

	private final SimpleDateFormat sdfMatchTim = new SimpleDateFormat("mm:ss:SSS");

	public void exportAggregatedsToCSVFile(String[][] defaultAggregatted, String[][] hitsByCategoryCount, File targetDirectory)
			throws TkStrikeServiceException {
		if(defaultAggregatted != null && hitsByCategoryCount != null && targetDirectory != null) {
			if( ! targetDirectory.exists())
				try {
					FileUtils.forceMkdir(targetDirectory);
				} catch(IOException e) {
					throw new TkStrikeServiceException(e);
				}
			Date current = new Date();
			File defaultAggregatedFile = new File(targetDirectory.getAbsolutePath() + "/" + this.sdf4FileFullDate.format(current)
					+ "-MatchLogAggregated.csv");
			exportToCSV(defaultAggregatted, defaultAggregatedFile);
			File hitsByCategoryCountFile = new File(targetDirectory.getAbsolutePath() + "/" + this.sdf4FileFullDate.format(current)
					+ "-HitsByCategoryCount.csv");
			exportToCSV(hitsByCategoryCount, hitsByCategoryCountFile);
		}
	}

	public void exportToCSVFile(CommonMatchLogDto matchLogEntry, List<? extends CommonMatchLogItemDto> matchLogItemEntries, File targetDirectory)
			throws TkStrikeServiceException {
		if(matchLogEntry != null && matchLogItemEntries != null && targetDirectory != null) {
			if( ! targetDirectory.exists())
				try {
					FileUtils.forceMkdir(targetDirectory);
				} catch(IOException e) {
					throw new TkStrikeServiceException(e);
				}
			File fileMatchLog = new File(targetDirectory.getAbsolutePath() + "/" + this.sdf4FileFullDate.format(new Date(matchLogEntry
					.getMatchStartTime().longValue())) + "-" + matchLogEntry.getMatchNumber() + "-matchLog.csv");
			exportHeader(matchLogEntry, fileMatchLog);
			File fileMatchLogItems = new File(targetDirectory.getAbsolutePath() + "/" + this.sdf4FileFullDate.format(new Date(matchLogEntry
					.getMatchStartTime().longValue())) + "-" + matchLogEntry.getMatchNumber() + "-matchLogItems.csv");
			exportItems(matchLogItemEntries, fileMatchLogItems);
		}
	}

	public void exportToXLSFile(CommonMatchLogDto matchLog, MatchInfo matchInfo, Map<Integer, DataTableRoundInfoItem> dataTablesItems,
			List<MatchHistoryItem> matchHistoryItems, File targetDirectory) throws TkStrikeServiceException {
		if(matchLog != null && dataTablesItems != null && targetDirectory != null) {
			if( ! targetDirectory.exists())
				try {
					FileUtils.forceMkdir(targetDirectory);
				} catch(IOException e) {
					throw new TkStrikeServiceException(e);
				}
			File fileMatchLog = new File(targetDirectory.getAbsolutePath() + "/" + this.sdf4FileFullDate.format(new Date(matchLog.getMatchStartTime()
					.longValue())) + "-" + matchLog.getMatchNumber() + "-matchLog.xls");
			if(fileMatchLog.exists())
				FileUtils.deleteQuietly(fileMatchLog);
			XlsExporterUtil.generateXLS(fileMatchLog, matchInfo, dataTablesItems, matchHistoryItems);
		}
	}

	public void exportToPDFFile(CommonMatchLogDto matchLogEntry, List<CommonMatchLogItemDto> matchLogItemEntries, File targetDirectory)
			throws TkStrikeServiceException {
		if(matchLogEntry != null && matchLogItemEntries != null && targetDirectory != null) {
			if( ! targetDirectory.exists())
				try {
					FileUtils.forceMkdir(targetDirectory);
				} catch(IOException e) {
					throw new TkStrikeServiceException(e);
				}
			File fileMatchLog = new File(targetDirectory.getAbsolutePath() + "/" + this.sdf4FileFullDate.format(new Date(matchLogEntry
					.getMatchStartTime().longValue())) + "-" + matchLogEntry.getMatchNumber() + "-matchLog.pdf");
			if(fileMatchLog.exists())
				FileUtils.deleteQuietly(fileMatchLog);
			try {
				FileUtils.writeByteArrayToFile(fileMatchLog, generateMatchLogPDFReport(matchLogEntry, matchLogItemEntries));
			} catch(IOException e) {
				throw new TkStrikeServiceException(e);
			}
		}
	}

	private byte[] generateMatchLogPDFReport(CommonMatchLogDto matchLogEntry, List<CommonMatchLogItemDto> matchLogItemEntries)
			throws TkStrikeServiceException {
		if(matchLogEntry != null)
			try {
				byte[] pdfBytes = null;
				InputStream matchLogReportMainJRXML = MatchLogExporterUtil.class.getResourceAsStream("/reports/repo_matchLog.jrxml");
				InputStream matchLogEntriesReportJRXML = MatchLogExporterUtil.class.getResourceAsStream("/reports/repo_MatchLogEntries.jrxml");
				JasperDesign jdMatchLogEntries = JRXmlLoader.load(matchLogEntriesReportJRXML);
				JasperReport jrMatchLogEntries = JasperCompileManager.compileReport(jdMatchLogEntries);
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("REPORT_LOCALE", Locale.ENGLISH);
				parameters.put("pathImageLogo", MatchLogExporterUtil.class.getResource("/images/LogoDaedoTot.png").toString());
				parameters.put("pathImageLogoTkStrike", MatchLogExporterUtil.class.getResource("/images/TkStrike.png").toString());
				parameters.put("pathImageIcoVideo", MatchLogExporterUtil.class.getResource("/images/ico-video.png").toString());
				parameters.put("pathImageIcoNoVideo", MatchLogExporterUtil.class.getResource("/images/ico-novideo.png").toString());
				parameters.put("pathImageHead", MatchLogExporterUtil.class.getResource("/images/headGearLittle.png").toString());
				parameters.put("pathImageBody", MatchLogExporterUtil.class.getResource("/images/trunkLittle.png").toString());
				parameters.put("jrMatchLogItems", jrMatchLogEntries);
				parameters.put("dsMatchLogItems", new ByDtoDataSource(matchLogItemEntries.toArray((Object[])new CommonMatchLogItemDto[0])));
				JasperDesign jd = JRXmlLoader.load(matchLogReportMainJRXML);
				JasperReport jr = JasperCompileManager.compileReport(jd);
				ByDtoDataSource<CommonMatchLogDto> reportMainDataSource = new ByDtoDataSource<>(new CommonMatchLogDto[] {matchLogEntry});
				pdfBytes = JasperRunManager.runReportToPdf(jr, parameters, reportMainDataSource);
				return pdfBytes;
			} catch(Exception e) {
				throw new TkStrikeServiceException(e);
			}
		return null;
	}

	private class ByDtoDataSource<D> implements JRDataSource {

		private D[] itemDtos;

		private int index = - 1;

		public ByDtoDataSource(D[] itemDtos) {
			this.itemDtos = itemDtos;
		}

		@Override
		public boolean next() throws JRException {
			this.index++;
			return (this.index < this.itemDtos.length);
		}

		@Override
		public Object getFieldValue(JRField jrField) throws JRException {
			Object res = null;
			String fieldName = jrField.getName();
			D current = this.itemDtos[this.index];
			try {
				res = PropertyUtils.getProperty(current, fieldName);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return res;
		}
	}

	private void exportHeader(CommonMatchLogDto matchLogEntry, File targetFile) throws TkStrikeServiceException {
		FileOutputStream fos = null;
		CSVWriter csvWriter = null;
		try {
			fos = new FileOutputStream(targetFile);
			csvWriter = new CSVWriter(new PrintWriter(fos), ',');
			csvWriter.writeNext(matchLogEntry.getCSVHeaders());
			csvWriter.writeNext(matchLogEntry.getCSVValues());
		} catch(IOException ioe) {
			throw new TkStrikeServiceException(ioe);
		} finally {
			if(csvWriter != null)
				try {
					csvWriter.flush();
					csvWriter.close();
				} catch(IOException iOException) {}
		}
	}

	private void exportItems(List<? extends CommonMatchLogItemDto> matchLogItems, File targetFile) throws TkStrikeServiceException {
		if(matchLogItems != null && targetFile != null) {
			sortMatchLogItems(matchLogItems);
			FileOutputStream fos = null;
			CSVWriter csvWriter = null;
			try {
				fos = new FileOutputStream(targetFile);
				csvWriter = new CSVWriter(new PrintWriter(fos), ',');
				if(matchLogItems.size() > 0) {
					csvWriter.writeNext(((CommonMatchLogItemDto)matchLogItems.get(0)).getCSVHeaders());
					for(CommonMatchLogItemDto matchLogItem : matchLogItems)
						csvWriter.writeNext(matchLogItem.getCSVValues());
				}
			} catch(IOException ioe) {
				throw new TkStrikeServiceException(ioe);
			} finally {
				if(csvWriter != null)
					try {
						csvWriter.flush();
						csvWriter.close();
					} catch(IOException iOException) {}
			}
		}
	}

	private void exportToCSV(String[][] csvContent, File targetFile) throws TkStrikeServiceException {
		FileOutputStream fos = null;
		CSVWriter csvWriter = null;
		try {
			fos = new FileOutputStream(targetFile);
			csvWriter = new CSVWriter(new PrintWriter(fos), ',');
			for(int i = 0; i < csvContent.length; i++) {
				String[] strings = csvContent[i];
				csvWriter.writeNext(strings);
			}
		} catch(IOException ioe) {
			throw new TkStrikeServiceException(ioe);
		} finally {
			if(csvWriter != null)
				try {
					csvWriter.flush();
					csvWriter.close();
				} catch(IOException iOException) {}
		}
	}

	public static void sortMatchLogItems(List<? extends CommonMatchLogItemDto> matchLogItems) {
		Collections.sort(matchLogItems, new Comparator<CommonMatchLogItemDto>() {

			@Override
			public int compare(CommonMatchLogItemDto o1, CommonMatchLogItemDto o2) {
				return Long.valueOf(o1.getEventTime().longValue()).compareTo(o2.getEventTime());
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {}
}

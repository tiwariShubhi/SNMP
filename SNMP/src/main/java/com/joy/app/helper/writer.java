package com.joy.app.helper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

public class writer {

	
	
	public byte[] excelWriter(ArrayList<String> sheetHeader, ArrayList<ArrayList<String>> presenceData) throws IOException {
		// TODO Auto-generated method stub
		
		//ArrayList<String> sheetHeader = new ArrayList<String>();
		//ArrayList<ArrayList<String>> presenceData = new ArrayList<ArrayList<String>>();
		String sheetTabName = "Attendance logs";
		final String sheetTitle = "Attendance Records"; 
		final String present = "present";
		final String absent = "absent";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.now();
		//System.out.println(dtf.format(localDate)); //2016/11/16
		String timeStamp = dtf.format(localDate).toString().replace('/','_');

		String outFile = "/Users/nitinkumar/Desktop/Attendance Report" + timeStamp + ".xls";
		
		
		// adding sheet header
		//sheetHeader.add("Roll number");
		//sheetHeader.add("Mac Address");
		//sheetHeader.add("Date A");
		//sheetHeader.add("Date B");
		
		//dummy presence data
		/*
		for(int k=1;k<5;k++)
		{
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(k+"");
			temp.add("mac "+k);
			temp.add("present");
			temp.add("absent");
			presenceData.add(temp);
		}
		*/
		
		int rowcount=0,columncount;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetTabName);
		ExcelHelper excelhelp = new ExcelHelper();
		HSSFCellStyle cellstyleRed,headstyle,cellstyleGreen,cellstyleWhite,cellstyleWhiteEnv,cellstyleWhiteServer,mainheadstyle;
		
		HSSFPalette newPalette = workbook.getCustomPalette();
//		newPalette.setColorAtIndex(HSSFColor.HSSFColorPredefined.TEAL.getIndex(),(byte)146,(byte)208,(byte)80);
//		newPalette.setColorAtIndex(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex(),(byte)191,(byte)191,(byte)191);
		mainheadstyle = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.TEAL.getIndex(),FillPatternType.SOLID_FOREGROUND.ordinal(), HorizontalAlignment.CENTER.ordinal(), VerticalAlignment.CENTER.ordinal(), 1, 1, 1, 1,true, false, true, 18,"Arial",HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		headstyle = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex(), FillPatternType.SOLID_FOREGROUND.ordinal(), HorizontalAlignment.CENTER.ordinal(), VerticalAlignment.CENTER.ordinal(), 1, 1, 1, 1, true, false, true, 11,"Arial",HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		cellstyleRed = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.RED.getIndex(), FillPatternType.SOLID_FOREGROUND.ordinal(), HorizontalAlignment.CENTER.ordinal(), VerticalAlignment.CENTER.ordinal(), 1, 1, 1, 1, false, false, true, 10,"Arial",HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		cellstyleWhite = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND.ordinal(), HorizontalAlignment.CENTER.ordinal(), VerticalAlignment.CENTER.ordinal(), 1, 1, 1, 1, false, false, true, 10,"Arial",HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		cellstyleGreen = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.TEAL.getIndex(), FillPatternType.SOLID_FOREGROUND.ordinal(), HorizontalAlignment.CENTER.ordinal(), VerticalAlignment.CENTER.ordinal(), 1, 1, 1, 1, false, false, true, 10,"Arial",HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		cellstyleWhiteEnv = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex(), FillPatternType.SOLID_FOREGROUND.ordinal(), HorizontalAlignment.CENTER.ordinal(), VerticalAlignment.CENTER.ordinal(), 1, 1, 1, 1, true, false, true, 10,"Arial",HSSFColor.HSSFColorPredefined.BLACK.getIndex());
//		cellstyleWhiteServer = excelhelp.getExcelCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex(), CellStyle.SOLID_FOREGROUND, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, 1, 1, 1, 1, false, false, true, 10,"Arial",HSSFColor.BLACK.index);
//		
		
		excelhelp.setCellValue(sheet,rowcount, 0,sheetTitle , mainheadstyle, true);
		int sizeOfHeader = presenceData.get(0).size()/26;
		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:E2"));
		rowcount = rowcount+2;
		for(int i=0;i<sheetHeader.size();i++)
		{
			excelhelp.setCellValue(sheet,rowcount, i,sheetHeader.get(i) , headstyle, true);
		}
		
		rowcount++;
		
		
		for(int i=0;i<presenceData.size();i++)
		{
			ArrayList<String> record = presenceData.get(i);
			for(int j=0;j<record.size();j++)
			{
				if(record.get(j).equalsIgnoreCase(present))
				{
					excelhelp.setCellValue(sheet,rowcount, j,record.get(j) , cellstyleGreen, true);
				}
				else if(record.get(j).equalsIgnoreCase(absent))
				{
					excelhelp.setCellValue(sheet,rowcount, j,record.get(j) , cellstyleRed, true);
				}
				else
				{
					excelhelp.setCellValue(sheet,rowcount, j,record.get(j) , cellstyleWhite, true);
				}
			}
			
			rowcount++;
		}
		
		
		
		
		//for resizing columns as per header length
		for(int i=0;i<sheetHeader.size();i++)
		{
			sheet.autoSizeColumn(i);
		}
		
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(outFile);
			workbook.write(out); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			workbook.write(bos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bos.close();
		}
		byte[] bytes = bos.toByteArray();
		return bytes;
		 
	}

}

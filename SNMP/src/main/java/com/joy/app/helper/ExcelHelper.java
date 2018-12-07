package com.joy.app.helper;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {

	public ArrayList<String> readColExcel(FileInputStream fp,String ext,int colNo)
	{
		//colNo starts from 1
		ArrayList<String> col = new ArrayList<String>();
		//reads first sheet of the excel
		Workbook wb = null;
		try
		{
			if(ext.equals(".xls"))
				{
					//POIFSFileSystem fs = new POIFSFileSystem(fp);
					System.out.println(".xls");
					wb = new HSSFWorkbook(fp);
				}
			else if(ext.equals(".xlsx"))
				{
					
					System.out.println(".xlsx");
					wb =(Workbook) new XSSFWorkbook(fp);
				}
			else
				{
					return null;
				}
			
			Sheet sheet = wb.getSheetAt(0);
			
			Iterator<Row> rowIt = sheet.iterator();
			while (rowIt.hasNext()) 
			{
				int i=1;
                Row row = rowIt.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext())
                {
                	if(i==colNo)
                	{
                		Cell cell = cellIterator.next();
                		col.add(cell.getStringCellValue());
                		System.out.println(cell.getStringCellValue());
                	}
                	i++;
                }
			}    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return col;
	}
//	
//	public void writeValueInCell(HSSFCell headerCell, int cellType, String cellValue, boolean isNumber)
//	{
//		headerCell.setCellType(cellType);
//		if(isNumber)
//		{
//			headerCell.setCellValue(Double.parseDouble(cellValue));
//		}
//		else
//		{
//			headerCell.setCellValue(cellValue);
//		}
//	}
//	
	public HSSFCellStyle getExcelCellStyle(HSSFWorkbook workbook, int foreGroundColor, int fillPattern, int horizontalAlignment, 
			int verticalAlignment, int borderTop, int borderLeft, int borderRight, int borderBottom, boolean bold, boolean underLine, 
			boolean wrapText, int fontSize, String fontName,short fontColor)
	{
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(getFont(workbook, bold, underLine, fontSize, fontName,fontColor));   
		setCellColor(cellStyle, foreGroundColor, fillPattern);
		setCellAlignment(cellStyle, horizontalAlignment, verticalAlignment, wrapText);
		setCellBorder(cellStyle, borderTop, borderLeft, borderRight, borderBottom);
		
		return cellStyle;
	}
//	
//	public HSSFCellStyle getExcelCellColor(HSSFWorkbook workbook, int foreGroundColor, int fillPattern)
//	{
//		HSSFCellStyle cellStyle = workbook.createCellStyle();
//		setCellColor(cellStyle, foreGroundColor, fillPattern);
//		return cellStyle;
//	}
	private void setCellBorder(HSSFCellStyle cellStyle, int borderTop, int borderLeft, int borderRight, int borderBottom)
	{
		cellStyle.setBorderTop(BorderStyle.valueOf((short) borderTop));
		cellStyle.setBorderLeft(BorderStyle.valueOf((short) borderLeft));
		cellStyle.setBorderRight(BorderStyle.valueOf((short) borderRight));
		cellStyle.setBorderBottom(BorderStyle.valueOf((short) borderBottom));
	}
	
	private void setCellAlignment(HSSFCellStyle cellStyle, int horizontalAlignment, int verticalAlignment, boolean wrapText)
	{
		cellStyle.setAlignment(HorizontalAlignment.forInt(horizontalAlignment));  
		cellStyle.setVerticalAlignment(VerticalAlignment.forInt(verticalAlignment));   
		cellStyle.setWrapText(wrapText);
	}
//	
	private void setCellColor(HSSFCellStyle cellStyle, int foreGroundColor, int fillPattern)
	{
		cellStyle.setFillForegroundColor((short)foreGroundColor); 
		
		cellStyle.setFillPattern(FillPatternType.forInt(fillPattern));   
	}
//	
	private HSSFFont getFont(HSSFWorkbook workbook, boolean bold, boolean underLine, int fontSize, String fontName,short fontColor)
	{
		HSSFFont font = workbook.createFont();
		
		if(bold)
		{
			font.setBold(bold);
		}
		if(underLine)
		{
			font.setUnderline((byte)1);
		}
        font.setFontHeightInPoints((short)fontSize);
        font.setFontName(fontName);
        font.setColor(fontColor);
		return font;
	}
//	
//	public void setBorderToRegion(HSSFWorkbook workbook, HSSFSheet sheet, String cellRange)
//	{
//		CellRangeAddress region = CellRangeAddress.valueOf(cellRange);
//		RegionUtil.setBorderBottom(new Integer(CellStyle.BORDER_THICK), region, sheet, workbook );
//	    RegionUtil.setBorderTop( new Integer(CellStyle.BORDER_THICK), region, sheet, workbook );
//	    RegionUtil.setBorderLeft( new Integer(CellStyle.BORDER_THICK), region, sheet, workbook );
//	    RegionUtil.setBorderRight( new Integer(CellStyle.BORDER_THICK), region, sheet, workbook );
//	}
//	
//	public String getCellValue(HSSFCell cell)
//	{
//		String value = null;
//		if(null!=cell){
//			value = handleCell(cell.getCellType(), cell);
//		}
//		return value;
//	}
//	
//	private String handleCell(int type, HSSFCell cell) {
//              
//        String value = null;
//        try{
//			switch (type) {
//		        case HSSFCell.CELL_TYPE_STRING:
//		        	value = cell.getRichStringCellValue().getString();
//		            break;
//		        case HSSFCell.CELL_TYPE_NUMERIC:
//		            if (DateUtil.isCellDateFormatted(cell)) {
//		            	Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue()); 
//		            	SimpleDateFormat  format  = new SimpleDateFormat("MM/dd/yyyy"); 
//		            	value  = format.format(date); 
//		            } else {
//		            	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//						value = cell.getStringCellValue();
//		            }
//		            break;
//		        case HSSFCell.CELL_TYPE_BOOLEAN:
//		        	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//					value = cell.getStringCellValue();
//		            break;
//		        case HSSFCell.CELL_TYPE_FORMULA:
//		        	// Re-run based on the formula type
//		            handleCell(cell.getCachedFormulaResultType(), cell);
//		        default:
//		        	value = cell.getRichStringCellValue().getString();
//		    }
//		}catch(Exception e){
//		//	logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//        return value;
//}
//	
//	public void setColorInRange(HSSFRow row, int startColIndex, int lastColIndex, HSSFCellStyle cellStyle)
//	{
//		for(int count=startColIndex; count<=lastColIndex; ++count)
//		{
//			HSSFCell cell = row.getCell(count);
//			// If cell is null, then create new cell
//			if(null!=cell){
//				cell.setCellStyle(cellStyle);
//			}else{
//				HSSFCell cell1 = row.createCell(count);
//				cell1.setCellStyle(cellStyle);
//			}
//		}
//	}
//	
	public void setCellValue(HSSFSheet sheet, int rowIndex, int colIndex, String message, HSSFCellStyle cellStyle, boolean isString)
	{
		HSSFRow row = sheet.getRow(rowIndex);
		// If row is null, then create new row
		if(null==row){
			row = sheet.createRow(rowIndex);
		}
		
		HSSFCell cell = row.getCell(colIndex);
		// If cell is null, then create new cell
		if(null==cell){
			cell = row.createCell(colIndex);
		}
		if(isString){
			cell.setCellValue(message);
		}else{
			//cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(Double.parseDouble(message));
		}
		cell.setCellStyle(cellStyle);	
	}
	
//	public void setCellValueWithHyperlink(HSSFSheet sheet, int rowIndex, int colIndex, String message, HSSFCellStyle cellStyle, String drl)
//	{
//		CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
//		Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
//		link.setAddress(drl);
//		
//		HSSFRow row = sheet.getRow(rowIndex);
//		// If row is null, then create new row
//		if(null==row){
//			row = sheet.createRow(rowIndex);
//		}
//		
//		HSSFCell cell = row.getCell(colIndex);
//		// If cell is null, then create new cell
//		if(null==cell){
//			cell = row.createCell(colIndex);
//		}
//		cell.setHyperlink(link);
//		cell.setCellValue(message);
//		cell.setCellStyle(cellStyle);	
//	}
//	
//	public void setCellValue(HSSFSheet sheet, int rowIndex, int colIndex, String message, int color, int fillPatern, boolean isString)
//	{
//		HSSFCellStyle cellStyle = getExcelCellStyle(sheet.getWorkbook(), color, fillPatern, 0, 0, 1, 1, 1, 1, false, false, true, 11, "Calibri",HSSFColor.BLACK.index);
//		
//		HSSFRow row = sheet.getRow(rowIndex);
//		// If row is null, then create new row
//		if(null==row){
//			row = sheet.createRow(rowIndex);
//		}
//		
//		HSSFCell cell = row.getCell(colIndex);
//		// If cell is null, then create new cell
//		if(null==cell){
//			cell = row.createCell(colIndex);
//		}
//		if(isString){
//			cell.setCellValue(message);
//		}else{
//			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//			cell.setCellValue(Double.parseDouble(message));
//		}
//		cell.setCellStyle(cellStyle);	
//	}
}

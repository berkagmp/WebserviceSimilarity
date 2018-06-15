package derek.project;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelBuilder {
	static HSSFRow row;
	static HSSFCell cell;

	public void buildExcel(Map<Double, Long> map, String fileName) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("mySheet");
		int rowIndex = 0;
		
		for (Entry<Double, Long> entry : map.entrySet()) {
		    row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(entry.getKey());
			row.createCell(1).setCellValue(entry.getValue());
		}
		
		/*
		 * row = sheet.createRow(1); row.createCell(0).setCellValue("DATA 21");
		 * row.createCell(1).setCellValue("DATA 22");
		 * row.createCell(2).setCellValue("DATA 23");
		 */

		FileOutputStream outFile;

		try {
			Path path = Paths.get("");
			outFile = new FileOutputStream(path.toAbsolutePath().toString() + "/resources/" + fileName + ".xls");
			workbook.write(outFile);
			outFile.close();
			System.out.println("The file has been created");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
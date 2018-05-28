package derek.project.util;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelBuilder {
	static HSSFRow row;
	static HSSFCell cell;

	public static void buildExcel(List<Map<String, Double>> resultList) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("mySheet");
		int rowIndex = 0;
		Iterator iterator;
		Entry entry;

		for (Map<String, Double> m : resultList) {
			iterator = m.entrySet().iterator();
			
			while (iterator.hasNext()) {
				row = sheet.createRow(rowIndex++);

				entry = (Entry) iterator.next();
				row.createCell(0).setCellValue(entry.getKey().toString());
				row.createCell(1).setCellValue(entry.getValue().toString());
			}
		}

		/*
		 * row = sheet.createRow(1); row.createCell(0).setCellValue("DATA 21");
		 * row.createCell(1).setCellValue("DATA 22");
		 * row.createCell(2).setCellValue("DATA 23");
		 */

		FileOutputStream outFile;

		try {
			Path path = Paths.get("");
			outFile = new FileOutputStream(path.toAbsolutePath().toString() + "/resources/result.xls");
			workbook.write(outFile);
			outFile.close();
			System.out.println("The file has been created");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
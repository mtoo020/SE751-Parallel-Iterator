package jxl.demo;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.FormulaCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.formula.FormulaException;
import jxl.read.biff.BiffException;

public class Main {

	public static void main(String[] args) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File("test.xls"));
			Sheet sheet = workbook.getSheet(0);
			Cell a1 = sheet.getCell(0, 0);

			if (a1.getType() == CellType.NUMBER_FORMULA
					|| a1.getType() == CellType.STRING_FORMULA
					|| a1.getType() == CellType.BOOLEAN_FORMULA
					|| a1.getType() == CellType.DATE_FORMULA
					|| a1.getType() == CellType.FORMULA_ERROR) {
				FormulaCell cell = (FormulaCell) a1;
				System.out.println(cell.getFormula());
			}
		} catch (BiffException | IOException e) {
			e.printStackTrace();
		} catch (FormulaException e) {
			e.printStackTrace();
		}
	}

}

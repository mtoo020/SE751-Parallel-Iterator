import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIteratorFactory;

import java.io.File;
import java.util.*;

import jxl.Cell;
import jxl.CellType;
import jxl.FormulaCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.formula.FormulaException;

public class MainDAG {
	public static void main(String[] args) throws Exception{
		int threadCount = 2;
		int chunkSize = 2;
		
		GraphAdapterInterface<INode, String> dag = new GraphAdapter(createNodesFromXLS("test.xls"));
		
		@SuppressWarnings("unchecked")
		ParIterator<INode> pi = ParIteratorFactory.getTreeParIteratorDFSonDAGTopBottom(dag, dag.getRoot(), threadCount);
		
		// Create and start a pool of worker threads
		Thread[] threadPool = new WorkerThread[threadCount];
		for (int i = 0; i < threadCount; i++) {
		    threadPool[i] = new WorkerThread(i, pi);
		    threadPool[i].start();
		}
		
		// ... Main thread may compute other (independent) tasks
		
		// Main thread waits for worker threads to complete
		for (int i = 0; i < threadCount; i++) {
		    try {
		    	threadPool[i].join();
		    } catch(InterruptedException e) {
		    	e.printStackTrace();
		    }
		}
		
		System.out.println("All worker threads have completed.");
		
	}
	
	public static ArrayList<INode> createNodesFromXLS(String filename) throws Exception {
		ArrayList<INode> list = new ArrayList<INode>();
		
		Workbook workbook = Workbook.getWorkbook(new File(filename));
		Sheet sheet = workbook.getSheet(0);
		
		for (int i=0;i<sheet.getColumns();i++) {
			for (int j=0;j<sheet.getRows();j++) {
				Cell cell = sheet.getCell(i,j);
				if (isFormulaCell(cell)) {
					FormulaCell fc = (FormulaCell)cell;
					INode node = new Node(getCellName(cell),fc.getFormula());
					for (String cellref : fc.getFormula().split("\\+|\\-|\\*|\\/|\\^|\\%")) {						
						node.addChild(new Node(cellref, getCellFormula(cellref, sheet)));
					}
					list.add(node);
				}
				else if (isNumberCell(cell)) {
					list.add(new Node(getCellName(cell), cell.getContents()));
				}
			}
		}
				
		return list;
	}
	
	/**
	 * Generate cell name given. e.g. column 1 and row 1 returns A1
	 */
	private static String getCellName(Cell cell){
		int columnId; 
		int rowId; 
		
		if(isFormulaCell(cell)){
			FormulaCell fc = (FormulaCell)cell;
			columnId = fc.getColumn()+65; 
			rowId = fc.getRow();
		}else{
			columnId = cell.getColumn()+65;
			rowId = cell.getRow();
		}
				
		return Character.toString ((char) columnId) + rowId;
	}
	
	/**
	 * Retrieve cell formula given cellName
	 * @param sheet 
	 * @param fc 
	 */
	private static String getCellFormula(String cellName, Sheet sheet){
		char[] splitString = cellName.toCharArray();
		
		int columnId = (int)splitString[0] - 65;
		String rowString = (Character.toString(splitString[1]));
		int rowId = Integer.parseInt(rowString);
		
		Cell cell = sheet.getCell(columnId,rowId-1);
		
		if(isFormulaCell(cell)){
			FormulaCell fc = (FormulaCell)cell;
			try {
				return fc.getFormula();
			} catch (FormulaException e) {
				e.printStackTrace();
			}
			
		}else{
			return cell.getContents();
		}
		
		
		return null;
	}

	private static boolean isFormulaCell(Cell cell) {
		return cell.getType() == CellType.NUMBER_FORMULA
				|| cell.getType() == CellType.STRING_FORMULA
				|| cell.getType() == CellType.BOOLEAN_FORMULA
				|| cell.getType() == CellType.DATE_FORMULA
				|| cell.getType() == CellType.FORMULA_ERROR;
	}
	private static boolean isNumberCell(Cell cell) {
		return cell.getType() == CellType.NUMBER;
	}
}

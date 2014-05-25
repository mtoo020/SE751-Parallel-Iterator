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
					INode node = new Node(fc.getFormula());
					for (String cellref : fc.getFormula().split("\\+|\\-|\\*|\\/|\\^|\\%")) {
						node.addChild(new Node(cellref));
					}
					list.add(node);
				}
				else if (isNumberCell(cell)) {
					list.add(new Node(cell.getContents()));
				}
			}
		}
		return list;
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

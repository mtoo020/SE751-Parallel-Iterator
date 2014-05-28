import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Cell;
import jxl.CellType;
import jxl.FormulaCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.formula.FormulaException;
import jxl.read.biff.BiffException;
import pi.GraphAdapterInterface;
import pi.INode;
import pi.Parser;

public class XLSParser implements Parser {
	private Workbook workbook;

	public XLSParser(String filename) throws BiffException, IOException {
		workbook = Workbook.getWorkbook(new File(filename));
	}

	public GraphAdapterInterface<INode, String> parse() {
		HashMap<String, INode> nodes = new HashMap<String, INode>();
		ArrayList<INode> leaves = new ArrayList<INode>();
		ArrayList<INode> formulaNodes = new ArrayList<INode>();

		Sheet sheet = workbook.getSheet(0);
		
		//limitations
		//- only looks at the first sheet
		//- only looks at cells with numbers or number formulas
		//- only handles "+ - * / ^ %" operations - no brackets or functions
		//- only works with columns A-Z

		//visit and cells and store nodes
		for (int i = 0; i < sheet.getColumns(); i++) {
			for (int j = 0; j < sheet.getRows(); j++) {
				Cell cell = sheet.getCell(i, j);
				if (cell.getType() == CellType.NUMBER_FORMULA) {
					try {
						INode formulaNode = new Node(getName(cell), ((FormulaCell) cell).getFormula());
						nodes.put(formulaNode.getName(), formulaNode);
						formulaNodes.add(formulaNode);
					} catch (FormulaException e) {
						e.printStackTrace();
					}
				} else if (cell.getType() == CellType.NUMBER) {
					INode numberNode = new Node(getName(cell), cell.getContents());
					nodes.put(numberNode.getName(), numberNode);
					leaves.add(numberNode);
				}
			}
		}
		
		//define children for each formula node based on its cell references
		for (INode formulaNode : formulaNodes) {
			boolean hasCellReferences = false;
			for (String operand : ((String)formulaNode.getData()).split("\\+|\\-|\\*|\\/|\\^|\\%")) {
				//if the formula refers to another cell
				if ('A' <= operand.charAt(0) && operand.charAt(0) <= 'Z') {
					INode childNode = nodes.get(operand);
					if (childNode == null) {
						System.out.println("Cell reference (" + operand + ") not found");
					}
					//add the node as a child
					formulaNode.addChild(childNode);
					childNode.addParent(formulaNode);
					hasCellReferences = true;
				}
			}
			if (!hasCellReferences) {
				leaves.add(formulaNode);
			}
		}
	
		return new GraphAdapter(nodes.values(), leaves);
	}

	private static String getName(Cell cell) {
		return "" + (char) (cell.getColumn() + 65) + (cell.getRow() + 1);
	}
}

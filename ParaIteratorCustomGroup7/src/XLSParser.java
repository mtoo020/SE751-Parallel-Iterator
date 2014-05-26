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
		ArrayList<INode> nodes = new ArrayList<INode>();
		ArrayList<INode> leaves = new ArrayList<INode>();
		
		HashMap<String, INode> nodeMap = new HashMap<String, INode>();
		ArrayList<INode> formulaNodes = new ArrayList<INode>();

		Sheet sheet = workbook.getSheet(0);
		
		//limitations
		//- only looks at the first sheet
		//- only looks at cells with numbers or number formulas
		//- only handles "+ - * / ^ %" operations - no brackets or functions
		//- only works with columns A-Z
		//- has to add all nodeMap elements into an ArrayList at the end (duplication of effort)
		//- could be solved if GraphAdapter accepted a Collection

		//visit and cells and store nodes
		for (int i = 0; i < sheet.getColumns(); i++) {
			for (int j = 0; j < sheet.getRows(); j++) {
				Cell cell = sheet.getCell(i, j);
				if (cell.getType() == CellType.NUMBER_FORMULA) {
					try {
						INode formulaNode = new Node(getName(cell), ((FormulaCell) cell).getFormula());
						nodeMap.put(formulaNode.getName(), formulaNode);
						formulaNodes.add(formulaNode);
					} catch (FormulaException e) {
						e.printStackTrace();
					}
				} else if (cell.getType() == CellType.NUMBER) {
					INode numberNode = new Node(getName(cell), cell.getContents());
					nodeMap.put(numberNode.getName(), numberNode);
					leaves.add(numberNode);
				}
			}
		}
		
		//define parents for each cell based on cell references (formulas refer to their parents)
		for (INode formulaNode : formulaNodes) {
			boolean hasCellReferences = false;
			for (String operand : formulaNode.getFormula().split("\\+|\\-|\\*|\\/|\\^|\\%")) {
				//if the formula refers to another cell
				if ('A' <= operand.charAt(0) && operand.charAt(0) <= 'Z') {
					INode parentNode = nodeMap.get(operand);
					if (parentNode == null) {
						System.out.println("Cell reference (" + operand + ") not found");
					}
					parentNode.addChild(formulaNode);
					formulaNode.addParent(parentNode);
					hasCellReferences = true;
				}
			}
			if (!hasCellReferences) {
				leaves.add(formulaNode);
			}
		}
	
		nodes.addAll(nodeMap.values());
		return new GraphAdapter(nodes, leaves);
	}

	private static String getName(Cell cell) {
		return "" + (char) (cell.getColumn() + 65) + (cell.getRow() + 1);
	}
}

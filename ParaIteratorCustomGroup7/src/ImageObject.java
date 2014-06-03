public class ImageObject {
	private String formula;
	private String imageLink;

	public ImageObject(String formula) {
		String[] split = formula.split("/", 2);
		if (split.length == 2) {
			this.formula = split[0];
			this.imageLink = split[1];
		} else {
			this.imageLink = split[0];
		}
		// System.out.println(this.formula);
	}

	public String getFormula() {
		return formula;
	}

	public String getImageLink() {
		return imageLink;
	}
}

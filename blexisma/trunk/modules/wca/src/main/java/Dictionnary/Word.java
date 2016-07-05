package Dictionnary;


public class Word {
	
	private String ids;
	private Definition def;
	
	public Word(){
		ids = "";
		def = new Definition(1);
	}
	

	public void setIDS(String string) {
		this.ids = string;
	}
	
	public String getIDS() {
		return this.ids;
	}

	public void setDef(String string) {
		def = new Definition(string);
	}
	
	public Definition getDef() {
		return this.def;
	}

	public String toString() {
		return this.ids + " (" + this.def.toString() + ")";
	}
}

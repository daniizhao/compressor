package domini;


public class Fitxer {
	
	//ATRIBUTS CLASSE

	private String nom;
	private String path;
	private String extensio;
	private byte[] contingut;
	private int mida;
	private boolean comprimit;

	//METODES CLASSE

	//CONSTRUCTORES

	public Fitxer() {}
	
	public Fitxer(String nom1, String path1, String extensio1, byte[] contingut1, int mida2, boolean comprimit1) {
		this.nom = nom1;
		this.path = path1;
		this.extensio = extensio1;
		this.contingut = contingut1;
		this.mida = mida2;
		this.comprimit = comprimit1;
	}

	//GETTERS

	//obtenir nom del fitxer
	public String getNom() {
		return nom;
	}

	//obtenir path del fitxer
	public String getPath() {
		return path;
	}

	//obtenir extensio del fixter
	public String getExtensio() {
		return extensio;
	}

	//obtenir contingut del fitxer
	public byte[] getContingut() {
		return contingut;
	}

	//obtenir mida del fitxer
	public Integer getMida() {
		return mida;
	}

	//obtenir si el fitxer està comprimit
	public boolean estaComprimit() {
		return comprimit;
	}


	//SETTERS

	//assignar nom del fitxer
	public void setNom(String nom) {
		this.nom = nom;
	}

	//assignar path del fitxer
	public void setPath(String path) {
		this.path = path;
	}

	//assignar extensio del fitxer
	public void setExtensio(String extensio) {
		this.extensio = extensio;
	}

	//assignar contingut del fitxer
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}

	//assignar mida del fitxer
	public void setMida(int mida) {
		this.mida = mida;
	}

	//assignar si el fitxer està comprimit
	public void setComprimit(boolean comprimit) {
		this.comprimit = comprimit;
	}
}

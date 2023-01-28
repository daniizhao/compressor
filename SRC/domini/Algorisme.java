package domini;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Algorisme {
	// ATRIBUTS

	protected String nom;
	protected EstadistiquesGlobals estadistiquesGlobals;

	// CONSTRUCTORES

	// La constructora es crida des de les subclasses
	protected Algorisme(String nom) {
		this.nom = nom;
		this.estadistiquesGlobals = new EstadistiquesGlobals();
	}

	// Funció per obtenir una instància d'un algorisme.
	public static Algorisme getInstance(String nom) {
		switch(nom) {
			case "lz78":
				return LZ78.getInstance();

			case "lzss":
				return LZSS.getInstance();

			case "lzw":
				return LZW.getInstance();

			case "jpeg":
				return JPEG.getInstance();

			default:
				return null;
		}
	}

	// MÉTODES

	// FUNCIONS PER SABER ELS ALGORISMES DISPONIBLES AL SISTEMA
	// Funcio que retorna tots els algorismes disponibles.
    public static List<String> getNomAlgorismes() {
		List<String> l = getNomAlgorismesText();
		for(String s: getNomAlgorismesImatge()) l.add(s);
		return l;
    }

	// Funció que retorna tots els algorismes disponibles per comprimir imatges disponibles al sistema.
    public static  List<String> getNomAlgorismesImatge(){
		List<String> l = new ArrayList<String>();
		l.add("jpeg");
		return l;
	}

	// Funció que retorna tots els algorismes disponibles per comprimir text al sistema.
	public static List<String> getNomAlgorismesText(){
		List<String> l = new ArrayList<String>();
		l.add("lz78");
		l.add("lzss");
		l.add("lzw");
		return l;
	}

	// GETTERS I SETTERS
	// Assignar el nom de l'algorisme utilitzat a la classe.
	public String getNom() {
		return this.nom;
	}

	// ESTADISTIQUES GLOBALS DE L'ALGORISME
	// Funció per obtenir les estadísituqes globals corresponent a l'algorisme.
	public EstadistiquesGlobals getEstadistiquesGlobals() {
		return estadistiquesGlobals;
	}

	// MÉTODES DE COMPRESSIÓ I DESCOMPRESSIÓ

	// Métode genéric de tots els algorismes per tal de poder realitzar la compressió d'un fitxer simple.
	// Aquesta funció també és l'encarregada de generar les Estadísitques Locals de compressió i fer l'actulització de les Globals corresponents al seu algorisme.
	public ResultatAlgorisme comprimir(byte[] input) throws IOException {
		long inici = System.nanoTime();
		byte[] contingut = this.comprimirContingut(input);
		long end = System.nanoTime();
		float temps = (float)(end - inici)/1000000;
		ResultatAlgorisme output = new ResultatAlgorisme();
		output.setContingut(contingut);
		EstadistiquesLocals estadistiques = new EstadistiquesLocals(temps, input.length, contingut.length, true);
		output.setEstadistiques(estadistiques);
		estadistiquesGlobals.actualitzaEstadistiques(estadistiques);
		return output;
	}

	// Métode genéric de tots els algorismes per tal de poder realitzar la descompressió d'un fitxer comprimit.
	// Aquesta funció també és l'encarregada de generar les Estadísitques Locals de descompressió i fer l'actulització de les Globals corresponents al seu algorisme.
	public ResultatAlgorisme descomprimir(byte[] input) throws IOException {
		long inici = System.nanoTime();
		byte[] contingut = this.descomprimirContingut(input);
		long end = System.nanoTime();
		float temps = (float)(end - inici)/1000000;
		ResultatAlgorisme output = new ResultatAlgorisme();
		output.setContingut(contingut);
		EstadistiquesLocals estadistiques = new EstadistiquesLocals(temps, input.length, contingut.length, false);
		output.setEstadistiques(estadistiques);
		estadistiquesGlobals.actualitzaEstadistiques(estadistiques);
		return output;
		
	}

	// Funció abstracta que implementa cada algorisme concret per fer la compressió del contingut del fitxer.
	abstract protected byte[] comprimirContingut(byte[] input) throws IOException;

	// Funció abstracta que implementa cada algorisme concret per fer la descompressió del contingut del fitxer comprimit.
	abstract protected byte[] descomprimirContingut(byte[] input) throws IOException;
	
	
}


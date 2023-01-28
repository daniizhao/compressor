package domini;

import java.util.ArrayList;
import java.util.List;

public class ControladorEstadistiquesGlobals {
	
	// CONSTRUCTORA
	public ControladorEstadistiquesGlobals() {}
	
	// METODES
	/*
	PRE: nom algorisme correcte
	POST: Retorna una llista de Strings que conte la velocitat mitjana de compressio i descompressio i el numero total de compressions i descompressions.
	Funcio per obtenir les estadistiques globals d'un algorisme.
	 */
	public List<String> getEstadistiquesGlobals(String nom_algorisme) {
		List<String> resultat = new ArrayList<>();
		Algorisme a;
		EstadistiquesGlobals eg;
		
		switch (nom_algorisme) {
		default :					        // poso com a valor default l'algorisme LZ78 per assegurarme que s'inicialitza la variable.
			a = LZ78.getInstance();
			break;
		
		case "lzss" :
			a = LZSS.getInstance();
			break;
		
		case "lzw" :
			a = LZW.getInstance();
			break;
			
		case "jpeg" :
			a = JPEG.getInstance();
			break;
		}
		
		eg = a.getEstadistiquesGlobals();		//estadistiques globals de algorisme escollit
		
		resultat.add(String.valueOf(eg.getVelMitjanaCompressio()));
		resultat.add(String.valueOf(eg.getVelMitjanaDescompressio()));
		resultat.add(String.valueOf(eg.getNumTotalComp()));
		resultat.add(String.valueOf(eg.getNumTotalDescomp()));
		
		return resultat;
	}
	
}

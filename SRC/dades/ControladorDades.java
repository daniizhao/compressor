package dades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ControladorDades {
	
	public static String getNomFitxer(String path) {
		/*
		Pre: El path correspon a un fitxer amb una extensio
		Post: S'ha obtingut el nom del fitxer sense l'extensio
		 */
		File f = new File(path);
		String nom = f.getName();
		int index = nom.length()-1;
		while (index > 0 && nom.charAt(index)!='.') index--;
		if (index > 0) nom = nom.substring(0,index);
		return nom;
		
	}
	
	public static String getExtensioFitxer(String path) {
		/*
		Pre: El path correspon a un fitxer amb una extensio
		Post: S'ha obtingut l'extensio del fitxer
		 */
		File f= new File(path);
		String ext = f.getName();
		int index = ext.length()-1;
		while (index > 0 && ext.charAt(index)!='.') index--;
		return ext.substring(index+1);
	}

	public static byte[] getContingutFitxer(String path) throws IOException {
		/*
		Pre: El path correspon a un fitxer
		Post: S'ha llegit el contingut del fitxer corrsponent al path
		 */
		File f = new File(path);
		FileInputStream fis = new FileInputStream(f);
		byte[] llegit = new byte[(int) f.length()];
		fis.read(llegit);
		fis.close();
		return llegit;
	}
	
	public static boolean esCarpeta(String path) {
		/*
		Pre: --
		Post: Si el path es d'una carpeta es retorna cert. Altrament, es retorna fals.
		 */
		// El path és d'una carpeta o fitxer
		File f = new File(path);
		return f.isDirectory();
	}
	
	public static String getNomCarpeta(String path) {
		/*
		Pre: El path correspon a una carpeta
		Post: S'ha obtingut el nom de la carpeta
		 */
		// El path és d'una carpeta
		File f = new File(path);
		return f.getName();
	}
	
	public static String[] getFitxers(String path) {
		/*
		Pre: El path correspon a una carpeta
		Post: S'han obtingut els noms de les subcarpetes i els fitxers que conte la carpeta indicada al path, no els
		paths sencers
		 */
		File f = new File(path);
		return f.list();
	}
	
	public static void creaCarpeta(String path, String nom_carpeta) {
		/*
		Pre: El path corrspon a un directori existent
		Post: S'ha creat una carpeta amb el nom indicat al directori corresponent a path
		 */
		File f = new File(path+File.separator+nom_carpeta);
		f.mkdir();
	}

	public static void creaFitxer(String nom_fitxer, String path, String extensio, byte[] contingut) throws IOException{
		/*
		Pre: El path correspon a un directori existent
		Post: S'ha creat un fitxer amb el nom i extesio indicats al directori corresponent al path amb el contingut que
		conte l'array de bytes. Si el fitxer ja existia, es sobreescriu.
		 */
		FileOutputStream fos = new FileOutputStream(path + File.separator + nom_fitxer + "." + extensio);
		fos.write(contingut);
		fos.close();
	}
}

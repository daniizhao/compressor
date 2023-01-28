package domini;

import java.util.List;

public class ControladorAlgorisme {
    public ControladorAlgorisme() {}

    public static  List<String> getAlgorismes() {
        /*
        Pre: --
        Post: S'han obtingut els noms dels algorismes de compressio disponibles
         */
        return Algorisme.getNomAlgorismes();
    }

    public static List<String> getAlgorismes(String path) {
        /*
        Pre: --
        Post: S'han obtingut els noms dels algorismes d'imatges disponibles si el path era d'un fitxer ppm. Altrament
        s'han obtingut els noms dels algorismes de text disponibles
         */
        if (!dades.ControladorDades.esCarpeta(path) && dades.ControladorDades.getExtensioFitxer(path).equals("ppm")) {
            return Algorisme.getNomAlgorismesImatge();
        }
        return Algorisme.getNomAlgorismesText();
    }
}

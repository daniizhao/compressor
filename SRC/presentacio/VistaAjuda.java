package presentacio;

import domini.ControladorArxius;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class VistaAjuda extends JFrame {
    private JPanel panelAjuda;
    private JButton btnTanca;
    private JScrollPane spAjuda;

    private ControladorArxius ctrlArx;

    // CONSTRUCTORA
    public VistaAjuda(String pantalla) throws IOException {
        ctrlArx = new ControladorArxius();

        JFrame frame = new JFrame("Ajuda");

        JTextPane text = new JTextPane();
        text.setEditable(false);

        String path = "";
        String contingut;

        // llegir de la base de dades, els html de cada ajuda
        switch (pantalla) {
            case "comprimir":
                path = "./AjudaPrograma/AjudaComprimir.html";
                break;
            case "descomprimir":
                path = "./AjudaPrograma/AjudaDescomprimir.html";
                break;
            case "comparar":
                path = "./AjudaPrograma/AjudaComparar.html";
                break;
            case "estadistiques":
                path = "./AjudaPrograma/AjudaEstadistiques.html";
                break;
        }

        contingut = new String(ctrlArx.getContingutFitxer(path));
        text.setContentType("text/html");
        text.setText(contingut);

        spAjuda.getViewport().add(text);

        // configurar scrolls
        spAjuda.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        frame.setContentPane(panelAjuda);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnTanca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelAjuda = new JPanel();
        panelAjuda.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(15, 15, 15, 15), -1, -1));
        panelAjuda.setPreferredSize(new Dimension(450, 600));
        btnTanca = new JButton();
        btnTanca.setText("Tanca");
        panelAjuda.add(btnTanca, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        spAjuda = new JScrollPane();
        panelAjuda.add(spAjuda, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelAjuda;
    }
}

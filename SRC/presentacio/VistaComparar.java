package presentacio;

import domini.ControladorArxius;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class VistaComparar extends JFrame {
    private JButton btnTancaComparar;
    private JPanel PanelComparar;
    private JScrollPane spOrig;
    private JScrollPane spRes;
    private JTextPane original;
    private JTextPane resultat;
    private JLabel lblDiff;

    private ControladorArxius ctrlArx = new ControladorArxius();

    // CONSTRUCTORA
    public VistaComparar(byte[] orig, byte[] res, String alg) {

        JFrame frame = new JFrame("Comparar fitxer");

        original = new JTextPane();
        resultat = new JTextPane();
        original.setEditable(false);
        resultat.setEditable(false);

        if (alg.equals("jpeg")) {
            setImatge(orig, res);
        } else {
            setText(orig, res);
        }

        spOrig.getViewport().add(original);
        spRes.getViewport().add(resultat);

        inicialitzarISincronitzarScrolls();

        if (Arrays.equals(orig, res)) {
            lblDiff.setText("No s'han trobat diferències.");
        } else {
            lblDiff.setText("S'han trobat diferències.");
        }

        frame.setContentPane(PanelComparar);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnTancaComparar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    // Funcio auxiliar per afegir els textos origen i resultat al visualitzador de comparar
    private void setText(byte[] o, byte[] r) {
        String sorig = new String(o);
        String sres = new String(r);
        original.setText(sorig);
        resultat.setText(sres);
    }

    // Funcio auxiliar per afegir una imatge origen i resultat al visualitzador de comparar
    private void setImatge(byte[] o, byte[] r) {
        List<Integer> midesImgOrig = ctrlArx.trobarMidesImatge(o);
        int amplada = midesImgOrig.get(0);
        int altura = midesImgOrig.get(1);
        int inici = midesImgOrig.get(3);
        //mostrar imatge ppm
        if (amplada > 0 && altura > 0) {
            BufferedImage iOrig = toBufferedImage(o, amplada, altura, inici);
            original.insertIcon(new ImageIcon(iOrig));
            List<Integer> midesImgRes = ctrlArx.trobarMidesImatge(r);
            inici = midesImgRes.get(3);
            BufferedImage iRes = toBufferedImage(r, amplada, altura, inici);
            resultat.insertIcon(new ImageIcon(iRes));
        }
    }

    // Funcio auxiliar per inicialitzar els valors i politiques dels scrolls
    private void inicialitzarISincronitzarScrolls() {
        // scrollbars del jtextpane original
        spOrig.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        spOrig.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // inicialitzar el scrollbar perque estigui al principi del text
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                spOrig.getVerticalScrollBar().setValue(0);
                spOrig.getHorizontalScrollBar().setValue(0);
            }
        });
        // fer que l'scroll del resultat estigui sincronitzat amb el original
        spOrig.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                spRes.getVerticalScrollBar().setValue(e.getValue());
            }
        });
        spOrig.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                spRes.getHorizontalScrollBar().setValue(e.getValue());
            }
        });

        // scrollbars del jtextpane resultat
        spRes.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        spOrig.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                spRes.getVerticalScrollBar().setValue(0);
                spRes.getHorizontalScrollBar().setValue(0);
            }
        });
        // fer que l'scroll de l'original estigui sincronitzat amb el resultat
        spRes.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                spOrig.getVerticalScrollBar().setValue(e.getValue());
            }
        });
        spRes.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                spOrig.getHorizontalScrollBar().setValue(e.getValue());
            }
        });
    }

    // Funcio auxiliar per traduir byte array que representa un ppm a bufferedimage.
    private BufferedImage toBufferedImage(byte[] img, int width, int height, int inici) {

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int r, g, b, k = inici, pixel;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                r = img[k++] & 0xFF;
                g = img[k++] & 0xFF;
                b = img[k++] & 0xFF;
                pixel = 0xFF000000 + (r << 16) + (g << 8) + b;
                bi.setRGB(i, j, pixel);
            }
        }
        return bi;
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
        PanelComparar = new JPanel();
        PanelComparar.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(15, 15, 15, 15), -1, -1, true, false));
        PanelComparar.setPreferredSize(new Dimension(900, 600));
        final JLabel label1 = new JLabel();
        label1.setText("Fitxer Original:");
        PanelComparar.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Fitxer Resultant:");
        PanelComparar.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnTancaComparar = new JButton();
        btnTancaComparar.setText("Tanca");
        PanelComparar.add(btnTancaComparar, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        spOrig = new JScrollPane();
        PanelComparar.add(spOrig, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, -1), new Dimension(500, 500), new Dimension(1000, 1000), 0, false));
        spRes = new JScrollPane();
        PanelComparar.add(spRes, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, -1), new Dimension(500, 500), new Dimension(1000, 1000), 0, false));
        lblDiff = new JLabel();
        lblDiff.setText("Label");
        PanelComparar.add(lblDiff, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return PanelComparar;
    }
}

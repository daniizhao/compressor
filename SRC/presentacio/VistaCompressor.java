package presentacio;

import domini.ControladorEstadistiquesGlobals;
import domini.ControladorArxius;
import domini.ControladorAlgorisme;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static javax.swing.JOptionPane.showMessageDialog;

public class VistaCompressor extends JFrame {
    // ATRIBUTS
    private JPanel panel1;
    private JTabbedPane tabbedPane1;

    // Atributs Comprimir
    private JPanel comprimir;
    private JTextField tbOrigenComp;
    private JButton btnExaminarOrigenComp;
    private JTextField tbDestiComp;
    private JButton btnExaminarDestiComp;
    private JTextField tbNomSortidaComp;
    private JComboBox<String> cbAlgorismeComp;
    private JLabel lblJPEGComp;
    private JLabel lblMostreigJPEGComp;
    private JRadioButton rbMostreigOnComp;
    private JRadioButton rbMostreigOffComp;
    private JLabel lblQuantitzacioJPEGComp;
    private JRadioButton rbMatriuQualComp;
    private JRadioButton rbMatriuCompComp;
    private JLabel lblAlgorismeCompressioResult;
    private JLabel lblTempsComprimirResult;
    private JLabel lblVelocitatComprimirResult;
    private JLabel lblGrauCompressioResult;
    private JButton btnComprimir;
    private JButton btnHelpComprimir;

    private String comp_carpeta_sortida = "";
    private boolean origenComprimirEsCarpeta;

    // Atributs Descomprimir
    private JPanel descomprimir;
    private JTextField tbOrigenDescomp;
    private JButton btnExaminarOrigenDescomp;
    private JTextField tbCarpetaDestiDescomp;
    private JButton btnExaminarDestiDescomp;
    private JTextField tbNomSortidaDescomp;
    private JLabel lblNomFitxerSortidaDesc;
    private JLabel lblAlgorismeDescompressioResult;
    private JLabel lblTempsDescomprimirResult;
    private JLabel lblVelocitatDescomprimirResult;
    private JButton btnDescomprimir;
    private JButton btnHelpDescomp;

    private String desc_carpeta_sortida = "";

    // Atributs Comparar
    private JPanel comparar;
    private JTextField tbOrigenComparar;
    private JButton btnExaminarOrigenComparar;
    private JComboBox<String> cbAlgorismesComparar;
    private JButton btnComparar;
    private JButton btnHelpComparar;

    // Atributs Estadistiques
    private JPanel estadistiques;
    private JComboBox<String> cbAlgorismesEstadistiques;
    private JLabel lblAlgorisme;
    private JLabel lblNumCompresions;
    private JLabel lblNumDescompresions;
    private JLabel lblVelCompresio;
    private JLabel lblVelDescompresio;
    private JButton btnExportarEstadistiques;
    private JButton btnHelpEstadistiques;

    //controladors
    private final ControladorArxius ctrlArxius = new ControladorArxius();
    private final ControladorEstadistiquesGlobals ctrlEstGlob = new ControladorEstadistiquesGlobals();
    private final ControladorAlgorisme ctrlAlg = new ControladorAlgorisme();

    public VistaCompressor() {

        JFrame frame = new JFrame("Compressor");

        List<String> algorismes = ctrlAlg.getAlgorismes();

        for (String a : algorismes) {
            cbAlgorismesEstadistiques.addItem(a);
        }
        cbAlgorismesEstadistiques.setSelectedItem(null);

        //COMPRIMIR
        btnExaminarOrigenComp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                /*
                Mostra la finestra per triar el fitxer o carpeta. Un cop triat, permet triar entre els algorismes
                que permeten comprimir aquest fitxer o carpeta.
                */
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filtre = new FileNameExtensionFilter("*.TXT, *.PPM  i/o  carpetes", "txt", "ppm");
                fc.setFileFilter(filtre);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int selection = fc.showOpenDialog(btnExaminarOrigenComp);
                if (selection == JFileChooser.APPROVE_OPTION) {
                    File fitxer = fc.getSelectedFile();
                    tbOrigenComp.setText(fitxer.getAbsolutePath());
                    origenComprimirEsCarpeta = fitxer.isDirectory();
                    tbNomSortidaComp.setText(ctrlArxius.getNomFitxer(tbOrigenComp.getText()));
                    cbAlgorismeComp.removeAllItems();
                    List<String> algorismes = ctrlAlg.getAlgorismes(tbOrigenComp.getText());
                    boolean hiHaJPEG = algorismes.contains("jpeg");
                    lblJPEGComp.setVisible(hiHaJPEG);
                    lblMostreigJPEGComp.setVisible(hiHaJPEG);
                    lblQuantitzacioJPEGComp.setVisible(hiHaJPEG);
                    rbMostreigOnComp.setVisible(hiHaJPEG);
                    rbMostreigOffComp.setVisible(hiHaJPEG);
                    rbMatriuQualComp.setVisible(hiHaJPEG);
                    rbMatriuCompComp.setVisible(hiHaJPEG);

                    if (algorismes.size() > 1) cbAlgorismeComp.addItem("Automàtic");
                    for (String a : algorismes) cbAlgorismeComp.addItem(a);
                    cbAlgorismeComp.setSelectedItem(null);
                }
            }
        });
        btnExaminarDestiComp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                /*
                Mostra la finestra per triar la carpeta desti de la compressio.
                 */
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int selection = fc.showOpenDialog(btnExaminarDestiComp);
                if (selection == JFileChooser.APPROVE_OPTION) {
                    File fitxer = fc.getSelectedFile();
                    tbDestiComp.setText(fitxer.getAbsolutePath());
                    comp_carpeta_sortida = fitxer.getAbsolutePath();
                }
            }
        });

        btnComprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                /*
                Inicia la compressio del fitxer o carpeta triat i mostra les estadistiques obtingudes.
                Es mostrara un missatge d'error si es captura alguna excepcio.
                 */
                try {
                    lblAlgorismeCompressioResult.setText("---");
                    lblGrauCompressioResult.setText("---");
                    lblTempsComprimirResult.setText("---");
                    lblVelocitatComprimirResult.setText("---");

                    String algorismeTriat = String.valueOf(Objects.requireNonNull(cbAlgorismeComp.getSelectedItem()));
                    if (comp_carpeta_sortida.isEmpty() || tbNomSortidaComp.getText().isEmpty())
                        throw new NullPointerException("Falten dades");
                    if (origenComprimirEsCarpeta) {
                        ctrlArxius.comprimirCarpeta(tbOrigenComp.getText(), comp_carpeta_sortida, algorismeTriat, tbNomSortidaComp.getText());
                    } else {
                        List<String> result = ctrlArxius.comprimir(tbOrigenComp.getText(), comp_carpeta_sortida, algorismeTriat, tbNomSortidaComp.getText());
                        if (algorismeTriat.equals("jpeg")) {
                            ctrlArxius.setMostreig(rbMostreigOnComp.isSelected());
                            ctrlArxius.setQuantitzacio(rbMatriuCompComp.isSelected());
                        }

                        lblAlgorismeCompressioResult.setText(result.get(1));
                        lblTempsComprimirResult.setText(result.get(2) + " ms");
                        lblVelocitatComprimirResult.setText(result.get(3) + " B/s");
                        lblGrauCompressioResult.setText(result.get(4) + " %");
                    }
                    showMessageDialog(null, "S'ha completat la compressió.", "Missatge", JOptionPane.INFORMATION_MESSAGE);
                } catch (NullPointerException nex) {
                    showMessageDialog(null, "Origen, destí o algorisme no seleccionats.", "ERROR", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    showMessageDialog(null, "Error al executar la compressió: " + e.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnHelpComprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                Mostra la finestra d'ajuda amb el text d'ajuda corresponent a comprimir.
                 */
                try {
                    new VistaAjuda("comprimir");
                } catch (IOException ex) {
                    showMessageDialog(null, "S'ha produït un error al consultar l'ajuda ", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // LISTENERS DESCOMPRIMIR

        // Listener del botó "..." de l'Origen (Per seleccionar el fitxer a descomprimir)
        btnExaminarOrigenDescomp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filtre = new FileNameExtensionFilter("*.LZ78, *.LZSS, *.LZW, *.IMCOMP, *.CCOMP", "lz78", "lzss", "lzw", "imcomp", "ccomp");
                fc.setFileFilter(filtre);
                int selection = fc.showOpenDialog(btnExaminarOrigenDescomp);
                if (selection == JFileChooser.APPROVE_OPTION) {
                    File fichero = fc.getSelectedFile();
                    tbOrigenDescomp.setText(fichero.getAbsolutePath());

                    if (tbOrigenDescomp.getText().contains(".ccomp")) {
                        lblNomFitxerSortidaDesc.setText("Nom carpeta sortida");
                    } else {
                        lblNomFitxerSortidaDesc.setText("Nom fitxer sortida");
                    }

                    tbNomSortidaDescomp.setText(ctrlArxius.getNomFitxer(tbOrigenDescomp.getText()));

                    //Si el que volem descomprimir és un fitxer, llavors no cal que utilitzem el nom, per tant aquest camp el podem eliminar.
                }
            }
        });

        // Listener del botó "..." de la "Carpeta Destí" (Per seleccionar el directori destí de la descompressió)
        btnExaminarDestiDescomp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int selection = fc.showOpenDialog(btnExaminarOrigenDescomp);
                if (selection == JFileChooser.APPROVE_OPTION) {
                    File fichero = fc.getSelectedFile();
                    tbCarpetaDestiDescomp.setText(fichero.getAbsolutePath());
                    desc_carpeta_sortida = fichero.getAbsolutePath();
                }
            }
        });


        // Listener del click al boto de "Descomprimir".
        btnDescomprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    lblAlgorismeDescompressioResult.setText("---");
                    lblTempsDescomprimirResult.setText("---");
                    lblVelocitatDescomprimirResult.setText("---");
                    if (tbOrigenDescomp.getText().isEmpty() || desc_carpeta_sortida.isEmpty())
                        throw new NullPointerException("Falten dades necessàries per realitzar la descompressió.");
                    if (tbNomSortidaDescomp.getText().isEmpty())
                        throw new NullPointerException("Falta el nom de sortida del fitxer.");
                    //Si es decomprimeix una carpeta comprimida
                    if (tbOrigenDescomp.getText().contains(".ccomp")) {
                        ctrlArxius.descomprimirCarpeta(tbOrigenDescomp.getText(), desc_carpeta_sortida, tbNomSortidaDescomp.getText());
                    } else { //En qualssevol altre cas

                        List<String> result = ctrlArxius.descomprimir(tbOrigenDescomp.getText(), desc_carpeta_sortida, tbNomSortidaDescomp.getText());
                        lblAlgorismeDescompressioResult.setText(result.get(1));
                        lblTempsDescomprimirResult.setText(result.get(2) + " ms");
                        lblVelocitatDescomprimirResult.setText(result.get(3) + " B/s");
                    }
                    showMessageDialog(null, "S'ha completat la descompressió.", "Missatge", JOptionPane.INFORMATION_MESSAGE);
                } catch (NullPointerException nex) {
                    showMessageDialog(null, "Origen o destí no seleccionats.", "ERROR", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessageDialog(null, "Error al executar la descompressió del fitxer: " + e.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);

                }
            }
        });

        // Listener del click sobre el boto de "?"
        btnHelpDescomp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new VistaAjuda("descomprimir");
                } catch (IOException ex) {
                    showMessageDialog(null, "S'ha produït un error al consultar l'ajuda ", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //COMPARAR
        btnExaminarOrigenComparar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filtre = new FileNameExtensionFilter("*.TXT, *.PPM", "txt", "ppm");
                fc.setFileFilter(filtre);
                int selection = fc.showOpenDialog(btnExaminarOrigenComparar);
                if (selection == JFileChooser.APPROVE_OPTION) {
                    File fichero = fc.getSelectedFile();
                    tbOrigenComparar.setText(fichero.getAbsolutePath());

                    cbAlgorismesComparar.removeAllItems();
                    List<String> algorismes = ctrlAlg.getAlgorismes(tbOrigenComparar.getText());
                    for (String a : algorismes) cbAlgorismesComparar.addItem(a);
                    cbAlgorismesComparar.setSelectedItem(null);
                }
            }
        });

        btnComparar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String AlgSelected = Objects.requireNonNull(cbAlgorismesComparar.getSelectedItem()).toString();
                    List<byte[]> resultat = ctrlArxius.comparar(tbOrigenComparar.getText(), AlgSelected);
                    new VistaComparar(resultat.get(0), resultat.get(1), AlgSelected);
                } catch (NullPointerException nex) {
                    showMessageDialog(null, "Fitxer o algorisme no seleccionats", "ERROR", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessageDialog(null, "S'ha produït un error al comparar el fitxer: " + ex.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnHelpComparar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new VistaAjuda("comparar");
                } catch (IOException ex) {
                    showMessageDialog(null, "S'ha produït un error al consultar l'ajuda ", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //ESTADISTIQUES GLOBALS

        btnExportarEstadistiques.addActionListener(new
                                                           ActionListener() {
                                                               @Override
                                                               public void actionPerformed(ActionEvent actionEvent) {
                                                                   try {
                                                                       if (cbAlgorismesEstadistiques.getSelectedItem() == null)
                                                                           throw new NullPointerException("No s'ha seleccionat cap algorisme.");
                                                                       JFileChooser fc = new JFileChooser();
                                                                       fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                                                       int selection = fc.showOpenDialog(btnExaminarDestiComp);
                                                                       if (selection == JFileChooser.APPROVE_OPTION) {
                                                                           File fitxer = fc.getSelectedFile();
                                                                           File estadistiques = new File(fitxer.getAbsolutePath() + File.separator + "EstGlobals.txt");
                                                                           BufferedWriter bw = null;
                                                                           String algorisme = (String) cbAlgorismesEstadistiques.getSelectedItem();
                                                                           List<String> resultat = null;
                                                                           try {
                                                                               resultat = ctrlEstGlob.getEstadistiquesGlobals(algorisme);
                                                                           } catch (Exception e) {
                                                                               showMessageDialog(null, "S'ha produït un error a l'exportar les dades: " + e.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                                                                           }
                                                                           try {
                                                                               bw = new BufferedWriter(new FileWriter(estadistiques));
                                                                           } catch (Exception e) {
                                                                               showMessageDialog(null, "S'ha produït un error al guardar les dades al fitxer crear : " + e.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                                                                           }
                                                                           try {
                                                                               if (bw == null)
                                                                                   throw new AssertionError();
                                                                               bw.write("Exportació Estadístiques Globals.");
                                                                               bw.newLine();
                                                                               bw.write("Estadístiques Globals generades per la Compressió i Descompressió de l'algorisme: " + algorisme);
                                                                               bw.newLine();
                                                                               bw.write("Algorisme: " + algorisme);
                                                                               bw.newLine();
                                                                               bw.write("Velocitat Mitjana Compressió: " + resultat.get(0) + " B/s.");
                                                                               bw.newLine();
                                                                               bw.write("Velocitat Mitjana Descompressió: " + resultat.get(1) + " B/s.");
                                                                               bw.newLine();
                                                                               bw.write("Número Total de Compresions: " + resultat.get(2));
                                                                               bw.newLine();
                                                                               bw.write("Número Total de Descompresions: " + resultat.get(3));
                                                                               bw.close();
                                                                               showMessageDialog(null, "S'han exportat correctament les estadístiques globals.", "Missatge", JOptionPane.INFORMATION_MESSAGE);
                                                                           } catch (Exception e) {
                                                                               showMessageDialog(null, "S'ha produït un error en mostrar les estadístiques globals: " + e.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                                                                           }
                                                                       }
                                                                   } catch (NullPointerException nex) {
                                                                       showMessageDialog(null, "No s'ha escollit un algorisme per exportar les estadístiques globals.", "ERROR", JOptionPane.WARNING_MESSAGE);
                                                                   } catch (Exception e) {
                                                                       showMessageDialog(null, "S'ha produït un error al exportar les estadístiques: " + e.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                                                                   }
                                                               }
                                                           });

        cbAlgorismesEstadistiques.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                //List<String> resultat = new ArrayList<>();
                try {
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                        String algorisme = String.valueOf(cbAlgorismesEstadistiques.getSelectedItem());
                        List<String> resultat = ctrlEstGlob.getEstadistiquesGlobals(algorisme);
                        System.out.println("Algorisme seleccionat: " + algorisme);
                        lblAlgorisme.setText(algorisme);
                        lblVelCompresio.setText(resultat.get(0) + " B/s");
                        lblVelDescompresio.setText(resultat.get(1) + " B/s");
                        lblNumCompresions.setText(resultat.get(2));
                        lblNumDescompresions.setText(resultat.get(3));
                    }
                } catch (Exception ex) {
                    showMessageDialog(null, "S'ha produït un error al consultar els algorismes disponibles: " + ex.getMessage(), "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnHelpEstadistiques.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new VistaAjuda("estadistiques");
                } catch (IOException ex) {
                    showMessageDialog(null, "S'ha produït un error al consultar l'ajuda ", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setDoubleBuffered(false);
        panel1.setMinimumSize(new Dimension(-1, -1));
        panel1.setPreferredSize(new Dimension(1000, 500));
        tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comprimir = new JPanel();
        comprimir.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(10, 4, new Insets(15, 15, 15, 15), -1, -1));
        comprimir.setMinimumSize(new Dimension(-1, -1));
        tabbedPane1.addTab("COMPRIMIR", comprimir);
        comprimir.setBorder(BorderFactory.createTitledBorder(""));
        final JLabel label1 = new JLabel();
        label1.setText("Origen");
        comprimir.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        tbOrigenComp = new JTextField();
        tbOrigenComp.setEditable(false);
        comprimir.add(tbOrigenComp, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Carpeta destí");
        comprimir.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 30), new Dimension(120, 30), new Dimension(120, 30), 0, false));
        tbDestiComp = new JTextField();
        tbDestiComp.setEditable(false);
        comprimir.add(tbDestiComp, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nom fitxer sortida");
        comprimir.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 35), new Dimension(130, 30), 0, false));
        tbNomSortidaComp = new JTextField();
        comprimir.add(tbNomSortidaComp, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Algorisme");
        comprimir.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbAlgorismeComp = new JComboBox();
        comprimir.add(cbAlgorismeComp, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        btnExaminarOrigenComp = new JButton();
        btnExaminarOrigenComp.setText("...");
        comprimir.add(btnExaminarOrigenComp, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        btnExaminarDestiComp = new JButton();
        btnExaminarDestiComp.setText("...");
        comprimir.add(btnExaminarDestiComp, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(15, 15, 15, 15), -1, -1));
        comprimir.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Estadístiques Locals"));
        final JLabel label5 = new JLabel();
        label5.setText("Algorisme utilitzat:");
        panel2.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Temps de compressió:");
        panel2.add(label6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Velocitat de compressió:");
        panel2.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblAlgorismeCompressioResult = new JLabel();
        lblAlgorismeCompressioResult.setText("---");
        panel2.add(lblAlgorismeCompressioResult, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblTempsComprimirResult = new JLabel();
        lblTempsComprimirResult.setText("---");
        panel2.add(lblTempsComprimirResult, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblVelocitatComprimirResult = new JLabel();
        lblVelocitatComprimirResult.setText("---");
        panel2.add(lblVelocitatComprimirResult, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Grau de compressió:");
        panel2.add(label8, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblGrauCompressioResult = new JLabel();
        lblGrauCompressioResult.setText("---");
        panel2.add(lblGrauCompressioResult, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        comprimir.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(680, 14), null, 0, false));
        btnComprimir = new JButton();
        btnComprimir.setText("Comprimir");
        comprimir.add(btnComprimir, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 40), new Dimension(130, 40), new Dimension(130, 40), 0, false));
        btnHelpComprimir = new JButton();
        btnHelpComprimir.setText("?");
        comprimir.add(btnHelpComprimir, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        lblJPEGComp = new JLabel();
        lblJPEGComp.setText("Configuració de l'algorisme JPEG");
        lblJPEGComp.setVisible(false);
        comprimir.add(lblJPEGComp, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbMostreigOnComp = new JRadioButton();
        rbMostreigOnComp.setSelected(true);
        rbMostreigOnComp.setText("4:2:0");
        rbMostreigOnComp.setVisible(false);
        comprimir.add(rbMostreigOnComp, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblMostreigJPEGComp = new JLabel();
        lblMostreigJPEGComp.setText("Mostreig");
        lblMostreigJPEGComp.setVisible(false);
        comprimir.add(lblMostreigJPEGComp, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblQuantitzacioJPEGComp = new JLabel();
        lblQuantitzacioJPEGComp.setText("Quantització");
        lblQuantitzacioJPEGComp.setVisible(false);
        comprimir.add(lblQuantitzacioJPEGComp, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbMostreigOffComp = new JRadioButton();
        rbMostreigOffComp.setText("4:4:4 (desactivat)");
        rbMostreigOffComp.setVisible(false);
        comprimir.add(rbMostreigOffComp, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbMatriuQualComp = new JRadioButton();
        rbMatriuQualComp.setSelected(true);
        rbMatriuQualComp.setText("Matriu per més qualitat d'imatge");
        rbMatriuQualComp.setVisible(false);
        comprimir.add(rbMatriuQualComp, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rbMatriuCompComp = new JRadioButton();
        rbMatriuCompComp.setText("Matriu per major compressió");
        rbMatriuCompComp.setVisible(false);
        comprimir.add(rbMatriuCompComp, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        descomprimir = new JPanel();
        descomprimir.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 3, new Insets(15, 15, 15, 15), -1, -1));
        descomprimir.setMinimumSize(new Dimension(-1, -1));
        tabbedPane1.addTab("DESCOMPRIMIR", descomprimir);
        descomprimir.setBorder(BorderFactory.createTitledBorder(""));
        tbOrigenDescomp = new JTextField();
        tbOrigenDescomp.setEditable(false);
        descomprimir.add(tbOrigenDescomp, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Origen");
        descomprimir.add(label9, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        tbCarpetaDestiDescomp = new JTextField();
        tbCarpetaDestiDescomp.setEditable(false);
        descomprimir.add(tbCarpetaDestiDescomp, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), null, 0, false));
        btnExaminarDestiDescomp = new JButton();
        btnExaminarDestiDescomp.setText("...");
        descomprimir.add(btnExaminarDestiDescomp, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        tbNomSortidaDescomp = new JTextField();
        tbNomSortidaDescomp.setText("");
        descomprimir.add(tbNomSortidaDescomp, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(15, 15, 15, 15), -1, -1));
        descomprimir.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("Estadístiques locals"));
        final JLabel label10 = new JLabel();
        label10.setText("Algorisme utilitzat:");
        panel3.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Temps de descompressió:");
        panel3.add(label11, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Velocitat de descompressió:");
        panel3.add(label12, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblAlgorismeDescompressioResult = new JLabel();
        lblAlgorismeDescompressioResult.setText("---");
        panel3.add(lblAlgorismeDescompressioResult, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblTempsDescomprimirResult = new JLabel();
        lblTempsDescomprimirResult.setText("---");
        panel3.add(lblTempsDescomprimirResult, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblVelocitatDescomprimirResult = new JLabel();
        lblVelocitatDescomprimirResult.setText("---");
        panel3.add(lblVelocitatDescomprimirResult, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panel3.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnExaminarOrigenDescomp = new JButton();
        btnExaminarOrigenDescomp.setText("...");
        descomprimir.add(btnExaminarOrigenDescomp, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Carpeta destí");
        descomprimir.add(label13, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        descomprimir.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnDescomprimir = new JButton();
        btnDescomprimir.setText("Descomprimir");
        descomprimir.add(btnDescomprimir, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 40), new Dimension(130, 40), new Dimension(130, 40), 0, false));
        btnHelpDescomp = new JButton();
        btnHelpDescomp.setText("?");
        descomprimir.add(btnHelpDescomp, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(129, 30), new Dimension(50, 30), 0, false));
        lblNomFitxerSortidaDesc = new JLabel();
        lblNomFitxerSortidaDesc.setText("Nom fitxer sortida");
        descomprimir.add(lblNomFitxerSortidaDesc, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        comparar = new JPanel();
        comparar.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(15, 15, 15, 15), -1, -1));
        comparar.setMinimumSize(new Dimension(-1, -1));
        tabbedPane1.addTab("COMPARAR", comparar);
        comparar.setBorder(BorderFactory.createTitledBorder(""));
        final JLabel label14 = new JLabel();
        label14.setText("Origen");
        comparar.add(label14, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        comparar.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Algorisme");
        comparar.add(label15, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        btnComparar = new JButton();
        btnComparar.setText("Comparar");
        comparar.add(btnComparar, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 40), new Dimension(130, 40), new Dimension(130, 40), 0, false));
        tbOrigenComparar = new JTextField();
        tbOrigenComparar.setEditable(false);
        comparar.add(tbOrigenComparar, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        btnExaminarOrigenComparar = new JButton();
        btnExaminarOrigenComparar.setText("...");
        comparar.add(btnExaminarOrigenComparar, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        cbAlgorismesComparar = new JComboBox();
        comparar.add(cbAlgorismesComparar, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        btnHelpComparar = new JButton();
        btnHelpComparar.setText("?");
        comparar.add(btnHelpComparar, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        estadistiques = new JPanel();
        estadistiques.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(15, 15, 15, 15), -1, -1));
        estadistiques.setMinimumSize(new Dimension(-1, -1));
        tabbedPane1.addTab("ESTADÍSTIQUES GLOBALS", estadistiques);
        estadistiques.setBorder(BorderFactory.createTitledBorder(""));
        final JLabel label16 = new JLabel();
        label16.setText("Algorisme");
        estadistiques.add(label16, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 30), new Dimension(130, 30), new Dimension(130, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        estadistiques.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        cbAlgorismesEstadistiques = new JComboBox();
        estadistiques.add(cbAlgorismesEstadistiques, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 3, new Insets(15, 15, 15, 15), -1, -1));
        estadistiques.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("Estadístiques Globals"));
        final JLabel label17 = new JLabel();
        label17.setText("Algorisme:");
        panel4.add(label17, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Número Total de Compresions:");
        panel4.add(label18, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Número Total de Descompresions:");
        panel4.add(label19, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Velocitat Mitjana Compressió:");
        panel4.add(label20, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label21 = new JLabel();
        label21.setText("Velocitat Mitjana Descompressió:");
        panel4.add(label21, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblAlgorisme = new JLabel();
        lblAlgorisme.setText("---");
        panel4.add(lblAlgorisme, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNumCompresions = new JLabel();
        lblNumCompresions.setText("---");
        panel4.add(lblNumCompresions, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNumDescompresions = new JLabel();
        lblNumDescompresions.setText("---");
        panel4.add(lblNumDescompresions, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblVelCompresio = new JLabel();
        lblVelCompresio.setText("---");
        panel4.add(lblVelCompresio, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblVelDescompresio = new JLabel();
        lblVelDescompresio.setText("---");
        panel4.add(lblVelDescompresio, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnHelpEstadistiques = new JButton();
        btnHelpEstadistiques.setText("?");
        estadistiques.add(btnHelpEstadistiques, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 30), new Dimension(50, 30), new Dimension(50, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        estadistiques.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnExportarEstadistiques = new JButton();
        btnExportarEstadistiques.setText("Exportar");
        estadistiques.add(btnExportarEstadistiques, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 40), new Dimension(130, 40), new Dimension(130, 40), 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbMatriuQualComp);
        buttonGroup.add(rbMatriuCompComp);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbMostreigOnComp);
        buttonGroup.add(rbMostreigOffComp);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}

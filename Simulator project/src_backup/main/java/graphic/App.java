package graphic;

import iobp.LogToXES;
import iobp.execution.IOBP;
import iobp.execution.Trigger;
import iobp.Translator;
import org.web3j.crypto.Credentials;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

public class App {

    private JPanel panelMain;

    private JPanel panel_collaborators;
    private JButton btn_openBPMN;
    private JButton btn_startSimulation;

    private JTextField txt_instances;
    private JTextArea txt_traces;
    private JLabel l_collabsOfSelectedFile;
    private JLabel l_instanceNumber;
    private JPanel panelTraces;
    private JButton exportCSVbutton;
    private JButton exportXESbutton;
    private JTextField txt_noisePercentage;
    private JCheckBox dataCleaningCheckBox;
    private JTextField txt_traceAvgSize;

    private JLabel l_supervisorPK;
    private JTextField txt_supervisorPK;
    private ArrayList<JTextField> txt_collabsPrivateAddresess = new ArrayList<>();
    private ArrayList<String> functionsCalls = new ArrayList<>();
    private ArrayList<String> collabsNames = new ArrayList<>();
    private String modelPath = "";

    public App() {

        txt_instances.setEnabled(false);
        btn_startSimulation.setEnabled(false);
        txt_traces.setEnabled(false);
        txt_noisePercentage.setEnabled(false);
        txt_traceAvgSize.setEnabled(false);
        exportCSVbutton.setEnabled(false);
        exportXESbutton.setEnabled(false);
        dataCleaningCheckBox.setSelected(true);

        JScrollPane scrollPane = new JScrollPane(txt_traces);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        panelTraces.add(scrollPane);
        panelTraces.repaint();
        panelTraces.revalidate();

        btn_openBPMN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Se escoge el archivo */
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Select a BPMN 2.0 file");
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileHidingEnabled(true);
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("BPMN files", "bpmn"));

                int result = fileChooser.showOpenDialog(fileChooser);

                if (result != JFileChooser.CANCEL_OPTION) {
                    panel_collaborators.removeAll();
                    panel_collaborators.repaint();
                    panel_collaborators.revalidate();

                    txt_collabsPrivateAddresess.clear();
                    collabsNames.clear();

                    txt_instances.setText("");
                    l_instanceNumber.setText("");

                    l_supervisorPK = new JLabel("Supervisor Private Key: ");
                    txt_supervisorPK = new JTextField("Paste private key here", 20);

                    txt_supervisorPK.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txt_supervisorPK.select(0, txt_supervisorPK.getText().length());
                        }
                    });

                    panel_collaborators.add(l_supervisorPK, panel_collaborators.getComponentCount());
                    panel_collaborators.add(txt_supervisorPK, panel_collaborators.getComponentCount());

                    //Get BPMN File
                    File bpmnFile = fileChooser.getSelectedFile();
                    modelPath = bpmnFile.getAbsolutePath();
                    l_collabsOfSelectedFile.setText("Model loaded " + modelPath + ". Please enter collaborators private keys.");
                    //Perform configuration init
                    Translator translator = new Translator();
                    functionsCalls = translator.parse(modelPath);
                    //Show Collaborators and ask to their private keys
                    for (String f : functionsCalls) {
                        if (f.contains("Collaborator")) {
                            String[] collabName = f.split("\t");
                            //Create JLabel and JTextField
                            JLabel cName = new JLabel(collabName[1] + ": ");
                            JTextField cPrivateAddress = new JTextField(collabName[1] + "'s Private Key", 20);

                            cPrivateAddress.addFocusListener(new FocusAdapter() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                    cPrivateAddress.select(0, cPrivateAddress.getText().length());
                                }
                            });

                            txt_collabsPrivateAddresess.add(cPrivateAddress);
                            collabsNames.add(collabName[1]);

                            panel_collaborators.add(cName, panel_collaborators.getComponentCount());
                            panel_collaborators.add(cPrivateAddress, panel_collaborators.getComponentCount());
                        }
                    }

                    panel_collaborators.revalidate();
                    panel_collaborators.repaint();

                    txt_instances.setEnabled(true);
                    txt_noisePercentage.setEnabled(true);
                    txt_traceAvgSize.setEnabled(true);
                    btn_startSimulation.setEnabled(true);
                    txt_traces.setEnabled(false);
                    exportCSVbutton.setEnabled(false);
                    exportXESbutton.setEnabled(false);
                    txt_traces.setText("");

                    panelMain.revalidate();
                    panelMain.repaint();

                }
            }
        });
        btn_startSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verifyCollabsAddresess() && verifySupervisorPK() && verifyNumberOfInstances()) {
                    txt_traces.setText("");
                    txt_traces.setEnabled(true);
                    txt_instances.setEnabled(false);
                    txt_noisePercentage.setEnabled(false);
                    txt_traceAvgSize.setEnabled(false);
                    btn_startSimulation.setEnabled(false);
                    HashMap<String, String> collaboratorsAddresess = new HashMap<>(); //Name->privateKey (Get pk and sk of each collaborator)
                    int size = txt_collabsPrivateAddresess.size();
                    for (int i = 0; i < size; i++) {
                        Credentials cred = Trigger.getCredentialsFromPrivateKey(txt_collabsPrivateAddresess.get(i).getText());

                        collaboratorsAddresess.put(collabsNames.get(i), cred.getAddress() + ":" + txt_collabsPrivateAddresess.get(i).getText());
                    }

                    new java.util.Timer().schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    l_instanceNumber.setText("Setting up IOBP...");
                                    txt_traces.setText(LogToXES.CSV_HEADER + "\n");
                                    //Init the IOBP
                                    IOBP iobp = new IOBP();
                                    try {
                                        iobp.setup(functionsCalls, collaboratorsAddresess, txt_supervisorPK.getText(), Integer.valueOf(txt_instances.getText()), Integer.valueOf(txt_noisePercentage.getText()), Integer.valueOf(txt_traceAvgSize.getText()), dataCleaningCheckBox.isSelected());
                                    } catch (Exception exception) {
                                        JOptionPane.showMessageDialog(null, "Error while trying to setup IOBP ");
                                        exception.printStackTrace();
                                    }

                                    //Get number of instances
                                    //int n = Integer.parseInt(txt_instances.getText());
                                    l_instanceNumber.setText("Executing IOBP...");
                                    try {
                                        iobp.run();
                                    } catch (Exception exception) {
                                        JOptionPane.showMessageDialog(null, "Error running IOBP instances");
                                        exception.printStackTrace();
                                    }

                                    l_instanceNumber.setText("Getting event log...");

                                    try {
                                        txt_traces.append(iobp.extractEventLog());
                                    } catch (Exception exception) {
                                        JOptionPane.showMessageDialog(null, "Error getting Event Log");
                                        exception.printStackTrace();
                                    }
                                    
                                    l_instanceNumber.setText(txt_instances.getText() + " traces generated.");
                                    txt_instances.setEnabled(true);
                                    btn_startSimulation.setEnabled(true);
                                    exportCSVbutton.setEnabled(true);
                                    exportXESbutton.setEnabled(true);
                                    JOptionPane.showMessageDialog(null, "Done");
                                }
                            },
                            0
                    );
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a private key for supervisor, a private key for each collaborator and a valid number of instances. You can get the private keys from Ganaches' accounts.");
                }
            }


        });
        txt_traces.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
            }
        });

        exportCSVbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String exportPath = modelPath.substring(0, modelPath.lastIndexOf(".")) + ".csv";
                String content = txt_traces.getText();

                FileWriter myWriter = null;
                try {
                    myWriter = new FileWriter(exportPath);
                    myWriter.write(content);
                    myWriter.close();
                    JOptionPane.showMessageDialog(null, "Event Log was successfully exported: " + exportPath);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Error while trying to export traces to CSV");
                }
                /*
                Copy to clipboard
                StringSelection stringSelection = new StringSelection(txt_traces.getText());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);*/
            }
        });

        exportXESbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String exportPath = modelPath.substring(0, modelPath.lastIndexOf(".")) + ".xes";
                String csvContent = txt_traces.getText();
                try {
                    LogToXES.CSVtoXES(csvContent, exportPath);
                    JOptionPane.showMessageDialog(null, "Event Log was successfully exported: " + exportPath);
                } catch (IOException | ParseException ioException) {
                    JOptionPane.showMessageDialog(null, "Error while trying to export traces to XES");
                }

            }
        });
    }

    public void saveTrace(String trace) {
        txt_traces.append(trace);
    }

    public boolean verifyNumberOfInstances() {
        if (!txt_instances.getText().equals("")) {
            try {
                Integer.parseInt(txt_instances.getText());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean verifySupervisorPK() {
        if (!txt_supervisorPK.getText().equals(""))
            return true;
        return false;
    }

    public boolean verifyCollabsAddresess() {
        if (txt_collabsPrivateAddresess.isEmpty() && txt_collabsPrivateAddresess.isEmpty()) {
            return false;
        } else {
            int size = txt_collabsPrivateAddresess.size();
            for (int i = 0; i < size; i++) {
                if (txt_collabsPrivateAddresess.get(i).getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Plase enter all collaborators private keys");
                    return false;
                } else {
                    try {
                        Trigger.getCredentialsFromPrivateKey(txt_collabsPrivateAddresess.get(i).getText());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Plase enter valid private keys");
                        return false;
                    }
                }

            }
        }
        return true;
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        panelMain = new JPanel();
        panelMain.setLayout(new GridBagLayout());
        panel_collaborators = new JPanel();
        panel_collaborators.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        panel_collaborators.setAutoscrolls(false);
        panel_collaborators.setBackground(new Color(-6966597));
        panel_collaborators.setForeground(new Color(-16777216));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 13;
        gbc.gridheight = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(40, 0, 0, 0);
        panelMain.add(panel_collaborators, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Execution and monitoring IOBP using Blockchain");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 14;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 0, 20, 0);
        panelMain.add(label1, gbc);
        l_collabsOfSelectedFile = new JLabel();
        l_collabsOfSelectedFile.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 20, 0, 0);
        panelMain.add(l_collabsOfSelectedFile, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 14;
        gbc.fill = GridBagConstraints.BOTH;
        panelMain.add(panel1, gbc);
        btn_openBPMN = new JButton();
        btn_openBPMN.setText("1) Open BPMN XML");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel1.add(btn_openBPMN, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 13;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 0, 20, 0);
        panelMain.add(panel2, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer3, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Instances:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel2.add(label2, gbc);
        txt_instances = new JTextField();
        txt_instances.setColumns(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(txt_instances, gbc);
        btn_startSimulation = new JButton();
        btn_startSimulation.setHorizontalTextPosition(0);
        btn_startSimulation.setText("2) Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel2.add(btn_startSimulation, gbc);
        l_instanceNumber = new JLabel();
        l_instanceNumber.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel2.add(l_instanceNumber, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("% Noise: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel2.add(label3, gbc);
        txt_noisePercentage = new JTextField();
        txt_noisePercentage.setColumns(5);
        txt_noisePercentage.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(txt_noisePercentage, gbc);
        dataCleaningCheckBox = new JCheckBox();
        dataCleaningCheckBox.setText("Data cleaning");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel2.add(dataCleaningCheckBox, gbc);
        txt_traceAvgSize = new JTextField();
        txt_traceAvgSize.setColumns(5);
        txt_traceAvgSize.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(txt_traceAvgSize, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Trace avg size:");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel2.add(label4, gbc);
        panelTraces = new JPanel();
        panelTraces.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 13;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panelMain.add(panelTraces, gbc);
        txt_traces = new JTextArea();
        txt_traces.setText("");
        txt_traces.setToolTipText("Traces");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 11;
        gbc.weightx = 3.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 0);
        panelTraces.add(txt_traces, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTraces.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        panelTraces.add(spacer5, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Traces");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(20, 20, 0, 20);
        panelTraces.add(label5, gbc);
        exportCSVbutton = new JButton();
        exportCSVbutton.setHorizontalTextPosition(2);
        exportCSVbutton.setText("Export to CSV");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelTraces.add(exportCSVbutton, gbc);
        exportXESbutton = new JButton();
        exportXESbutton.setText("Export to XES");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelTraces.add(exportXESbutton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

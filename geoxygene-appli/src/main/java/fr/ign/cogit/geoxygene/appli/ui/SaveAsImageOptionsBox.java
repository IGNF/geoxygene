package fr.ign.cogit.geoxygene.appli.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.FilenameUtils;

import fr.ign.cogit.geoxygene.appli.I18N;

public class SaveAsImageOptionsBox extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTextField filetextfield;
    private JSpinner spinnerWidth;
    private JSpinner spinnerHeight;

    boolean spinner_lock = false;


    private double imgratio = 1.;

    // 0-> EXPORT FILE IS VALID , -1 -> INVALID
    public int status = -1;
    
    private JCheckBox chckbxWLD = null;
    

    public SaveAsImageOptionsBox() {
        setAlwaysOnTop(true);
        setTitle(I18N.getString("MainFrame.SaveAsImage"));
        setModal(true);
        this.setSize(347, 291);

        JButton btnExport = new JButton("Exporter");
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((int) spinnerHeight.getValue() > 0 && (int) spinnerWidth.getValue() > 0 && !filetextfield.getText().isEmpty())
                    status = 0;
                setVisible(false);
                removeAll();
                dispose();
            }
        });

        JPanel paneldims = new JPanel();
        paneldims.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), I18N.getString("SaveAsImageOptionBox.dimensionlabel"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(
                51, 51, 51)));
        paneldims.setToolTipText(I18N.getString("SaveAsImageOptionsBox.panel.toolTipText")); //$NON-NLS-1$

        JPanel panelfile = new JPanel();
        panelfile.setToolTipText("Image Dimensions");
        panelfile.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), I18N.getString("SaveAsImageOptionBox.filelabel"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51,
                51, 51)));
        
        this.chckbxWLD = new JCheckBox(I18N.getString("SaveAsImageOptionBox.chckbxWLDText")); //$NON-NLS-1$
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(paneldims, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                                .addComponent(btnExport, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addGap(10)
                            .addComponent(panelfile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(chckbxWLD)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(paneldims, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelfile, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(chckbxWLD)
                    .addGap(11)
                    .addComponent(btnExport)
                    .addContainerGap())
        );

        filetextfield = new JTextField();
        filetextfield.setColumns(10);

        JButton choseFileButton = new JButton("...");
        choseFileButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        choseFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int ret = fc.showSaveDialog(SaveAsImageOptionsBox.this);
                File f = fc.getSelectedFile();
                f = SaveAsImageOptionsBox.this.validateFile(f);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    SaveAsImageOptionsBox.this.filetextfield.setText(f.getAbsolutePath());

                }
            }
        });
        GroupLayout gl_panelfile = new GroupLayout(panelfile);
        gl_panelfile.setHorizontalGroup(gl_panelfile.createParallelGroup(Alignment.LEADING).addGroup(
                gl_panelfile.createSequentialGroup().addContainerGap().addComponent(filetextfield, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(choseFileButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        gl_panelfile.setVerticalGroup(gl_panelfile.createParallelGroup(Alignment.LEADING).addGroup(
                gl_panelfile
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_panelfile.createParallelGroup(Alignment.BASELINE).addComponent(filetextfield, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(choseFileButton)).addContainerGap(32, Short.MAX_VALUE)));
        panelfile.setLayout(gl_panelfile);

        JLabel lblLargeur = new JLabel(I18N.getString("SaveAsImageOptionBox.width"));
        lblLargeur.setFont(new Font("Dialog", Font.PLAIN, 12));

        JLabel lblPixels = new JLabel("pixels");
        lblPixels.setFont(new Font("Dialog", Font.PLAIN, 12));

        spinnerWidth = new JSpinner();

        spinnerHeight = new JSpinner();

        spinnerWidth.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int w = (int) spinnerWidth.getValue();
                if (w != 0 && !spinner_lock) {
                    spinner_lock=true;
                    int nh = (int) (w / imgratio);
                    spinnerHeight.setValue(nh);
                    spinner_lock=false;
                }
            }
        });

        spinnerHeight.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int h = (int) spinnerHeight.getValue();
                if (h != 0 && !spinner_lock) {
                    spinner_lock=true;
                    int nw = (int) (h * imgratio);
                    spinnerWidth.setValue(nw);
                    spinner_lock=false;
                }
            }
        });

        JLabel lblHauteur = new JLabel(I18N.getString("SaveAsImageOptionBox.height"));
        lblHauteur.setFont(new Font("Dialog", Font.PLAIN, 12));

        JLabel label_1 = new JLabel("pixels");
        label_1.setFont(new Font("Dialog", Font.PLAIN, 12));
        GroupLayout gl_paneldims = new GroupLayout(paneldims);
        gl_paneldims.setHorizontalGroup(gl_paneldims.createParallelGroup(Alignment.LEADING).addGroup(
                gl_paneldims
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_paneldims.createParallelGroup(Alignment.LEADING).addComponent(lblLargeur).addComponent(lblHauteur))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_paneldims.createParallelGroup(Alignment.LEADING).addComponent(spinnerHeight, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spinnerWidth, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_paneldims.createParallelGroup(Alignment.LEADING).addComponent(lblPixels).addComponent(label_1)).addContainerGap(86, Short.MAX_VALUE)));
        gl_paneldims.setVerticalGroup(gl_paneldims.createParallelGroup(Alignment.LEADING).addGroup(
                gl_paneldims
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_paneldims.createParallelGroup(Alignment.BASELINE).addComponent(lblLargeur)
                                        .addComponent(spinnerWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblPixels))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_paneldims.createParallelGroup(Alignment.BASELINE).addComponent(lblHauteur)
                                        .addComponent(spinnerHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(label_1))
                        .addContainerGap(47, Short.MAX_VALUE)));
        paneldims.setLayout(gl_paneldims);
        getContentPane().setLayout(groupLayout);
    }

    public void setDefaultImageDimensions(int width, int height) {
        imgratio = width / (height == 0 ? 1. : height);
        this.spinnerWidth.setValue(width < 0 ? 0 : width);
        this.spinnerHeight.setValue(height < 0 ? 0 : height);
    }

    protected File validateFile(File f) {
        if (f == null || f.getName().isEmpty()) {
            return null;
        }
        String ext = FilenameUtils.getExtension(f.getName());
        if (!(ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
            return new File(f.getAbsolutePath() + ".png");
        }
        return f;
    }

    public String getFile() {
        return this.filetextfield.getText();
    }

    public int getImageWidth() {
        return (Integer) this.spinnerWidth.getValue();
    }

    public int getImageHeight() {
        return (Integer) this.spinnerHeight.getValue();
    }

    public int getExportStatus() {
        return this.status;
    }

    public boolean saveWld() {
        return this.chckbxWLD.isSelected();
    }
}

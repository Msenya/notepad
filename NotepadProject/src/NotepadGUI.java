import javax.swing.*;
import java.awt.*;
import javax.swing.undo.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;

public class NotepadGUI extends JFrame{

    // File explorer
    private JFileChooser fChooser;

    private JTextArea textArea;
    public JTextArea getTextArea(){
        return textArea;
    }

    private JMenuItem save;

    // Current file being worked on
    private File currentFile;

    // Swing's built in library to manage undo and redo functionality
    private UndoManager uManager;

    // Track whether changes have been made
    private boolean changes = false;

    private JLabel statusLabel;

    // NotepadGUI class constructor
    public NotepadGUI(){
        super("Notepad");

        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // File chooser setup
       fChooser = new JFileChooser();

       // Store the last used diectory path 
       String lastUsedDirectory = "src\\assets";

       // Set current diresctory of file chooser
       fChooser.setCurrentDirectory(new File(lastUsedDirectory));
       fChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

       // Initialize UndoManager
       uManager = new UndoManager();

        // Window listener to prompt user when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                promptToSave();
            }
        });

        addGuiComponents();
        addStatusBar();
    }

    // Method that adds toolbar to JFrame as well as JTextArea for users to type
    private void addGuiComponents(){
        addToolbar();

        // Area for user to type text into
        textArea = new JTextArea();
        textArea.setTabSize(5);
        textArea.getDocument().addUndoableEditListener(e ->{
            // Add each edit done int text area
            uManager.addEdit(e.getEdit());
            changes = true;
            updateStatusBar();
        });

        // Listener to handle tab key pressed
        textArea.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_TAB){
                    e.consume();  // Consume tab event to prevent default tab behaviour
                    int caretPos = textArea.getCaretPosition();
                    textArea.insert("     ", caretPos);
                }
            }
        });

        JScrollPane sPane = new JScrollPane(textArea);
        add(sPane, BorderLayout.CENTER);
    }


    // Add toolbar to GUI containing file, edit and view operations
    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Add menu items
        menuBar.add(addFileMenu());
        menuBar.add(addEditMenu());
        menuBar.add(addViewMenu());

        // Add toolbar to GUI's north border layout
        add(toolBar, BorderLayout.NORTH);
    }


    // Method to add status bar to bottom of frame
    private void addStatusBar(){
        statusLabel = new JLabel("Line: 1, Column: 1     |   Words: 0    |   Zoom: 100%     |   Tab Size: 0");
        add(statusLabel, BorderLayout.SOUTH);
    }


    // Update status bar information
    private void updateStatusBar(){
        try{
            int caretPos = textArea.getCaretPosition();
            int lineNum = textArea.getLineOfOffset(caretPos) + 1;
            int columnNum = caretPos - textArea.getLineStartOffset(lineNum - 1) + 1;
            int wordCount = textArea.getText().isEmpty() ? 0 : textArea.getText().split("\\s+").length;
            int zoomPercent = (int) ((textArea.getFont().getSize()/ 12.0) * 100);
            int tabSize = textArea.getTabSize();
    
            statusLabel.setText(String.format("Line: %d, Column: %d     |   Words: %d    |   Zoom: %d%%     |   Tab Size: %d spaces", lineNum, columnNum, wordCount, zoomPercent, tabSize));

        } catch (BadLocationException e){
            e.printStackTrace();
        }
    }


    // Method to add file menu & menu items to the menubar
    private JMenu addFileMenu(){
        JMenu fileMenu = new JMenu("File");

        // 'New' functionality (resets everything)
        JMenuItem newMenu = new JMenuItem("New");
        newMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenu.addActionListener(e -> {
            // Reset title header  
            setTitle("NotePad"); 
            
            //  Reset text area
            textArea.setText("");

            // Reset current file variable
            currentFile = null;
        });
        fileMenu.add(newMenu);

        // 'Open' functionality (open a text file)
        JMenuItem open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        open.addActionListener(e -> {
            // Open file explorer
            int result = fChooser.showOpenDialog(NotepadGUI.this);

            // Execute code only if open has been clicked
            if (result != JFileChooser.APPROVE_OPTION) return;

            try{
            // Reset notepad
            // doClick() simulates a button click to perform a 'button click' action
            newMenu.doClick();

            // Get selected file
            File selectedFile =fChooser.getSelectedFile();

            // Update current file
            currentFile = selectedFile;

            // Update title header
            setTitle(selectedFile.getName());

            // Read file
            FileReader fReader = new FileReader(selectedFile);
            // Read entire file at once
            BufferedReader bReader = new BufferedReader(fReader);
            
            // Store the text
            // Use string builder to provide efficient way to concatenate strings
            StringBuilder fileText = new StringBuilder();
            String readText;
            // Check for lines to read in text file
            while ((readText = bReader.readLine()) != null){
                fileText.append(readText + "\n");
            }

            // Update text area
            textArea.setText(fileText.toString());
            bReader.close();

            } catch (Exception exception){
                exception.printStackTrace();
            }

        });
        fileMenu.add(open);

        // 'Save as' funtionality (creates new text file and saves user text file)
        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAs.addActionListener(e -> {
            
            int result = fChooser.showSaveDialog(NotepadGUI.this);

            // Execute code only if the user clicked save
            if (result != JFileChooser.APPROVE_OPTION) return;
            try {

                File selectedFile =fChooser.getSelectedFile();

                // Append .txt to the file if it does not have txt extension
                String fileName = selectedFile.getName();
                if (!fileName.substring(fileName.length() - 4).equalsIgnoreCase(".txt")) {
                    selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                }

                // Create new file
                selectedFile.createNewFile();

                // Write the user's text info into the file created
                FileWriter fWriter = new FileWriter(selectedFile);
                // Make write process faster and smoother
                BufferedWriter bWriter = new BufferedWriter(fWriter);
                bWriter.write(textArea.getText());
                bWriter.close();
                fWriter.close();

                // Update title header of GUI to save text file
                setTitle(fileName);

                currentFile = selectedFile;

                // Display dialog
                JOptionPane.showMessageDialog(NotepadGUI.this, "File Saved!");

                // Reset changes flag
                changes = false;

            } catch (Exception exception) {
                exception.getStackTrace();
            }
        });
        fileMenu.add(saveAs);


        // 'Save' functionaloty (saves text from current open text file)
        // Update content of current text file
        save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        save.addActionListener(e -> {

            // Perform save as functionality of current file is null
            if (currentFile == null) saveAs.doClick();

            // If user choses to cancel saving, current file will still be null
            // then we want to prevent executing the rest of the code
            if (currentFile == null) return;

            try{
                // Write to current file
                FileWriter fWriter = new FileWriter(currentFile);
                BufferedWriter bWriter = new BufferedWriter(fWriter);
                bWriter.write(textArea.getText());
                bWriter.close();
                fWriter.close();

                changes = false;

            } catch (Exception exception){
                exception.printStackTrace();
            }
        });
        fileMenu.add(save);

        fileMenu.addSeparator();

        // 'Exit' functionality (ends program)
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> {

        // Dispose of GUI
        NotepadGUI.this.dispose();
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }


    // Method to add edit menu & menu items to menubar
    private JMenu addEditMenu(){
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.addActionListener(e -> {
            // Undo any edit in test area that can be undone
            if (uManager.canUndo()){
                uManager.undo();
            }

        });
        editMenu.add(undoMenuItem);

        JMenuItem redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.addActionListener(e -> {
            // Redo any edit in text arean that can be redone
            if (uManager.canRedo()){
                uManager.redo();
            }

        });
        editMenu.add(redoMenuItem);

        editMenu.addSeparator();

        JMenuItem cutMenuItem = new JMenuItem("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.addActionListener(e -> {
            textArea.cut();
        });
        editMenu.add(cutMenuItem);

        JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.addActionListener(e -> {
            textArea.copy();
        });
        editMenu.add(copyMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.addActionListener(e -> {
            textArea.paste();
        });
        editMenu.add(pasteMenuItem);

        JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.addActionListener(e -> {
            textArea.selectAll();
        });
        editMenu.add(selectAllMenuItem);

        editMenu.addSeparator();

        JMenuItem find = new JMenuItem("Find");
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        find.addActionListener(e -> {
            String textToFind = JOptionPane.showInputDialog(NotepadGUI.this, "Enter text to find.");
            
            if (textToFind != null && textToFind.length() > 0){
                String text = textArea.getText();
                int index = text.indexOf(textToFind);
                if(index >= 0){
                    textArea.setSelectionStart(index);
                    textArea.setSelectionEnd(index + textToFind.length());

                } else JOptionPane.showMessageDialog(NotepadGUI.this, "Text not found!");
            }
        });
        editMenu.add(find);

        JMenuItem replace = new JMenuItem("Replace");
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        replace.addActionListener(e -> {
            String textToFind = JOptionPane.showInputDialog(NotepadGUI.this, "Enter text to find:");

            if (textToFind != null && textToFind.length() > 0) {
                String replaceWith = JOptionPane.showInputDialog(NotepadGUI.this, "Replace with:");
                if (replaceWith != null) {
                    String text = textArea.getText();
                    text = text.replace(textToFind, replaceWith);
                    textArea.setText(text);
                }
            }
        });
        editMenu.add(replace);

        editMenu.addSeparator();

        // Font format
        // Be able to change font family, styling and size
        JMenuItem font = new JMenuItem("Font");
        font.addActionListener(e -> {
            // launch font menu
            new FontMenu(NotepadGUI.this).setVisible(true);
        });
        editMenu.add(font);

        return editMenu;
    }


    // Method to add view menu & menu items to menubar
    private JMenu addViewMenu(){
        JMenu viewMenu = new JMenu("View");

        // Word wrap functionality
        // Word wrap = moves words from end of line to beginning of nexr line to fit the screen
        JCheckBoxMenuItem wordWrap = new JCheckBoxMenuItem("Word Wrap", true);
        wordWrap.addActionListener(e -> {

            boolean isChecked = wordWrap.getState();
            if (isChecked){
                // Wrap words
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
            } else{
                // Unwrap words
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
            }

        });
        viewMenu.add(wordWrap);

        // Aligning text functionality
        JMenu alignText = new JMenu("Align Text");

        // Align text to the left
        JMenuItem alignTextLeft = new JMenuItem("Left");
        alignTextLeft.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        alignTextLeft.addActionListener(e -> {

            textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        });
        alignText.add(alignTextLeft);

        // Align text to the right
        JMenuItem alignTextRight = new JMenuItem("Right");
        alignTextRight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        alignTextRight.addActionListener(e -> {

            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        });
        alignText.add(alignTextRight);

        viewMenu.add(alignText);

        // Status bar toggle menu item
        JCheckBoxMenuItem statusBarToggle = new JCheckBoxMenuItem("Status Bar", true); // Default: status bar is visible
        statusBarToggle.addActionListener(e -> {
            boolean isVisible = statusBarToggle.getState();
            statusLabel.setVisible(isVisible); // Show or hide the status bar label
        });
        viewMenu.add(statusBarToggle);

        JMenu zoom = new JMenu("Zoom");

        JMenuItem zoomIn = new JMenuItem("Zoom In");
        zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        zoomIn.addActionListener(e -> {
            Font currentFont = textArea.getFont();
            textArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 2));
            updateStatusBar();
        });
        zoom.add(zoomIn);

        JMenuItem zoomOut = new JMenuItem("Zoom Out");
        zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        zoomOut.addActionListener(e -> {
            Font currentFont = textArea.getFont();
            textArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() - 2));
            updateStatusBar();
        });
        zoom.add(zoomOut);

        // Mouse wheel listener for zooming in and out (Ctrl + mouse scroll)
        SwingUtilities.invokeLater(() -> {
            textArea.addMouseWheelListener(e -> {
                if (e.isControlDown()){
                   int rotation = e.getWheelRotation();
                   Font currentFont = textArea.getFont();
                   int newSize = currentFont.getSize() + (rotation > 0 ? -2 : 2);
                   textArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), newSize));
                   updateStatusBar();
                }
            });
        });

        JMenuItem restore = new JMenuItem("Restore to Default");
        restore.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
        restore.addActionListener(e -> {
            Font currentFont = textArea.getFont();
            textArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), 12));
            updateStatusBar();
        });
        zoom.add(restore);

        viewMenu.add(zoom);



        viewMenu.addSeparator();

        // Dark theme toggle menu item
        JCheckBoxMenuItem darkTheme = new JCheckBoxMenuItem("Dark Theme");
        darkTheme.addActionListener(e -> {
            toggleTheme();

            // Initialize check mark based on current theme state
            darkTheme.setSelected(isDarkTheme);

            // Add check mark
            ImageIcon checkIcon = new ImageIcon("lib\\checkmark.jpg");
            darkTheme.setSelectedIcon(checkIcon);
        });
        viewMenu.add(darkTheme);

        return viewMenu;
    }

    // Boolean to track current theme
    private boolean isDarkTheme = true;


    // Method to toggle betwwen dark and light theme
    private void toggleTheme(){
        
        if (isDarkTheme){
        textArea.setBackground(Color.WHITE);  // Text area background colour
        textArea.setForeground(Color.BLACK);  // Text colour
        textArea.setCaretColor(Color.BLACK);  // Line cursor colour
        } else{
            textArea.setBackground(new Color(45, 45, 45));  // Text area background colour
            textArea.setForeground(Color.WHITE);  // Text colour
            textArea.setCaretColor(Color.WHITE);  // Line cursor colour
        }
        isDarkTheme = !isDarkTheme;
    }


    // Method to prompt user to save cahnges before exiting if not saved before
    private void promptToSave(){
        // Check if changes have been made
        if (changes){
            JDialog dialog = new JDialog(this, "Notepad", true);
            dialog.setLayout(new BorderLayout());
            dialog.setResizable(false);
            JLabel label = new JLabel("Do you want to save the changes");
            dialog.add(label, BorderLayout.NORTH);

            JButton saveBtn = new JButton("Save");
            JButton dontSaveBtn = new JButton("Don't Save");
            JButton cancelBtn = new JButton("Cancel");

            // "Save" button action listener
            saveBtn.addActionListener(e -> {
                save.doClick();
                if (!changes){
                    dispose();
                }
                dialog.dispose();
            });

            // "Don't Save" button action listener
            dontSaveBtn.addActionListener(e -> {
                dispose();
                dialog.dispose();
            });

            // "Cancel" button action listener
            cancelBtn.addActionListener(e -> {
                dialog.dispose();
            });

            // Add buttons
            JPanel btnPanel = new JPanel();
            btnPanel.add(saveBtn);
            btnPanel.add(dontSaveBtn);
            btnPanel.add(cancelBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);
            

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } else dispose();
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}
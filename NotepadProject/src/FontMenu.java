// Font menu should open as a dialog

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class FontMenu extends JDialog{
    
    // Reference GUI to make changes to GUI from this class
    private NotepadGUI source;

    // Global variables to be apply changes to text area made in font setting
    private JTextField currentFont, currentFStyleField, current_fSize;
    private JPanel currentColour;

    // Method creates dialog for font options
    public FontMenu(NotepadGUI source){
        this.source = source;

        setTitle("Font Settings");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);  // Dispose used resources once closed
        setSize(365, 380);
        setLocationRelativeTo(source);  // Launch menu at the center of GUI
        setResizable(false);

        // Make dialog modal (user will be unable to interact with Notepad until dialog is closed)
        setModal(true);

        // Remove layout management = more control of GUI components
        setLayout(null);

        addFontMenuComponents();
    }


    // Method adds font optios, apply button and cancel button to dialog
   private void addFontMenuComponents(){
        addFont();
        addFontStyle();
        addFontSize();
        addFontColour();

        // Apply changes to font
        JButton applyBtn = new JButton("Apply");
        applyBtn.setBounds(150, 300, 90, 30);
        // Update text area
        applyBtn.addActionListener(e -> {
            // Get font style
            String fontType = currentFont.getText();

            // Get font style
            int fontStyle;
            switch (currentFStyleField.getText()){

                case "Plain":
                    fontStyle = Font.PLAIN;
                    break;

                case "Bold":
                    fontStyle = Font.BOLD;
                    break;

                case "Italic":
                    fontStyle = Font.ITALIC;
                    break;

                default:  // Bold italic
                    fontStyle = Font.BOLD | Font.ITALIC;
                    break;
            }

            // Get font size
            int fontSize = Integer.parseInt(current_fSize.getText());
            // Get font colour
            Color fontColor = currentColour.getBackground();

            // Create font
            Font newFont = new Font(fontType, fontStyle, fontSize);

            source.getTextArea().setFont(newFont);
            source.getTextArea().setForeground(fontColor);

            // Dispose menu
            FontMenu.this.dispose();
        });
        add(applyBtn);

        // Cancel(exit font settings menu)
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(250, 300, 90, 30);
        cancelBtn.addActionListener(e -> {
            // Dispose menu
            FontMenu.this.dispose();
        });
        add(cancelBtn);
        
   }


   // Method creates label for font and displays current and available font names
   private void addFont(){
        JLabel fontLabel = new JLabel("Font");
        fontLabel.setBounds(25, 5, 125, 15);
        add(fontLabel);

        // Display current font and list of fonts available to choose from
        JPanel fontPanel = new JPanel();
        fontPanel.setBounds(10, 15, 150, 150);

        // Display current font
        currentFont = new JTextField(source.getTextArea().getFont().getFontName());
        currentFont.setPreferredSize(new Dimension(125, 25));
        currentFont.setEditable(false);
        currentFont.setBackground(Color.WHITE);
        fontPanel.add(currentFont);

        // Display list of fonts
        JPanel fontList = new JPanel();
        // BoxLayout arrange components either vertically or horizontally (make only one column)
        fontList.setLayout(new BoxLayout(fontList, BoxLayout.Y_AXIS));

        // Background colour
        fontList.setBackground(Color.WHITE);
        
        JScrollPane sPane = new JScrollPane(fontList);
        sPane.setPreferredSize(new Dimension(125, 110));

        // Retrieve all possible fonts
        GraphicsEnvironment graphicsE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = graphicsE.getAvailableFontFamilyNames();

        // Display each font in fontNames to fontLists as a JLabel
        for (String fName : fontNames){
            JLabel fSizeValue = new JLabel(fName);

            // Update current font field with selected font
            fSizeValue.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent e){
                    // Set font name when clicked
                    currentFont.setText(fName);
                }

                @Override
                public void mouseEntered(MouseEvent e){
                    // Highlight font when mouse hover over font name
                    fSizeValue.setOpaque(true);
                    fSizeValue.setBackground(Color.BLUE);
                    fSizeValue.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e){
                    // Remove highlight when mouse stops hovering over font name
                    fSizeValue.setBackground(null);  // Reset background Color
                    fSizeValue.setForeground(null);  // Reset font colour
                }
            });

            // Add to panel
            fontList.add(fSizeValue);
        }

        fontPanel.add(sPane);

        add(fontPanel);

   }


   // Method creates label for font style and displays current and available font styles
   private void addFontStyle(){
        JLabel fStyleLabel = new JLabel("Font Style");
        fStyleLabel.setBounds(180, 5, 125, 15);
        add(fStyleLabel);

        // Display current font style and all available font styles
        JPanel fStylePanel = new JPanel();
        fStylePanel.setBounds(180, 15, 150, 150);

        // Current font style
        int currentFStyle = source.getTextArea().getFont().getStyle();
        String currentFStyleText;

        switch (currentFStyle){
            
            case Font.PLAIN:
                currentFStyleText = "Plain";
                break;
            
            case Font.BOLD:
                currentFStyleText = "Bold";
                break;
            
            case Font.ITALIC:
                currentFStyleText = "Italic";
                break;
            
            default:  // Bold italic
                currentFStyleText = "Bold Italic";
                break;
        }

        currentFStyleField = new JTextField(currentFStyleText);
        currentFStyleField.setPreferredSize(new Dimension(150, 25));
        currentFStyleField.setEditable(false);
        currentFStyleField.setBackground(Color.WHITE);
        fStylePanel.add(currentFStyleField);

        // Display list of all available font styles
        JPanel fStyleList = new JPanel();
        fStyleList.setLayout(new BoxLayout(fStyleList, BoxLayout.Y_AXIS));
        fStyleList.setBackground(Color.WHITE);

        JScrollPane sPane = new JScrollPane(fStyleList);
        sPane.setPreferredSize(new Dimension(150, 110));

        // List of font Styles
        JLabel plainStyle = new JLabel("Plain");
        plainStyle.setFont(new Font("Dialog", Font.PLAIN, 12));
        plainStyle.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e){
                // Update current font style field
                currentFStyleField.setText(plainStyle.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e){
                // Highlight
                plainStyle.setOpaque(true);
                plainStyle.setBackground(Color.BLUE);
                plainStyle.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e){
                // Remove highlight
                plainStyle.setBackground(null); 
                plainStyle.setForeground(null);
            }
        });
        fStyleList.add(plainStyle);

        JLabel boldStyle = new JLabel("Bold");
        boldStyle.setFont(new Font("Dialog", Font.BOLD, 12));
        boldStyle.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e){
                // Update current font style field
                currentFStyleField.setText(boldStyle.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e){
                // Highlight
                boldStyle.setOpaque(true);
                boldStyle.setBackground(Color.BLUE);
                boldStyle.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e){
                // Remove highlight
                boldStyle.setBackground(null); 
                boldStyle.setForeground(null);
            }
        });
        fStyleList.add(boldStyle);

        JLabel italicStyle = new JLabel("Italic");
        italicStyle.setFont(new Font("Dialog", Font.ITALIC, 12));
        italicStyle.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e){
                // Update current font style field
                currentFStyleField.setText(italicStyle.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e){
                // Highlight
                italicStyle.setOpaque(true);
                italicStyle.setBackground(Color.BLUE);
                italicStyle.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e){
                // Remove highlight
                italicStyle.setBackground(null); 
                italicStyle.setForeground(null);
            }
        });
        fStyleList.add(italicStyle);

        JLabel boldItalicStyle = new JLabel("Bold Italic");
        // Single pipe '|' performs bitwise OR operation
        boldItalicStyle.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
        boldItalicStyle.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e){
                // Update current font style field
                currentFStyleField.setText(boldItalicStyle.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e){
                // Highlight
                boldItalicStyle.setOpaque(true);
                boldItalicStyle.setBackground(Color.BLUE);
                boldItalicStyle.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e){
                // Remove highlight
                boldItalicStyle.setBackground(null); 
                boldItalicStyle.setForeground(null);
            }
        });
        fStyleList.add(boldItalicStyle);

        fStylePanel.add(sPane);

        add(fStylePanel);
   }


   // Method creates label for font size and displays current and available font size
   private void addFontSize(){
        JLabel fSize = new JLabel("Font Size");
        fSize.setBounds(25, 175, 125, 15);
        add(fSize);

        // Display current font size and list of availabl font sizes
        JPanel fSizePanel = new JPanel();
        fSizePanel.setBounds(25, 185, 50, 150);

        current_fSize = new JTextField(Integer.toString(source.getTextArea().getFont().getSize()));
        current_fSize.setPreferredSize(new Dimension(50, 25));
        current_fSize.setEditable(true);
        current_fSize.setBackground(Color.WHITE);
        fSizePanel.add(current_fSize);

        // List of font sizes to choose from
        JPanel fSizeList = new JPanel();
        fSizeList.setLayout(new BoxLayout(fSizeList, BoxLayout.Y_AXIS));
        fSizeList.setBackground(Color.WHITE);

        // Available sizes from 8-72 (increases by 2)
        for (int i = 8; i <= 72; i += 2){
            JLabel fSizeValue = new JLabel(Integer.toString(i));
            fSizeValue.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent e){
                    // Set font name when clicked
                    current_fSize.setText(fSizeValue.getText());
                }

                @Override
                public void mouseEntered(MouseEvent e){
                    // Highlight font when mouse hover over font name
                    fSizeValue.setOpaque(true);
                    fSizeValue.setBackground(Color.BLUE);
                    fSizeValue.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e){
                    // Remove highlight when mouse stops hovering over font name
                    fSizeValue.setBackground(null);  // Reset background Color
                    fSizeValue.setForeground(null);  // Reset font colour
                }
            });
            fSizeList.add(fSizeValue);
        }

        JScrollPane sPane = new JScrollPane(fSizeList);
        sPane.setPreferredSize(new Dimension(50, 100));
        fSizePanel.add(sPane);

        add(fSizePanel);
   }


   // Method adds component for selecting font colour (from a spectrum) and displays current and availabel colours
   private void addFontColour(){
        // Display current colour
        currentColour = new JPanel();
        currentColour.setBounds(240, 190, 20, 20);
        currentColour.setBackground(source.getTextArea().getForeground());
        currentColour.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(currentColour);

        // Store default colour
        Color defaultColour = source.getTextArea().getForeground();

        JButton chooseColourBtn = new JButton("Choose Colour");
        chooseColourBtn.setBounds(180, 230, 150, 30);
        chooseColourBtn.addActionListener(e -> {
            // When clicked, dialog appears to select from a spectrum of colours
            Color selectedColour = JColorChooser.showDialog(null, "Colour Panel", Color.BLACK);
            // Update colour
            if (selectedColour != null){
                currentColour.setBackground(selectedColour);
            } else currentColour.setBackground(defaultColour);
        });
        add(chooseColourBtn);
   }

}

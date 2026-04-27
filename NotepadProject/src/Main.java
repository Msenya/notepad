import javax.swing.*;

public class Main {

    
    public static void main(String[] args) {
        // SwingUtilities is a collection of methods for swing
        // invokeLater() = static method that allows you to update swing components from any thread other than Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run(){
                try{
                    new NotepadGUI().setVisible(true);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
}

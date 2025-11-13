import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.SwingConstants;

import java.util.Arrays;

// repo https://github.com/hitoyaCute/Java-calculator-swift.git

// inherit the JFrame (Frame processor ig)
public class GUI extends JFrame {

    private JTextField displayField;

    private double operand1 = 0;
    private double operand2 = 0;

    private String operator = "";
    // a state where its parameter is all satisfied
    // 0 no paramer is provided
    // 1 A is provided
    // 2 A and opeator is provided
    // 3 all operator is provided
    // 4 continues mode but pressing number will clear everything
    private int collected = 0;
    private boolean is_dot_pressed = false;

    private static boolean debugg = false;

    public static Double round(double x, int precision) {
        double factor = Math.pow(10, precision); // Calculate the power of 10
        return Math.round(x * factor) / factor;
    }

    public GUI() {
        setTitle("Calculator");
        setSize(Conf.window_size);
        // setBackground(Color.BLACK);
        getContentPane().setBackground(new Color(84, 8, 99));
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // allows full control of positioning

        // Current number display
        displayField = new JTextField();
        displayField.setBounds(37, 24, 406, 54);
        displayField.setFont(Conf.button_font);
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setEditable(false);
        displayField.setText("0");
        displayField.setBackground(Conf.button_bg);
        displayField.setForeground(Conf.button_fg);
        add(displayField);
        
        
        ButtonClickHandler listener = new ButtonClickHandler();

        // Buttons (you can reposition them freely)
        // array struct
        //     037 141 245 349
        // 103 del  C   %   +
        // 184  7   8   9   -
        // 265  4   5   6   *
        // 346  1   2   3   /
        // 427  0   .   # = #

        // row 103
        addButton("Del", listener, 37,  103);
        addButton("C",   listener, 141, 103);
        addButton("%",   listener, 245, 103);
        addButton("+",   listener, 349, 103);

        addButton("7", listener, 37, 174);
        addButton("8", listener, 141, 174);
        addButton("9", listener, 245, 174);
        addButton("-", listener, 349, 174);

        addButton("4", listener, 37, 245);
        addButton("5", listener, 141, 245);
        addButton("6", listener, 245, 245);
        addButton("*", listener, 349, 245);

        addButton("1", listener, 37, 316);
        addButton("2", listener, 141, 316);
        addButton("3", listener, 245, 316);
        addButton("/", listener, 349, 316);

        addButton("0", listener, 37, 387);
        addButton(".", listener, 141, 387);
        addButton("=", listener, 245, 387, 198, 60);

        setVisible(true);
    }
    // Wrapper of the Helper method to add buttons easily
    private JButton addButton(String label, ActionListener listener, int x, int y) {
        return addButton(label, listener, x, y, 94, 60);
    }
    // Helper method to add buttons easily
    private JButton addButton(String label, ActionListener listener, int x, int y, int width, int height) {
        JButton button = new RoundedButton(label, 50);
        button.setFont(Conf.button_font);
        button.setBackground(Conf.button_bg); // gray for digits
        button.setForeground(Conf.button_fg);
        button.setBounds(x, y, width, height);
        
        button.addActionListener(listener);
        add(button);
        return button;
    }

    // Centralized event handler for all buttons
    private class ButtonClickHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            String display_text = displayField.getText();

            

            if (command.equals(".") && collected != 4  && !is_dot_pressed) {
                displayField.setText(display_text + ".");
                is_dot_pressed = true;
                if (collected == 2){
                    collected = 3;
                    displayField.setText("0.");
                }
                if (collected == 0)
                    collected = 1;
            } else if (command.equals("Del") && !display_text.isEmpty() && collected != 4 ) {
                String temp = display_text;
                if (temp.length() >= 2) {
                    displayField.setText(temp.substring(0, temp.length()-1));

                    if (temp.charAt(temp.length()-1) == '.') {
                        is_dot_pressed = false;
                    }
                } else {
                    displayField.setText("0");
                    is_dot_pressed = false;
                }


            } else if (command.matches("\\d")) { // if digit
                if (collected == 0) {
                    displayField.setText(command);
                    collected = 1;
                } else if (collected == 4) {
                    operand1 = operand2 = collected = 0;
                    operator = "";
                    is_dot_pressed = false;
                    displayField.setText("0");
                } else if(collected == 3){
                    displayField.setText(display_text + command);
                } else if (collected >= 2) {
                    displayField.setText(command);
                    collected = 3;
                    is_dot_pressed = false;
                } else if (collected >= 1) {
                    displayField.setText(display_text + command);
                }
            } else if ("+-*/%".contains(command)) { // operator
                if (command.equals("%") && collected >= 1) { // atleast A is provided
                    operand1 = operand1 / 100;
                    displayField.setText(Double.toString(round(operand1, 6)));
                    operator = "";
                    collected = 1;
                } else if (collected == 2) {
                    operator = command;
                    is_dot_pressed = false;
                } else if (collected == 3) {
                    operator = command;
                    operand1 = calculate(operand1, operand2, operator);
                    displayField.setText(Double.toString(operand1));
                    collected = 4;
                } else if (collected >= 1) {
                    operand1 = Double.parseDouble(display_text);
                    operator = command;
                    collected = 2;
                    is_dot_pressed = false;
                }
                
            } else if (command.equals("=") && collected >= 3) {
                if (collected == 3) {
                    operand2 = Double.parseDouble(display_text);
                }
                operand1 = calculate(operand1, operand2, operator);
                displayField.setText(Double.toString(operand1));
                collected = 4;
                is_dot_pressed = false;
            } else if (command.equals("C")) { // clear
                operand1 = operand2 = collected = 0;
                operator = "";
                displayField.setText("0");

                is_dot_pressed = false;
            }

            if (debugg)
                System.out.println("command: " + command + " values (A,B,O): (" + operand1 + ", " + operand2 + ", " + operator + ") collected: " + collected);
        }
    }
    private class Conf {
        public static Dimension    window_size   = new Dimension(491, 511);
        // public static double[] window_size_f = Arrays.stream(window_size).asDoubleStream().toArray();
        public static Color    button_bg     = new Color(146, 72, 122);
        public static Color    button_fg     = new Color(255, 211, 213);
        public static Font     button_font   = new Font("Tahoma", Font.BOLD, 20);
    };
    // Arithmetic logic
    private double calculate(double op1, double op2, String op) {
        switch (op) {
            case "+": return round(op1 + op2, 6);
            case "-": return round(op1 - op2, 6);
            case "*": return round(op1 * op2, 6);
            case "/": return op2 == 0 ? Double.NaN : round(op1 / op2, 6);
            default: return 0;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
        if (args.length >= 1) {
            debugg = true;
        }
    }
    public class RoundedButton extends JButton {
        private int arcRadius;
        private Shape shape;

        public RoundedButton(String label, int radius) {
            super(label);
            this.arcRadius = radius;
            setOpaque(false);
            setFocusPainted(false); // Remove the focus border
            setBorderPainted(false);
            setContentAreaFilled(false); // Do not fill the content area with the default square background
        }

        public int getArcRadius() {
            return arcRadius;
        }

        public void setArcRadius(int arcRadius) {
            this.arcRadius = arcRadius;
            repaint(); // Repaint the button when the radius changes
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Use Graphics2D for better control and anti-aliasing
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Determine the color based on the button's state (pressed, rollover, or normal)
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }

            // Fill the rounded rectangle
            g2.fillRoundRect(0, 0, getWidth() , getHeight() , arcRadius, arcRadius);
            
            // Call the superclass method to paint the text and icons
            super.paintComponent(g2);
            g2.dispose();
        }

        // Ensures that mouse clicks outside the rounded shape are ignored
        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arcRadius, arcRadius);
            }
            return shape.contains(x, y);
        }
    } 
}


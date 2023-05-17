import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class PortScannerGUI extends JFrame implements ActionListener {
    private JTextField hostField;
    private JTextField startPortField;
    private JTextField endPortField;
    private JTextArea resultArea;
    private JButton scanButton;
    private JButton stopButton;
    private Thread scanThread;

    public PortScannerGUI() {

        JLabel hostLabel = new JLabel("Host:");
        JLabel startPortLabel = new JLabel("Start Port:");
        JLabel endPortLabel = new JLabel("End Port:");
        hostField = new JTextField("localhost", 20);
        startPortField = new JTextField("1", 5);
        endPortField = new JTextField("65535", 5);
        scanButton = new JButton("Scan");
        scanButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);


        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(hostLabel);
        inputPanel.add(hostField);
        inputPanel.add(startPortLabel);
        inputPanel.add(startPortField);
        inputPanel.add(endPortLabel);
        inputPanel.add(endPortField);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(scanButton);
        buttonPanel.add(stopButton);
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
        getContentPane().add(resultPanel, BorderLayout.SOUTH);


        setTitle("Port Scanner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == scanButton) {
            
            scanButton.setEnabled(false);
            stopButton.setEnabled(true);

            // new thread tom port scan
            scanThread = new Thread(new Runnable() {
                public void run() {
                    // input values
                    String host = hostField.getText().trim();
                    int startPort = Integer.parseInt(startPortField.getText().trim());
                    int endPort = Integer.parseInt(endPortField.getText().trim());

                    // port scan
                    try {
                        InetAddress inetAddress = InetAddress.getByName(host);
                        resultArea.setText("Scanning ports on " + host + " (" + inetAddress.getHostAddress() + ")...\n");

                        for (int port = startPort; port <= endPort; port++) {
                            if (Thread.currentThread().isInterrupted()) {

                                resultArea.append("Scan stopped\n");
                                return;
                            }

                            try (Socket socket = new Socket()) {
                                socket.connect(new InetSocketAddress(inetAddress, port), 1000);
                                resultArea.append("Port " + port + " is open\n");
                            } catch (Exception ex) {
                                // Port open / closed
                            }
                        }

                        resultArea.append("Scan complete\n");
                    } catch (UnknownHostException ex) {
                        resultArea.setText("Unknown host: " + host);
                    }

                    scanButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }
            });

            scanThread.start();
        } else if (e.getSource() == stopButton) {

            scanThread.interrupt();


            stopButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PortScannerGUI();
            }
        });
    }
}



package Gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;
import MusicPlayer.mp3Player;

public class GUI {
    JFrame frame;

    int screenSizeX = 600; //px
    int screenSizeY = 500; //px

    int locationX = Toolkit.getDefaultToolkit().getScreenSize().width / 3;
    int locationY = Toolkit.getDefaultToolkit().getScreenSize().height / 3;

    mp3Player player = new mp3Player();

    int playingSongsIndex = 1;

    public GUI() {
        startTasks();
        createGui();
        changeGuiSettings();
        createAndSetLayout();
        createAndAddComponents();
        refreshFrame();
    }

    @SuppressWarnings("BusyWait")
    private void startTasks(){
        Thread one = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    System.out.println(ie.getMessage());
                }
                try {
                    showListOfUrls(player.getPlayList());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                try{
                    Thread.sleep(4000);
                }catch(InterruptedException ie){
                    System.out.println(ie.getMessage());
                }
                clear();
            }
        });
        one.start();

        Thread two = new Thread(() -> {
          if(currentTitle != null){
              currentTitle.setText(Arrays.toString(player.getPlayList().toArray()));
          }
        });
        two.start();

        Thread three = new Thread(()->{
            while(true){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ie){
                    System.out.println(ie.getMessage());
                }
                refreshFrame();
            }
        });
        three.start();

        Thread four = new Thread(() -> {
            if(playList != null) {
                int index = 0;
                int linecounter = 0;
                for (String line : playList.getText().split("\n")) {
                    index++;
                    linecounter++;
                    if (index == playingSongsIndex) {
                        Highlighter high = playList.getHighlighter();
                        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
                        String[] arr = new String[]{line};
                        int index1 = line.indexOf(arr[0] + ((linecounter - 1)*playList.getWidth()));
                        int index2 = line.indexOf(arr[arr.length - 1] + ((linecounter - 1)*playList.getWidth()));
                        try {
                            high.addHighlight(index1, index2, painter);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        four.start();
    }

    private void showListOfUrls(List<URL> list) throws URISyntaxException {
        for(URL url : list){
            System.out.println(url.toURI());
        }
    }

    public static void clear()
    {
        try
        {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ignored) {}
    }

    private void createGui(){
        frame = new JFrame();
    }

    private void changeGuiSettings() {
        frame.setVisible(true);

        frame.setSize(getGuiSize());
        frame.setResizable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocationRelativeTo(null);
        frame.setLocation(getLocation());

        frame.setTitle("mp3Player");

        frame.getContentPane().setBackground(Color.BLACK);

        setIcon();
    }

    private void setIcon() {
        frame.setIconImage(new ImageIcon("res\\mp3Player_icon.png").getImage());
    }

    private Dimension getGuiSize(){
        return new Dimension(screenSizeX, screenSizeY);
    }

    private Point getLocation(){
        return new Point(locationX, locationY);
    }

    GroupLayout gl;
    private void createAndSetLayout(){
        gl = new GroupLayout(frame.getContentPane());
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        frame.getContentPane().setLayout(gl);
    }

    //components: button_add_directory, button_play, button_pause, button_stop, button_skip_forward
    //components: button_skip_backward, button_set_shuffle, button_set_repeat
    //components:
    JButton button_add_directory;
    JButton button_play;
    JButton button_pause;
    JButton button_stop;
    JButton button_skip_forward;
    JButton button_skip_backward;
    JButton button_set_shuffle;
    JButton button_set_repeat;

    JLabel currentTitle;
    JTextArea playList;

    JButton exit;

    boolean shuffle = false;
    boolean repeat = false;

    File dir;

    private void addToTextArea(String filename){
        boolean found = false;
        for(String line : playList.getText().split("\n")){
            if (line.contains(filename)) {
                found = true;
                break;
            }
        }
        if(!found){
            playList.setText(playList.getText()+"\n"+filename);
        }
    }

    private void createAndAddComponents(){
        currentTitle = new JLabel();
        button_add_directory = new JButton("Add dir");
        button_play = new JButton("Play");
        button_pause = new JButton("Pause");
        button_stop = new JButton("Stop");
        button_skip_forward = new JButton(">>");
        button_skip_backward = new JButton("<<");
        button_set_shuffle = new JButton("Shuffle: " + shuffle);
        button_set_repeat = new JButton("Repeat: " + repeat);
        playList = new JTextArea(10, 50);
        exit = new JButton("Exit");

        exit.setForeground(Color.GREEN);
        exit.setBackground(Color.BLACK);
        exit.setFont(new Font("Courier", Font.BOLD, 20));
        exit.addActionListener((var e10)->{System.exit(0);});

        playList.setForeground(Color.GREEN);
        playList.setFont(new Font("Courier", Font.BOLD, 15));
        playList.setEditable(false);
        new JScrollPane(playList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        playList.setLineWrap(true);
        playList.setWrapStyleWord(true);

        currentTitle.setForeground(Color.GREEN);
        currentTitle.setBackground(Color.BLACK);
        currentTitle.setFont(new Font("Courier", Font.BOLD, 20));

        //playlist is a:List<URL>,we ll add all the files in the directory that have an .mp3 extension
        button_add_directory.setForeground(Color.GREEN);
        button_add_directory.setBackground(Color.BLACK);
        button_add_directory.setFont(new Font("Courier", Font.BOLD, 20));
        button_add_directory.addActionListener((var e9)->{
            JFileChooser chooser = new JFileChooser();
            int i = chooser.showOpenDialog(frame);
            if(i == JFileChooser.APPROVE_OPTION){
                dir = chooser.getCurrentDirectory();
            }
            for(File file : Objects.requireNonNull(dir.listFiles())){
                if(!file.isDirectory()){
                    if(file.getName().contains(".mp3")){
                        player.addToPlaylist(file);
                        addToTextArea(file.getName());
                    }
                }
            }
        });

        button_play.setForeground(Color.GREEN);
        button_play.setBackground(Color.BLACK);
        button_play.setFont(new Font("Courier", Font.BOLD, 20));
        button_play.addActionListener((var e3)-> player.play());

        button_pause.setForeground(Color.GREEN);
        button_pause.setBackground(Color.BLACK);
        button_pause.setFont(new Font("Courier", Font.BOLD, 20));
        button_pause.addActionListener((var e4)-> player.pause());

        button_stop.setForeground(Color.GREEN);
        button_stop.setBackground(Color.BLACK);
        button_stop.setFont(new Font("Courier", Font.BOLD, 20));
        button_stop.addActionListener((var e5)-> player.stop());

        button_skip_forward.setForeground(Color.GREEN);
        button_skip_forward.setBackground(Color.BLACK);
        button_skip_forward.setFont(new Font("Courier", Font.BOLD, 20));
        button_skip_forward.addActionListener((var e6)-> {
            player.skipForward();
            playingSongsIndex++;
        });

        button_skip_backward.setForeground(Color.GREEN);
        button_skip_backward.setBackground(Color.BLACK);
        button_skip_backward.setFont(new Font("Courier", Font.BOLD, 20));
        button_skip_backward.addActionListener((var e7)-> {
            player.skipBackward();
            playingSongsIndex--;
        });

        button_set_shuffle.setForeground(Color.RED);
        button_set_shuffle.setBackground(Color.BLACK);
        button_set_shuffle.setFont(new Font("Courier", Font.BOLD, 20));
        button_set_shuffle.addActionListener((var e1)->{
            shuffle = !shuffle;
            if(shuffle){
                button_set_shuffle.setForeground(Color.BLUE);
            }else{
                button_set_shuffle.setForeground(Color.RED);
            }
            button_set_shuffle.setText("Shuffle: " + shuffle);
            player.setShuffle(shuffle);
        });
        player.setShuffle(shuffle);

        button_set_repeat.setForeground(Color.RED);
        button_set_repeat.setBackground(Color.BLACK);
        button_set_repeat.setFont(new Font("Courier", Font.BOLD, 20));
        button_set_repeat.addActionListener((var e2)->{
            repeat = !repeat;
            if(repeat){
                button_set_repeat.setForeground(Color.BLUE);
            }else{
                button_set_repeat.setForeground(Color.RED);
            }
            button_set_repeat.setText("Repeat: " + repeat);
            player.setRepeat(repeat);
        });
        player.setRepeat(repeat);

        JLabel randomText = new JLabel("");

        JLabel randomText1 = new JLabel("List:");
        randomText1.setForeground(Color.GREEN);
        randomText1.setBackground(Color.BLACK);
        randomText1.setFont(new Font("Courier", Font.BOLD, 20));

        //layout manager
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(button_play).addComponent(button_skip_backward).addComponent(button_set_shuffle).addComponent(randomText).addComponent(randomText1))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(button_pause).addComponent(button_add_directory).addComponent(randomText).addComponent(randomText).addComponent(playList))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(button_stop).addComponent(button_skip_forward).addComponent(button_set_repeat).addComponent(randomText).addComponent(exit)));

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(button_play).addComponent(button_pause).addComponent(button_stop))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(button_skip_backward).addComponent(button_add_directory).addComponent(button_skip_forward))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(button_set_shuffle).addComponent(randomText).addComponent(button_set_repeat))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(randomText1).addComponent(randomText).addComponent(randomText))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(randomText1).addComponent(playList).addComponent(exit)));
    }

    private void refreshFrame(){
        frame.revalidate();
        frame.repaint();
    }
}


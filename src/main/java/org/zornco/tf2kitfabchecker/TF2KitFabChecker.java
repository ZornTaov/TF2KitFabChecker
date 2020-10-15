package org.zornco.tf2kitfabchecker;

import java.security.CodeSource;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.io.IOException;
import org.json.simple.parser.ParseException;

import java.awt.Color;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.File;
import java.awt.GridBagConstraints;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class TF2KitFabChecker extends JFrame
{
    private static final Pattern STEAM_PATTERN;
    protected static final Map<String, String> ROBOT_PARTS;
    protected static final Map<String, String> IMG_PATHS;
    private static final Map<String, Integer> PARTS_IN_INV;
    protected static String PATH;
    Preferences prefs;
    private JPanel collectionPanel;
    private JButton downloaderButton;
    private JCheckBox forcedUpdateBox;
    private JProgressBar jProgressBar1;
    private JTextField userText;
    
    public TF2KitFabChecker() {
        this.prefs = Preferences.userNodeForPackage(TF2KitFabChecker.class);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.downloaderButton);
    }
    
    private void initComponents() {
        this.downloaderButton = new JButton();
        this.userText = new JTextField();
        JLabel userLabel = new JLabel();
        JScrollPane jScrollPane2 = new JScrollPane();
        this.collectionPanel = new JPanel();
        JLabel jLabel1 = new JLabel();
        this.forcedUpdateBox = new JCheckBox();
        this.jProgressBar1 = new JProgressBar();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.downloaderButton.setText("Get Inventory");
        this.downloaderButton.addActionListener(TF2KitFabChecker.this::downloaderButtonActionPerformed);
        userLabel.setText("Copy the link to a steam profile and paste it here.");
        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.collectionPanel.setLayout(new GridLayout(0, 1));
        jScrollPane2.setViewportView(this.collectionPanel);
        this.forcedUpdateBox.setText("Force Update");
        this.jProgressBar1.setPreferredSize(new Dimension(146, 17));
        final GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout
                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(this.userText)
                                .addComponent(jScrollPane2, GroupLayout.Alignment.TRAILING)
                                .addGroup(layout
                                        .createSequentialGroup()
                                        .addGroup(layout
                                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(userLabel)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(this.downloaderButton)
                                                        .addPreferredGap(LayoutStyle
                                                                .ComponentPlacement.RELATED)
                                                        .addComponent(this.forcedUpdateBox)
                                                        .addGap(115, 115, 115)
                                                        .addComponent(jLabel1)))
                                        .addGap(0, 334, 32767))
                                .addComponent(this.jProgressBar1, -1, -1, 32767))
                        .addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(userLabel)
                        .addPreferredGap(LayoutStyle
                                .ComponentPlacement.RELATED)
                        .addComponent(this.userText, -2, -1, -2)
                        .addPreferredGap(LayoutStyle
                                .ComponentPlacement.RELATED)
                        .addGroup(layout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(this.downloaderButton)
                                .addComponent(jLabel1)
                                .addComponent(this.forcedUpdateBox))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, -1, 295, 32767)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(this.jProgressBar1, -2, -1, -2)
                        .addContainerGap()));
        this.userText.setText(this.prefs.get("lastUser", ""));
        this.pack();
    }

    private void downloaderButtonActionPerformed(final ActionEvent evt) {
        this.collectionPanel.removeAll();
        this.downloaderButton.setEnabled(false);
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.setMaximum(100);
        this.revalidate();
        this.repaint();
        final Thread hilo = new Thread(() -> {
            JSONParser parser = new JSONParser();
            GridBagConstraints gbc = new GridBagConstraints();
            String type;
            String profile;
            try {
                try {
                    Matcher m = TF2KitFabChecker.STEAM_PATTERN.matcher(this.userText.getText());
                    boolean success = m.find();
                    profile = (success ? m.group(2) : null);
                    if (profile == null) {
                        this.postErrorLabel("MALFORMED URL: profile ID not found.", gbc);
                    }
                    type = (success ? m.group(1) : null);
                    Label_0127_1: {
                        if (type != null) {
                            if (!type.equals("")) {
                                break Label_0127_1;
                            }
                        }
                        try {
                            if (profile != null) {
                                Long.parseLong(profile);
                            }
                            type = "profiles";
                        }
                        catch (Exception e) {
                            System.out.println(e.toString());
                            type = "id";
                        }
                    }
                }
                catch (Exception e2) {
                    this.postErrorLabel("MALFORMED URL " + e2, gbc);
                    return;
                }
                this.prefs.put("lastUser", this.userText.getText());
                File inventoryCache = new File(TF2KitFabChecker.PATH + "/" + type + "/" + profile + ".json");
                if (!inventoryCache.exists() || this.forcedUpdateBox.isSelected()) {
                    this.downloadFile(inventoryCache, "http://steamcommunity.com/" + type + "/" + profile + "/inventory/json/440/2");
                }
                File itemSchema = new File(TF2KitFabChecker.PATH + "/itemschema.json");
                if (!itemSchema.exists() || this.forcedUpdateBox.isSelected()) {
                    this.downloadFile(itemSchema, "http://api.steampowered.com/IEconItems_440/GetSchemaItems/v0001/?key=" + Steam.API_KEY);
                }
                this.forcedUpdateBox.setSelected(false);

                JSONObject inventoryCacheJobj = this.readFile(inventoryCache, parser);
                JSONObject itemSchemaJobj = this.readFile(itemSchema, parser);
                JSONObject result = (JSONObject) itemSchemaJobj.get("result");
                JSONArray items = (JSONArray) result.get("items");

                if ((boolean)inventoryCacheJobj.get("success") && (long)((JSONObject)itemSchemaJobj.get("result")).get("status") == 1) {
                    clearPartsInInv();
                    ArrayList<KitPanel> kits = new ArrayList<>();
                    ArrayList<KitPanel> kitsCanMake = new ArrayList<>();
                    JSONObject map = (JSONObject)inventoryCacheJobj.get("rgInventory");
                    for (Object o : map.keySet()) {
                        String next = o.toString();
                        JSONObject item = (JSONObject) map.get(next);
                        if (item.containsKey("classid") && TF2KitFabChecker.ROBOT_PARTS.containsKey(item.get("classid").toString())) {
                            int j = TF2KitFabChecker.PARTS_IN_INV.get(TF2KitFabChecker.ROBOT_PARTS.get(item.get("classid").toString())) + 1;
                            TF2KitFabChecker.PARTS_IN_INV.put(TF2KitFabChecker.ROBOT_PARTS.get(item.get("classid").toString()), j);
                        }
                    }
                    JSONObject map2 = (JSONObject)inventoryCacheJobj.get("rgDescriptions");
                    int kitCount = 0;
                    int count = 0;
                    this.jProgressBar1.setMaximum(map2.size());
                    this.jProgressBar1.setValue(count);
                    this.jProgressBar1.setStringPainted(true);
                    for (Object o : map2.keySet()) {
                        count++;
                        this.jProgressBar1.setValue(count);
                        this.revalidate();
                        this.repaint();
                        String next2 = o.toString();
                        JSONObject item2 = (JSONObject) map2.get(next2);
                        if (item2.get("market_hash_name").toString().contains("Fabricator")) {
                            KitPanel kit3 = new KitPanel();
                            kit3.nameLabel.setText(item2.get("market_hash_name").toString());
                            JSONArray array = (JSONArray) item2.get("descriptions");
                            for (Object value : array) {
                                JSONObject part = (JSONObject) value;
                                String val = part.get("value").toString();
                                if (val.contains(" x ") && !val.contains("Unique")) {
                                    String[] split = val.split(" x ");
                                    kit3.addPart(split[0], TF2KitFabChecker.PARTS_IN_INV.get(split[0]), Integer.parseInt(split[1]));
                                }
                            }
                            JSONObject found = this.containsName(Arrays.asList(items.toArray()), item2.get("market_hash_name").toString()
                                    .replace("Specialized Killstreak ", "")
                                    .replace("Professional Killstreak ", "")
                                    .replace(" Kit Fabricator", ""));
                            if (found != null) {
                                makeIcon(item2.get("market_hash_name").toString()
                                        .replace("Specialized Killstreak ", "")
                                        .replace("Professional Killstreak ", "")
                                        .replace(" Kit Fabricator", ""),
                                        found.get("image_url").toString());
                                kit3.setIcon(item2.get("market_hash_name").toString()
                                        .replace("Specialized Killstreak ", "")
                                        .replace("Professional Killstreak ", "")
                                        .replace(" Kit Fabricator", ""),
                                        (String) item2.get("market_hash_name").toString()
                                                .subSequence(0, item2.get("market_hash_name").toString().indexOf(" ")));
                            }
                            if (kit3.canMake > 0) {
                                kit3.iconPanel.setBackground(Color.GREEN);
                                kitsCanMake.add(kit3);
                            } else {
                                kits.add(kit3);
                            }
                        }
                    }
                    kits.sort(Comparator.comparing(kit -> kit.nameLabel.getText()));
                    kitsCanMake.sort(Comparator.comparing(kit -> kit.nameLabel.getText()));
                    int kitCount2 = this.addKits(kitsCanMake, gbc, kitCount);
                    int kitCount3 = this.addKits(kits, gbc, kitCount2);
                    if (kitCount3 == 0) {
                        this.postErrorLabel("No Kit Fabricators found!", gbc);
                    }
                    this.jProgressBar1.setValue(0);
                }
                else {
                    this.postErrorLabel("Error: " + inventoryCacheJobj.get("Error").toString(), gbc);
                }
                this.collectionPanel.revalidate();
                this.collectionPanel.repaint();
            }
            catch (ParseException pe) {
                System.out.println("position: " + pe.getPosition());
                System.out.println(pe.toString());
                this.postErrorLabel("Specified Profile can not be found!", gbc);
            }
            catch (IOException e3) {
                System.out.println(e3.toString());
            }
            this.downloaderButton.setEnabled(true);
            this.jProgressBar1.setStringPainted(false);
        });
        hilo.start();
    }
    
    private int addKits(final List<KitPanel> kitsCanMake, final GridBagConstraints gbc, int kitCount) {
        for (final KitPanel kit : kitsCanMake) {
            gbc.gridy = kitCount;
            ++kitCount;
            this.collectionPanel.add(kit, gbc);
        }
        return kitCount;
    }
    
    private JSONObject readFile(final File inventoryCache, final JSONParser parser) throws IOException, ParseException {
        JSONObject obj;
        try (final FileReader fr = new FileReader(inventoryCache)) {
            obj = (JSONObject)parser.parse(fr);
        }
        return obj;
    }
    
    private void downloadFile(final File file, final String u) throws IOException {
        final URL url = new URL(u);
        final URLConnection connection = url.openConnection();
        connection.connect();
        final long lengthOfFile = connection.getContentLengthLong();

        this.jProgressBar1.setStringPainted(true);
        this.jProgressBar1.setString(file.getName());
        try (final InputStream input = new BufferedInputStream(connection.getInputStream());
             final OutputStream output = new FileOutputStream(file)) {
            final byte[] data = new byte[1024];
            long total = 0L;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                this.jProgressBar1.setValue((int)(total * 100L / lengthOfFile));
                this.revalidate();
                this.repaint();
                output.write(data, 0, count);
            }
            output.flush();
        }
        this.jProgressBar1.setString(null);
        this.jProgressBar1.setStringPainted(false);
    }
    
    private void postErrorLabel(final String text, final GridBagConstraints gbc) {
        final JLabel label = new JLabel(text);
        gbc.gridy = 0;
        this.collectionPanel.add(label, gbc);
        this.collectionPanel.revalidate();
        this.collectionPanel.repaint();
    }
    
    public JSONObject containsName(List<Object> list, final String name) {
        return (JSONObject) list.stream().filter(o -> ((JSONObject)o).get("name").toString().contains(name)).findFirst().orElse(null);
    }
    
    private static void makeIcon(final String name, final String url) throws IOException {
        final File img = new File(TF2KitFabChecker.PATH + "/" + name + ".png");
        if (!img.exists()) {
            final BufferedImage image = ImageIO.read(new URL(url));
            ImageIO.write(image, "png", img);
        }
        TF2KitFabChecker.IMG_PATHS.put(name, TF2KitFabChecker.PATH + "/" + name + ".png");
    }
    
    private static void mkdir(final String path) {
        final File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    private static void clearPartsInInv() {
        for (String s : TF2KitFabChecker.ROBOT_PARTS.values()) {
            TF2KitFabChecker.PARTS_IN_INV.put(s, 0);
        }
    }
    
    public static void main(final String[] args) {
        EventQueue.invokeLater(() -> new TF2KitFabChecker().setVisible(true));
    }
    
    static {
        STEAM_PATTERN = Pattern.compile("(?:https?:\\/\\/steamcommunity\\.com\\/(?<type>id|profiles)\\/)?(?<name>\\w*)\\/?");
        ROBOT_PARTS = new HashMap<>();
        IMG_PATHS = new HashMap<>();
        PARTS_IN_INV = new HashMap<>();
        TF2KitFabChecker.PATH = "";
        try {
            final CodeSource codeSource = TF2KitFabChecker.class.getProtectionDomain().getCodeSource();
            final File jarFile = new File(codeSource.getLocation().toURI().getPath());
            mkdir(TF2KitFabChecker.PATH = jarFile.getParentFile().getPath() + "/cache");
            mkdir(TF2KitFabChecker.PATH + "/id");
            mkdir(TF2KitFabChecker.PATH + "/profiles");
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(TF2KitFabChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        TF2KitFabChecker.ROBOT_PARTS.put("237182228", "Battle-Worn Robot Taunt Processor");
        TF2KitFabChecker.ROBOT_PARTS.put("237182229", "Battle-Worn Robot Money Furnace");
        TF2KitFabChecker.ROBOT_PARTS.put("237182230", "Reinforced Robot Humor Suppression Pump");
        TF2KitFabChecker.ROBOT_PARTS.put("237182231", "Reinforced Robot Emotion Detector");
        TF2KitFabChecker.ROBOT_PARTS.put("237182586", "Reinforced Robot Bomb Stabilizer");
        TF2KitFabChecker.ROBOT_PARTS.put("237183779", "Battle-Worn Robot KB-808");
        TF2KitFabChecker.ROBOT_PARTS.put("237193064", "Pristine Robot Brainstorm Bulb");
        TF2KitFabChecker.ROBOT_PARTS.put("237193078", "Pristine Robot Currency Digester");
        clearPartsInInv();
        final Map<String, String> PARTS_TO_URL = new HashMap<>();
        PARTS_TO_URL.put("Battle-Worn Robot Taunt Processor", "http://media.steampowered.com/apps/440/icons/mvm_robits_06.38805807029f0b631031bcb6062fa2d1a89086ef.png");
        PARTS_TO_URL.put("Battle-Worn Robot Money Furnace", "http://media.steampowered.com/apps/440/icons/mvm_robits_08.c207bc933a28dd09e3361304b376b13c0ac6cc11.png");
        PARTS_TO_URL.put("Reinforced Robot Humor Suppression Pump", "http://media.steampowered.com/apps/440/icons/mvm_robits_04.61976ee61344e53961586c18f7a0a8cabd69ae38.png");
        PARTS_TO_URL.put("Reinforced Robot Emotion Detector", "http://media.steampowered.com/apps/440/icons/mvm_robits_03.1cd0667e5017dd62bbd1babc2177a969997d1fcb.png");
        PARTS_TO_URL.put("Reinforced Robot Bomb Stabilizer", "http://media.steampowered.com/apps/440/icons/mvm_robits_05.dc56d867621a3a22adcbe8feb6fe6500305e9176.png");
        PARTS_TO_URL.put("Battle-Worn Robot KB-808", "http://media.steampowered.com/apps/440/icons/mvm_robits_07.26e0e2b5131c0c0020bfbf20df549d902e43345c.png");
        PARTS_TO_URL.put("Pristine Robot Brainstorm Bulb", "http://media.steampowered.com/apps/440/icons/mvm_robits_02.98b0216697e870f6bc6963177331ca46217d806a.png");
        PARTS_TO_URL.put("Pristine Robot Currency Digester", "http://media.steampowered.com/apps/440/icons/mvm_robits_01.90fc479e691fd7765c9492fb4a6f181c274f9a81.png");
        try {
            for (final String next : PARTS_TO_URL.keySet()) {
                makeIcon(next, PARTS_TO_URL.get(next));
            }
            makeIcon("Specialized", "http://media.steampowered.com/apps/440/icons/professional_kit.e91092b5612133c80fd1c49634c89e60f375e9e6.png");
            makeIcon("Professional", "http://media.steampowered.com/apps/440/icons/professional_kit_rare.51ae1c95c9db7e9baee367285187eb0365868433.png");
        }
        catch (IOException ex2) {
            Logger.getLogger(TF2KitFabChecker.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
}

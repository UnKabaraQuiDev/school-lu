
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Persons {
    
    private Node root;
    private int size = 0;
    //private int levels = 0;
    
    private ArrayList<Person> alPersons = new ArrayList<>();
    
    public void addCEO(Person CEO) {
        if (root == null) {
            root = new Node(CEO);
            size++;
        }
        alPersons.add(CEO);
    }
    
    public void add(Person person, Person boss) {
        Node bossNode = findNode(root, boss);
        if (bossNode != null) {
            bossNode.addSubordinate(new Node(person));
            alPersons.add(person);
        }
    }
    
//    public Node getNode(Person person) {
//        return getNodeRecursive(root, person);
//    }
//    
//    public Node getNodeRecursive(Node node, Person person) {
//        if (node.getPerson() == person) return node;
//        if (node.getAlNext() == null) {
//            return null;
//        } else {
//            ArrayList<Node> alNodes = node.getAlNext();
//            for (int i = 0; i < alNodes.size(); i++) {
//                Node get = alNodes.get(i);
//                getNodeRecursive(get, person);
//            }
//        }
//        return null;
//    }
    
    private Node findNode(Node currentNode, Person target) {
        if (currentNode == null) return null;
        if (currentNode.getPerson().equals(target)) {
            return currentNode;
        }
        for (Node subordinate : currentNode.getSubordinates()) {
            Node found = findNode(subordinate, target);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
    public void remove(Person person) {
        
    }
    
//    public void fillAlPersons() {
//        alPersons.clear();
//        alPersons.add(root.getPerson());
//        fillAlPersonsRecursive(root.getAlNext());
//    }
//    
//    public void fillAlPersonsRecursive(ArrayList<Node> alNodes) {
//        for (int i = 0; i < alNodes.size(); i++) {
//            Node node = alNodes.get(i);
//            alPersons.add(node.getPerson());
//            if (node.getAlNext() != null) {
//                fillAlPersonsRecursive(node.getAlNext());
//            }
//        }
//    }
//
//    public Object[] toArray() {
//        fillAlPersons();
//        Collections.sort(alPersons);
//        return alPersons.toArray();
//    }
    
    public Object[] toArray() {
        ArrayList<Person> sortedList = new ArrayList<>(alPersons);
        Collections.sort(sortedList);
        return sortedList.toArray();
    }
    
    public ArrayList<ArrayList<Node>> getLevels() {
        ArrayList<ArrayList<Node>> levels = new ArrayList<>();
        if (root == null) return levels;
        
        ArrayList<Node> currentLevel = new ArrayList<>();
        currentLevel.add(root);
        
        while (!currentLevel.isEmpty()) {
            levels.add(new ArrayList<>(currentLevel));
            ArrayList<Node> nextLevel = new ArrayList<>();
            
            for (Node node : currentLevel) {
                nextLevel.addAll(node.getSubordinates());
            }
            currentLevel = nextLevel;
        }
        return levels;
    }
    
//    public void draw(Graphics g, int width, int height) {
//        g.setColor(Color.BLACK);
//        ArrayList<ArrayList<Node>> levels = getLevels();
//        if (levels.isEmpty()) return;
//        
//        int panelWidth = width;
//        int panelHeight = height;
//        int rectWidth = 120;
//        int rectHeight = 40;
//        int levelSpacing = 80;
//        int maxNodesPerLevel = 0;
//        
//        // Find maximum nodes in any level
//        for (ArrayList<Node> level : levels) {
//            maxNodesPerLevel = Math.max(maxNodesPerLevel, level.size());
//        }
//        
//        // Draw each level
//        for (int levelIndex = 0; levelIndex < levels.size(); levelIndex++) {
//            ArrayList<Node> currentLevel = levels.get(levelIndex);
//            int y = levelIndex * levelSpacing + 20;
//            
//            // Calculate spacing between nodes
//            int totalWidth = (currentLevel.size() - 1) * (rectWidth + 20);
//            int startX = (panelWidth - totalWidth) / 2;
//            
//            for (int i = 0; i < currentLevel.size(); i++) {
//                int x = startX + i * (rectWidth + 20);
//                Node node = currentLevel.get(i);
//                
//                // Draw rectangle
//                g.drawRect(x, y, rectWidth, rectHeight);
//                // Draw text
//                String text = node.getPerson().getSurname() + " " + node.getPerson().getFirstname() + " " + node.getPerson().getTitle();
//                g.drawString(text, x + 5, y + 15);
//            }
//            
//            // Draw connections to next level
//            if (levelIndex < levels.size() - 1) {
//                ArrayList<Node> nextLevel = levels.get(levelIndex + 1);
//                for (int i = 0; i < currentLevel.size(); i++) {
//                    Node parentNode = currentLevel.get(i);
//                    int parentX = startX + i * (rectWidth + 20) + rectWidth / 2;
//                    int parentY = y + rectHeight;
//                    
//                    // Find all children of this parent
//                    for (int j = 0; j < nextLevel.size(); j++) {
//                        Node childNode = nextLevel.get(j);
//                        // Check if child belongs to this parent
//                        if (isChildOf(parentNode, childNode)) {
//                            int childX = (panelWidth - (nextLevel.size() - 1) * (rectWidth + 20)) / 2 + 
//                                       j * (rectWidth + 20) + rectWidth / 2;
//                            int childY = (levelIndex + 1) * levelSpacing + 20;
//                            g.drawLine(parentX, parentY, childX, childY);
//                        }
//                    }
//                }
//            }
//        }
//    }
    
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        ArrayList<ArrayList<Node>> levels = getLevels();
        if (levels.isEmpty()) return;

        // Calculate dynamic sizes based on available space
        int margin = 20;
        int availableWidth = width - 2 * margin;
        int availableHeight = height - 2 * margin;

        // Find the level with the most nodes
        int maxNodesInLevel = 0;
        for (ArrayList<Node> level : levels) {
            maxNodesInLevel = Math.max(maxNodesInLevel, level.size());
        }

        // Calculate rectangle dimensions
        int rectWidth, rectHeight;
        if (maxNodesInLevel > 0) {
            // Leave some spacing between rectangles (20 pixels)
            rectWidth = Math.max(80, (availableWidth - (maxNodesInLevel - 1) * 20) / maxNodesInLevel);
            rectWidth = Math.min(rectWidth, 200); // Maximum width
        } else {
            rectWidth = 120;
        }

        // Height based on number of levels
        int levelSpacing = Math.max(60, availableHeight / Math.max(1, levels.size()));
        rectHeight = Math.min(40, levelSpacing - 20); // Leave space between levels

        // Draw each level
        for (int levelIndex = 0; levelIndex < levels.size(); levelIndex++) {
            ArrayList<Node> currentLevel = levels.get(levelIndex);
            if (currentLevel.isEmpty()) continue;

            int y = margin + levelIndex * levelSpacing;

            // Calculate total width needed for this level
            int levelTotalWidth = currentLevel.size() * rectWidth + (currentLevel.size() - 1) * 20;
            int startX = margin + (availableWidth - levelTotalWidth) / 2;

            // Draw rectangles for this level
            for (int i = 0; i < currentLevel.size(); i++) {
                int x = startX + i * (rectWidth + 20);
                Node node = currentLevel.get(i);

                // Draw rectangle
                g.drawRect(x, y, rectWidth, rectHeight);

                // Draw text (centered and truncated if too long)
                String fullName = node.getPerson().getFirstname() + " " + node.getPerson().getSurname();
                String title = node.getPerson().getTitle();

                // Truncate text if it doesn't fit
                FontMetrics fm = g.getFontMetrics();
                String displayFullName = fullName;
                String displayTitle = title;

                if (fm.stringWidth(fullName) > rectWidth - 10) {
                    displayFullName = fullName.substring(0, Math.min(fullName.length(), 10)) + "...";
                }
                if (fm.stringWidth(title) > rectWidth - 10) {
                    displayTitle = title.substring(0, Math.min(title.length(), 10)) + "...";
                }

                // Draw centered text
                int fullNameWidth = fm.stringWidth(displayFullName);
                int titleWidth = fm.stringWidth(displayTitle);
                g.drawString(displayFullName, x + (rectWidth - fullNameWidth) / 2, y + 15);
                g.drawString(displayTitle, x + (rectWidth - titleWidth) / 2, y + 30);
            }

            // Draw connections to next level
            if (levelIndex < levels.size() - 1) {
                ArrayList<Node> nextLevel = levels.get(levelIndex + 1);
                for (int i = 0; i < currentLevel.size(); i++) {
                    Node parentNode = currentLevel.get(i);
                    int parentX = startX + i * (rectWidth + 20) + rectWidth / 2;
                    int parentY = y + rectHeight;

                    // Find all children of this parent in the next level
                    for (int j = 0; j < nextLevel.size(); j++) {
                        Node childNode = nextLevel.get(j);
                        if (isChildOf(parentNode, childNode)) {
                            // Calculate child position
                            int nextLevelTotalWidth = nextLevel.size() * rectWidth + (nextLevel.size() - 1) * 20;
                            int nextStartX = margin + (availableWidth - nextLevelTotalWidth) / 2;
                            int childX = nextStartX + j * (rectWidth + 20) + rectWidth / 2;
                            int childY = margin + (levelIndex + 1) * levelSpacing;

                            g.drawLine(parentX, parentY, childX, childY);
                        }
                    }
                }
            }
        }
    }
    
    private boolean isChildOf(Node parent, Node child) {
        for (Node subordinate : parent.getSubordinates()) {
            if (subordinate.getPerson().equals(child.getPerson())) {
                return true;
            }
        }
        return false;
    }
}

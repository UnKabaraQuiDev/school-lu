
import java.awt.Color;
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Instruction {
    
    private String cmd;
    private int param = 0;
    private Instruction sub = null;
    private Instruction next;
    private int x = 0, y = 0;       // top left
    private int width;      // vu wou???
    private int height;
    
    public Instruction(String cmd, String param) {
        this.cmd = cmd;
        this.param = Integer.parseInt(param);
    }
    
    public void append(Instruction instruction) {
        this.cmd = next.getCmd();
        this.param = next.getParam();
        this.sub = next.getSub();
        this.next = instruction;
    }
    
    public void appendSub(Instruction instruction) {
        if(next == null) next = instruction;
        else {
            Instruction tmp = next;
            while (tmp.next != null) {
                tmp = tmp.getNext();
            }
            tmp.setNext(instruction);
        }
    }
    
    @Override
    public String toString() {
        if (param == 0) {
            return cmd;
        } else {
            return cmd + " " + param;
        }
    }
    
    public int draw(Graphics g, int x, int y) {
        String title = toString();
        width = g.getFontMetrics().stringWidth(title);
        height = 15;
        this.x = x;
        this.y = y;
        
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width+4+4, height+4);
        g.drawString(title, x + 4, y + 14);
        
        int top = y + height + 4;
        
        if (sub != null) {
            top = sub.draw(g, x + 20, top);
        }
        if (next != null) {
            top = next.draw(g, x, top);
        }
        return top;
    }

    public String getCmd() {
        return cmd;
    }
    
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getParam() {
        return param;
    }
    
    public void setParam(int param) {
        this.param = param;
    }

    public Instruction getSub() {
        return sub;
    }
    
    public void setSub(Instruction sub) {
        this.sub = sub;
    }

    public Instruction getNext() {
        return next;
    }
    
    public void setNext(Instruction next) {
        this.next = next;
    }
    
    public String toCode(int indent) {
        return "";
    }
}

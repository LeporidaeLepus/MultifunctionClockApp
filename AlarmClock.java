/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClockApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


/**
 *
 * @author HP
 */
public class AlarmClock extends JPanel implements Runnable{
    Thread alarmThread;
    Graphics g;
    int hour,min;   //时钟时间
    int ahour,amin; //闹钟时间
    int jhour,jmin; //闹铃响起时间   
    boolean isRun;  //判断闹钟是否开启   
    JButton start = new JButton("START");   //开启按钮
    JButton stop = new JButton("STOP");     //关闭按钮
    JButton remove = new JButton("REMOVE"); //移除按钮
    String musFile = "alarmbell.wav";
    //音乐文件输入流
    BufferedInputStream buffer;
    AudioStream as;
    //该闹钟在闹钟组中的序号，ClockFrame.jPanel2.jButton1ActionPerformed中调用
    int num = 0;
    TimeZone tz;    //时区
    String tzName;  //时区名称
    //必须实例化Clock类，否则NullPointerException
    Clock clock = new Clock();   //当前时钟
    
    //添加世界时后需要Clock类变量
    public AlarmClock(int h,int m,Clock ck){
        this(h,m);
        clock = ck;
    }
    
    public AlarmClock(int h,int m){
        super();
        setSize(300,30);
        alarmThread = new Thread(this,"Alarm");
        g = this.getGraphics();
        
        alarmThread.start();  
        repaint();
        //线程开始后，闹钟为默认开始状态
        isRun = true;        
        
        this.ahour = h;
        this.amin = m;
        this.jhour = h;
        this.jmin = m;  
        
        //在组件中添加start, stop, remove按钮，并设置格式
        //不能添加在paint()中，否则会占用资源
        this.add(start);
        this.add(stop);
        this.add(remove);
        start.setBackground(Color.green);
        start.setBounds(50,0,75,25);
        stop.setBounds(125,0, 75, 25);
        remove.setBounds(200,0,85,25);
        
        //stop按钮添加监听器
        StopAction stopAction = new StopAction();
        stop.addActionListener(stopAction);
        
        //start按钮添加监听器
        StartAction startAction = new StartAction();
        start.addActionListener(startAction);     
    }
    
    //无参构造器，使AlarmClock能添加到NetBeans的GUI组件列表中并使用
    public AlarmClock(){
        this(0,0); 
    }
    
    //判断当前时钟时间是否为闹铃响起时间，若是则返回true
    public boolean judge(){
        boolean j = false;
        //获取当前时钟的时区
        tzName = clock.tzName;
        tz = TimeZone.getTimeZone(tzName);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        //获取时钟时间
        //Calendar.HOUR_OF_DAY为24小时制，Calendar.HOUR为12小时制
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
//        System.out.println(tzName+hour+min);
        
        //判断当前是否为时钟时间
        j = (hour==this.jhour)&&(min==this.jmin);
        
        return j;
    }

    @Override
    public void run(){
        Thread myThread = Thread.currentThread();
        
        while(myThread == alarmThread){
            //闹钟打开
            if(isRun==true){
                if(judge()){
                     //播放闹铃
                     //此段代码要放在弹出提示框前，否则将会在提示框关闭后才被执行
                    try{
                        buffer = new BufferedInputStream(new FileInputStream(musFile));
                        AudioStream as = new AudioStream(buffer);
                        //开始播放
                        AudioPlayer.player.start(as);
                    }
                    catch(IOException e){
                        
                    }

                    //弹出提示框
                    Object[] options = {"I'm tired zzZ","Got it."};               
                    //choose判断按下的按钮，并决定下一步操作
                    int choose = JOptionPane.showOptionDialog(null,"It is "+this.ahour+":"+this.amin+" now!","Alarm Clock",
                        JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[1]);
                                   
                    //选择"Got it."关闭闹钟，线程休眠一分钟，进入下一天同一时间的判断
                    if(choose==1){
                        try{                            
                            AudioPlayer.player.stop(as);
                            Thread.sleep(60000);
                        }
                        catch(InterruptedException e){
                        
                        }
                    }
                    //选择"I'm tired zzZ"，开启贪睡模式，十分钟后再次提醒
                    else if(choose == 0){  
                        AudioPlayer.player.stop(as);
                        //通过修改jhour，jmin使闹钟十分钟后再次提醒
                        if(jmin>=50)    jhour = (jhour+1)%24;
                        jmin = (jmin+10)%60;
                        System.out.println(jhour+":"+jmin+"+"+judge()+"+"+isRun);
                    }
                }       
            }
            //闹钟关闭
            else if(isRun==false){
                
            }
            
            //每1s刷新一次
            //不可以放在if内
            try{
                Thread.sleep(1000);
                }
                catch(InterruptedException e){
                
                }

//            if(isRun==false){
//                try{
//                    //只能在run()中调用wait，且需要synchronized
//                    synchronized(this){
//                        wait();
//                    }
//                }
//                catch(InterruptedException e){
//                    
//                }
//            }        
        }
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setFont(new Font("",Font.BOLD,15));
        
        g.drawString(this.ahour+":"+this.amin, 10, 18);
    }
    
    //stop按钮监听器触发事件
    public class StopAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent evt){
            //标志位为false，闹钟关闭，start按钮无色，stop按钮红色
            isRun = false;         
            stop.setBackground(Color.red);
            start.setBackground(null);
        }
    }
    
    //start按钮监听器触发事件
    public class StartAction implements ActionListener{
       @Override
        public void actionPerformed(ActionEvent evt){
            //标志位为true，闹钟开启，start按钮绿色，stop按钮无色
            isRun = true;
            start.setBackground(Color.green);
            stop.setBackground(null);
        }
    }

}

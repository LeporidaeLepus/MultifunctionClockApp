import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author HP
 */
public class Countdown extends JPanel implements Runnable{
    Thread cdThread;
    Graphics g;
    int rhour,rmin,rsec;    // 剩余的倒计时时间
    int rest;   //剩余秒数
    boolean isRun;  //判断倒计时是否开启
    JButton start = new JButton("START");   //开启按钮
    JButton stop = new JButton("STOP");     //关闭按钮
    JButton delete = new JButton("DELETE"); //移除按钮
    String musFile = "cdbell.wav";
    //音乐文件输入流
    BufferedInputStream buffer;
    AudioStream as;
    //该倒计时在倒计时组中的序号，ClockFrame.jPanel3.jButton2ActionPerformed中调用
    int num = 0;
    
    public Countdown(){
        this(0,0,0);
    }
    
    public Countdown(int h,int m,int s){
        super();
        setSize(300,30);
        cdThread = new Thread(this,"Countdown");
        g = this.getGraphics();
        
        cdThread.start();  
        repaint();
        
        //线程开始后，若时间不为0，倒计时为默认开始状态
        if(h==0&&m==0&&s==0){
            isRun = false;
            stop.setBackground(Color.red);
        }else{
            isRun = true;
            start.setBackground(Color.green);
        }

        this.rhour = h;
        this.rmin = m;  
        this.rsec = s;
        this.rest = h*60*60+m*60+s;
        
        //在组件中添加start, stop, remove按钮，并设置格式
        //不能添加在paint()中，否则会占用资源
        this.add(start);
        this.add(stop);
        this.add(delete);    
        start.setBounds(50,0,75,25);
        stop.setBounds(125,0, 75, 25);
        delete.setBounds(200,0,85,25);
        
        //stop按钮添加监听器
        StopAction stopAction = new StopAction();
        stop.addActionListener(stopAction);
        
        //start按钮添加监听器
        StartAction startAction = new StartAction();
        start.addActionListener(startAction); 
    }
    
    @Override
    public void run(){
        Thread myThread = Thread.currentThread();
        while(myThread == cdThread){
            if(isRun){
                repaint();
                
                //不能用 rsec = (rsec-1)%60计算，(0-1)%60=-1
                rsec = rest%60;
                rmin = rest/60%60;
                rhour = rest/60/60;                          
                
                if(rest==0||rest<0){
                    isRun = false;
                    
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
                    
                    //改变按钮颜色
                    start.setBackground(null);
                    stop.setBackground(Color.red);
                    
                    //弹出提示框
                    JOptionPane.showConfirmDialog(null,"TIME OVER!","Countdown",
                            JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);   
                }
                
                //剩余秒数减1，必须放在末尾
                rest--;       
            }
            else if(isRun == false){
                
            }
            
            //每秒刷新一次
            try{
                cdThread.sleep(1000);
            }
            catch(InterruptedException e){
                
            }    
        }       
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setFont(new Font("",Font.BOLD,15));
        
        g.drawString(this.rhour+":"+this.rmin+":"+this.rsec, 0, 18);
//        System.out.println(this.rhour+":"+this.rmin+":"+this.rsec);
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
            //使定时器归零后无法再开启
            if(rest>0){
                //标志位为true，闹钟开启，start按钮绿色，stop按钮无色
                isRun = true;
                start.setBackground(Color.green);
                stop.setBackground(null);
            }
        }
    }
}

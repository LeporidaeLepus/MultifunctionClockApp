import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.JPanel;


public class Clock extends JPanel implements Runnable{
    Thread clockThread;
    Graphics g;
    int hour,min;   //当前时间变量
    int width,height;   //  组件宽高
    Font font = new Font("",Font.BOLD,30);      //设计字体
    TimeZone tz;    //时区
    String tzName;  //时区名称
    
    public Clock(){
        super();
        
//        setSize(100,100);
        clockThread = new Thread(this,"Clock");
        g = this.getGraphics();
        
        clockThread.start();     
        
        //默认时区上海
        tzName = "Asia/Shanghai";
/*        
        //获取所有可用id
        String[] ids = TimeZone.getAvailableIDs();
        for(String id:ids)
                System.out.println(id);
*/
    }
    
    @Override
    public void run(){
        Thread myThread = Thread.currentThread();
        
        while(myThread == clockThread){
            repaint();
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setFont(font);
        
        //获取组件宽高
        width = this.getWidth();
        height = this.getHeight();
        
        //用于获取指定时区时间
        tz = TimeZone.getTimeZone(tzName);
        Calendar cal = Calendar.getInstance();          
        cal.setTimeZone(tz);
        //setTimeZone后直接使用getTime()获取到的Date和原先是相同的，使用设置calTmp间接获得Date；
        Calendar calTmp = Calendar.getInstance();       
        calTmp.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        
        //设置时间格式化
        //hh十二小时制，HH二十四小时制
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String st = sdfTime.format(calTmp.getTime());
                        
        SimpleDateFormat sdfDate = new SimpleDateFormat("YYYY-MM-dd");        
        String sd = sdfDate.format(calTmp.getTime());

        SimpleDateFormat sdfWeek = new SimpleDateFormat("EEEE",Locale.ENGLISH);
        String sw = sdfWeek.format(calTmp.getTime());
        
        //使文字处于合适位置（水平居中）
        //计算string宽高
        int strWidth = g.getFontMetrics().stringWidth(st);
        int strHeight = g.getFontMetrics().getHeight();
        //drawString坐标(x,y)为基线起点
        g.drawString(st, (width-strWidth)/2, (height/2-strHeight-10));
        strWidth = g.getFontMetrics().stringWidth(sd);
        g.drawString(sd, (width-strWidth)/2, (height/2));
        strWidth = g.getFontMetrics().stringWidth(sw);
        g.drawString(sw, (width-strWidth)/2, (height/2+strHeight+10));
    }
    
    //从外部获取时区名称
    public void setTimeZoneName(String tzn){
        this.tzName = tzn;
    }
    
    //传出当前时钟的时区
    public String getTimeZoneName(){
        return this.tzName;
    }
}

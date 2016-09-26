package cn.arvix.vr.client;

import javax.swing.JTextArea;

public class UILog {
	private static UILog  uilog;
	private JTextArea textArea;
	java.text.DecimalFormat   df = new   java.text.DecimalFormat("#.##");   
	private long lastUploaded = 0;
	/**
	 * note: must call UILog(JTextArea textArea)
	 * @return
	 */
	public static UILog getInstance(){
		return uilog;
	}
	public static void init(JTextArea textArea){
		uilog = new UILog();
		uilog.textArea = textArea;
	}
	public UILog() {
	}
	public void log(String str){
		textArea.append("\n"+str);
		textArea.setCaretPosition(textArea.getText().length());
	}
	public void logProgress(long total,long uploaded,String fileName){
		if((uploaded-lastUploaded)>1024000){
			double totalD = total;
			textArea.append("\n上传进度("+fileName+")："+df.format(uploaded/1024.0/1024)+"M/"+
			df.format(totalD/1024/1024)+"M,进度："+df.format(uploaded/totalD*100)+"%");
			lastUploaded = uploaded;
			textArea.setCaretPosition(textArea.getText().length());
		}
	}
	

}

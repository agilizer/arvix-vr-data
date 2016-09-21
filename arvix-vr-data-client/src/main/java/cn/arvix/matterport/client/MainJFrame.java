package cn.arvix.matterport.client;

import java.awt.Toolkit;

import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author abel.lee
 */
public class MainJFrame extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 978854316777369381L;

	FetchService fetchService = new FetchServiceImpl();
	
	private static boolean workFlag = false;
	
	public static void setWorkFlag(boolean flag){
		workFlag = flag;
	}
    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
    	fetchService.setUploadDataService(new UploadDataServiceImpl());
        initComponents();
        UILog.init(logTextArea);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        serverUrlTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        apiKeyTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        workDirTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        sourceUrlTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("mainFame"); // NOI18N
        setTitle("Arvix matterport client");

        jLabel1.setText("服务器地址：");

        serverUrlTextField.setText("http://123.56.182.22:8080/api/v1/updateModelData");

        jLabel2.setText("ApiKey   :");

        apiKeyTextField.setText("c2654aa9-f432-49a7-9dd6-524518beeea1");

        jLabel3.setText("目标地址  ：");

        workDirTextField.setText("D:/arvix-client/temp/");

        jLabel4.setText("工作目录  :");

        jLabel5.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel5.setText("注意：本地运行时得开启vpn客户端。或者能正常访问amazon cdn服务。");

        submitButton.setText("开始工作");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        sourceUrlTextArea.setColumns(20);
        sourceUrlTextArea.setRows(5);
        sourceUrlTextArea.setText("https://my.matterport.com/show/?m=N6HuPecF32y");
        
        
        
        jScrollPane1.setViewportView(sourceUrlTextArea);


        jLabel6.setText("运行状态");

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(logTextArea);

        jScrollPane3.setViewportView(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(264, 264, 264)
                .addComponent(submitButton))
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 578, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(serverUrlTextField)
                            .addComponent(apiKeyTextField)
                            .addComponent(workDirTextField)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(serverUrlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(apiKeyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(workDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(submitButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>                        

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) { 
    	if(workFlag==false){
    		workFlag = true;
    		String serverUrl = serverUrlTextField.getText();
            String apiKey = apiKeyTextField.getText();
            String workDir = workDirTextField.getText();
            String urlText =  sourceUrlTextArea.getText();
            String[] fetchUrlArray = urlText.split("\n");
            for(String url:fetchUrlArray){
            	logTextArea.append("serverUrl:"+serverUrl+"\n"+"apiKey:"+apiKey+"\nworkDir:"+workDir+"\nfetchUrl:"+url+"\n\n");
            }
            fetchService.fetchData(serverUrl, apiKey, workDir, fetchUrlArray);
    	}else{
    		JOptionPane.showMessageDialog(null, "当前正在工作！不要重复点哦？ ","错误", JOptionPane.INFORMATION_MESSAGE); 
    	}
    }                                            

    /** 
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	MainJFrame frame = new MainJFrame();
            	int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;  
                int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;  
                frame.setLocation((screenWidth - 653)/2, (screenHeight-769)/2);  
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JTextField apiKeyTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea sourceUrlTextArea;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JTextField serverUrlTextField;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField workDirTextField;
    // End of variables declaration                   
}

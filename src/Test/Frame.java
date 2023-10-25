package Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Frame {
    public static void main(String args[]) {
    	
 

        JFrame frm = new JFrame("TEST");
        
        JComboBox<String> tableDropdown = new JComboBox<>();
        JComboBox<String> columnDropdown = new JComboBox<>(); // 두 번째 드롭다운 메뉴 추가
        JTextField salaryInput = new JTextField();
        JButton searchButton = new JButton("검색");
        
        tableDropdown.setBounds(130, 30, 100, 30);
        columnDropdown.setBounds(240, 30, 100, 30); // 두 번째 드롭다운 메뉴의 가로 위치 조정
        salaryInput.setBounds(240,30,100,30);
        
        JCheckBox[] checkBoxes = new JCheckBox[8];
        String[] checkBoxNames = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
        int[] checkedItems = {0,0,0,0,0,0,0,0};
        
        int initialX = 130;
        int initialY = 70;
        int width = 100;
        int height = 30;
        int gap = 10;  // 체크박스 사이의 간격

        // 8개의 체크박스 생성 및 설정
        for (int i = 0; i < checkBoxes.length; i++) {
        	final int index = i;
            checkBoxes[i] = new JCheckBox(checkBoxNames[i]);
            checkBoxes[i].setBounds(initialX + (width + gap) * i, initialY, width, height);
            frm.getContentPane().add(checkBoxes[i]);
            
            checkBoxes[i].addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        // 체크박스가 선택된 경우
                    	
                    	
                        checkedItems[index] = 1;
                    } else {
                        // 체크박스 선택이 해제된 경우
                        checkedItems[index] = 0;
                    }
                }
            });
            
            if(i == checkBoxes.length - 1) {
            	searchButton.setBounds(initialX + (width + gap) * (i + 1), initialY, width, height);
            	frm.getContentPane().add(searchButton);
            }
        }
        
        JLabel label = new JLabel("검색 범위:");
        label.setBounds(30, 30, 100, 30);
        frm.setSize(800, 800);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.getContentPane().setLayout(null);

        tableDropdown.addItem("부서");
        tableDropdown.addItem("성별");
        tableDropdown.addItem("연봉");
        
        try (Connection conn = Test.getConnection()){
        	String temp = (String) tableDropdown.getSelectedItem(); 
        	if(temp == "부서") {
        		temp = "Dname";
  	        	String columns = findDName(conn, temp);
	        	String[] columnsArray = columns.split("\n");
	        	
	        	for(String colName: columnsArray) {
	        		columnDropdown.addItem(colName.trim());
	        	}
        	}
        }catch(SQLException e) {
        	
        }
        
        tableDropdown.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	columnDropdown.removeAllItems();
            	String selectedItem = (String) e.getItem();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    try(Connection conn = Test.getConnection()){
                    	if(selectedItem == "부서") {
                    		salaryInput.setVisible(false);
                    		columnDropdown.setVisible(true);
	                    	String columns = findDName(conn, selectedItem);
	                    	String[] columnsArray = columns.split("\n");
	                    	for(String colName: columnsArray) {
	                    		columnDropdown.addItem(colName.trim());
	                    	}
                    	}else if(selectedItem == "성별") {
                    		salaryInput.setVisible(false);
                    		columnDropdown.setVisible(true);
                    		columnDropdown.addItem("F");
                    		columnDropdown.addItem("M");
                    	}else if(selectedItem == "연봉") {
                    		columnDropdown.setVisible(false);
                    		salaryInput.setVisible(true);
                    	}
                    }catch(SQLException a) {
                    	System.out.println("NON");
                    }

                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] cols = new String[8];
                int count=0;
                String[] queries = {
                    "SELECT Fname, Minit, Lname FROM EMPLOYEE",
                    "SELECT Ssn FROM EMPLOYEE",
                    "SELECT Bdate FROM EMPLOYEE",
                    "SELECT Address FROM EMPLOYEE",
                    "SELECT Sex FROM EMPLOYEE",
                    "SELECT Salary FROM EMPLOYEE",
                    "SELECT M.Fname FROM EMPLOYEE E, EMPLOYEE M WHERE E.Super_ssn = M.Ssn",
                    "SELECT Dname FROM EMPLOYEE JOIN DEPARTMENT ON Dno = Dnumber"
                };

                for(int i = 0; i<8; i++) {
                    if(checkedItems[i] == 1) {
                    	count++;
                        cols[i] = checkBoxNames[i];
                        
                    }else {
                        cols[i] = "";
                    }
                }
                String[] newCol = new String[count];
                int temp = 0;
                for(int i = 0; i<8; i++) {
                	
                	if(cols[i] == "") continue;
                	else {
                		newCol[temp] = cols[i];
                		temp++;
                	}
                }
                String name="";
              
                String ssn = "";
                String Bdate= "";
                String Address = "";
                String Sex = "";
                String Salary = "";
                String Dname = "";
                
                String Supervisor = "";
                
                int cnt = 0;
                for(int i = 0; i<newCol.length; i++) {
                	cnt++;
	                try(Connection conn = Test.getConnection()){
	                	
	                	if(newCol[i] == "Name") {
	                		name = findName(conn);
	                	}else if(newCol[i] == "Ssn") {
	                		ssn = findSsn(conn);
	                		System.out.println(ssn);
	                	}else if(newCol[i] == "Bdate") {
	                		Bdate = findBdate(conn);
	                	}else if(newCol[i] == "Address") {
	                		Address = findAddress(conn);
	                	}else if(newCol[i] == "Sex") {
	                		Sex = findSex(conn);
	                	}else if(newCol[i] == "Salary") {
	                		Salary = findSalary(conn);
	                	}else if(newCol[i] == "Department") {
	                		Dname = findDname2(conn);
	                	}else if(newCol[i] == "Supervisor") {
	                		Supervisor = findSupervisor(conn);
	                	}
	                }catch(SQLException a) {
	                }
                }
                

                
            }
        });
        
        
        frm.getContentPane().add(label); 
        frm.getContentPane().add(tableDropdown);
        frm.getContentPane().add(columnDropdown);
        frm.getContentPane().add(salaryInput);
        frm.setVisible(true);
    }
    
    public static String findDName(Connection conn, String colName) throws SQLException {
    	try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT DISTINCT Dname FROM DEPARTMENT";
            ResultSet resultSet = stmt.executeQuery(sql);
            StringBuilder resultText = new StringBuilder();
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                resultText.append(tableName).append("\n");
            }

            return resultText.toString();
        }
    }
    
    public static String findName(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT Fname, Minit, Lname FROM EMPLOYEE";
            ResultSet resultSet = stmt.executeQuery(sql);

            StringBuilder resultText = new StringBuilder();
            while (resultSet.next()) {
                String Fname = resultSet.getString("Fname");
                String Minit = resultSet.getString("Minit");
                String Lname = resultSet.getString("Lname");
                resultText.append(Fname).append(Minit).append(Lname).append("\n");
            }
            return resultText.toString();
        }
    }
    
    public static String findSsn(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Ssn FROM EMPLOYEE";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Ssn");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    
    public static String findBdate(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Bdate FROM EMPLOYEE";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Bdate");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    public static String findAddress(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Address FROM EMPLOYEE";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Address");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    
    public static String findSex(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Sex FROM EMPLOYEE";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Sex");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    
    public static String findSalary(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Salary FROM EMPLOYEE";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Salary");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }

    public static String findDname2(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Dname FROM Department JOIN Employee ON Dno = Dnumber";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Dname");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }

    public static String findSupervisor(Connection conn) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT M.Fname FROM EMPLOYEE E, EMPLOYEE M WHERE E.Super_ssn = M.Ssn";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Fname");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }

}
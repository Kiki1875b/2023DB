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
                
                ArrayList<String[]> data = new ArrayList<String[]>();
                ArrayList<String> columnNames = new ArrayList<String>();
                
                String[] nameArray = null;
                String[] ssnArray = null;
                String[] BdateArray = null;
                String[] AddressArray = null;
                String[] SexArray = null;
                String[] SalaryArray = null;
                String[] SuperArray = null;
                String[] DnameArray = null;
                
                if(!name.equals("")) {
                	nameArray = name.split("\n");
                	columnNames.add("Name");
                }
                if(!ssn.equals("")) {
                	ssnArray = ssn.split("\n");
                	columnNames.add("Ssn");
                }
                if(!Bdate.equals("")) {
                	BdateArray = Bdate.split("\n");
                	columnNames.add("Bdate");
                }
                if(!Address.equals("")) {
                	AddressArray = Address.split("\n");
                	columnNames.add("Address");
                }
                if(!Sex.equals("")) {
                	SexArray = Sex.split("\n");
                	columnNames.add("Sex");
                }
                if(!Salary.equals("")) {
                	SalaryArray = Salary.split("\n");
                	columnNames.add("Salary");
                }
                if(!Supervisor.equals("")) {
                	SuperArray = Supervisor.split("\n");
                	columnNames.add("Supervisor");
                }
                if(!Dname.equals("")) {
                	DnameArray = Dname.split("\n");
                	columnNames.add("Department");
                }
                
                

                int maxLength = 0;
                if (nameArray != null) maxLength = Math.max(maxLength, nameArray.length);
                if (ssnArray != null) maxLength = Math.max(maxLength, ssnArray.length);
                if (BdateArray != null) maxLength = Math.max(maxLength, BdateArray.length);
                if (AddressArray != null) maxLength = Math.max(maxLength, AddressArray.length);
                if (SexArray != null) maxLength = Math.max(maxLength, SexArray.length);
                if (SalaryArray != null) maxLength = Math.max(maxLength, SalaryArray.length);
                if (SuperArray != null) maxLength = Math.max(maxLength, SuperArray.length);
                if (DnameArray != null) maxLength = Math.max(maxLength, DnameArray.length);
                
                for (int i = 0; i < maxLength; i++) {
                    ArrayList<String> row = new ArrayList<String>();
                    if (nameArray != null && i < nameArray.length) row.add(nameArray[i]);
                    if (ssnArray != null && i < ssnArray.length) row.add(ssnArray[i]);
                    if (BdateArray != null && i < BdateArray.length) row.add(BdateArray[i]);
                    if (AddressArray != null && i < AddressArray.length) row.add(AddressArray[i]);
                    if (SexArray != null && i < SexArray.length) row.add(SexArray[i]);
                    if (SalaryArray != null && i < SalaryArray.length) row.add(SalaryArray[i]);
                    if (SuperArray != null && i < SuperArray.length) row.add(SuperArray[i]);
                    if (DnameArray != null && i < DnameArray.length) row.add(DnameArray[i]);
                  
                    
                    data.add(row.toArray(new String[0]));
                }
                
                String[][] rowData = data.toArray(new String[0][]);
                String[] columnNamesArray = columnNames.toArray(new String[0]);

                JTable table = new JTable(rowData, columnNamesArray);
                JScrollPane scrollpane = new JScrollPane(table);
                scrollpane.setBounds(30,150,1300,600);
                frm.getContentPane().add(scrollpane);
                frm.revalidate();
                
                
                
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
    		String sql = "SELECT CASE WHEN E.Super_ssn IS NOT NULL THEN S.Fname ELSE E.Fname END AS SupervisorName FROM EMPLOYEE E LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.Ssn";
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("SupervisorName");

    		    System.out.println(ssn + " ");

    			resultText.append(ssn).append("\n");
    		}
    	
    		return resultText.toString();
    	}
    }

}
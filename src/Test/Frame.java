package Test;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Frame {
	
	private static JScrollPane scrollpane;
	
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

        tableDropdown.addItem("전체");
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

                    }

                }
            }
        });
        
        final int CHECKBOX_COLUMN = 0;
        
        searchButton.addActionListener(new ActionListener() { //검색 버튼 로직
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	if(scrollpane != null) {
            		frm.getContentPane().remove(scrollpane);
            	}
            	
                String[] cols = new String[8];
                int count=0;
                
                String tableSelect = tableDropdown.getSelectedItem().toString(); 
                String columnSelect = null;
                
            
                if(tableSelect == "부서" || tableSelect == "성별") {
                	columnSelect = columnDropdown.getSelectedItem().toString();
                }
                if(tableSelect == "연봉") {
                	columnSelect = salaryInput.getText();
                	 
                }

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
                

               
                for(int i = 0; i<newCol.length; i++) {
	                try(Connection conn = Test.getConnection()){
	                	if(newCol[i] == "Name") {
	                		name = findName(conn, tableSelect, columnSelect);
	                	}else if(newCol[i] == "Ssn") {
	                		ssn = findSsn(conn,tableSelect, columnSelect);
	                		System.out.println(ssn);
	                	}else if(newCol[i] == "Bdate") {
	                		Bdate = findBdate(conn, tableSelect, columnSelect);
	                	}else if(newCol[i] == "Address") {
	                		Address = findAddress(conn, tableSelect, columnSelect);
	                	}else if(newCol[i] == "Sex") {
	                		Sex = findSex(conn, tableSelect, columnSelect);
	                	}else if(newCol[i] == "Salary") {
	                		Salary = findSalary(conn,tableSelect, columnSelect);
	                	}else if(newCol[i] == "Department") {
	                		Dname = findDname2(conn,tableSelect, columnSelect);
	                	}else if(newCol[i] == "Supervisor") {
	                		Supervisor = findSupervisor(conn,tableSelect, columnSelect);
	                	}
	                }catch(SQLException a) {
	                }
                }
                
                ArrayList<Object[]> data = new ArrayList<Object[]>();
                ArrayList<String> columnNames = new ArrayList<String>();
                
                String[] nameArray = null;
                String[] ssnArray = null;
                String[] BdateArray = null;
                String[] AddressArray = null;
                String[] SexArray = null;
                String[] SalaryArray = null;
                String[] SuperArray = null;
                String[] DnameArray = null;
                
                columnNames.add("Select");
                
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
                    ArrayList<Object> row = new ArrayList<Object>();
                    row.add(false);
                    if (nameArray != null && i < nameArray.length) row.add(nameArray[i]);
                    if (ssnArray != null && i < ssnArray.length) row.add(ssnArray[i]);
                    if (BdateArray != null && i < BdateArray.length) row.add(BdateArray[i]);
                    if (AddressArray != null && i < AddressArray.length) row.add(AddressArray[i]);
                    if (SexArray != null && i < SexArray.length) row.add(SexArray[i]);
                    if (SalaryArray != null && i < SalaryArray.length) row.add(SalaryArray[i]);
                    if (SuperArray != null && i < SuperArray.length) row.add(SuperArray[i]);
                    if (DnameArray != null && i < DnameArray.length && DnameArray[i] != null) row.add(DnameArray[i]);
                    
                    
                    data.add(row.toArray());
                }
                
                
                
                
              
                
                Object[][] rowData = data.toArray(new Object[0][]);
                String[] columnNamesArray = columnNames.toArray(new String[0]);
                
                DefaultTableModel model = new DefaultTableModel(rowData, columnNamesArray) {
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == CHECKBOX_COLUMN) {
                            // 체크박스를 추가하려는 열의 클래스를 Boolean.class로 지정
                            return Boolean.class;
                        }
                        return super.getColumnClass(columnIndex);
                    }
                };

                JTable table = new JTable(model);
                
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        if (value instanceof Boolean) {
                            JCheckBox checkBox = new JCheckBox();
                            checkBox.setSelected((Boolean) value);
                            checkBox.setHorizontalAlignment(JLabel.CENTER);
                            return checkBox;
                        }
                        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    }
                };

                
                table.getColumnModel().getColumn(CHECKBOX_COLUMN).setCellRenderer(renderer);
                table.getColumnModel().getColumn(CHECKBOX_COLUMN).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                
                
                scrollpane = new JScrollPane(table);
                scrollpane.setBounds(30,150,1300,300);
                frm.getContentPane().add(scrollpane);
                frm.revalidate();
            }
        });
        
        
        frm.getContentPane().add(label); 
        frm.getContentPane().add(tableDropdown);
        frm.getContentPane().add(columnDropdown);
        frm.getContentPane().add(salaryInput);
        frm.setVisible(true);
        
        JPanel panel = new JPanel();
        panel.setBounds(30,600,700,80);
        
        
        String[] sexOptions = {"F", "M"};
        JComboBox<String> sexDropdown = new JComboBox<>(sexOptions);
        
        
        Integer[] DnumberOptions = {1,4,5};
        JComboBox<Integer> DnumberDropdown = new JComboBox<>(DnumberOptions);
        
        
        JLabel L1 = new JLabel("Fname: ");
        JLabel L2 = new JLabel("Middle init: ");
        JLabel L3 = new JLabel("Last name: ");
        JLabel L4 = new JLabel("Ssn: ");
        JLabel L5 = new JLabel("Brithdate: ");
        JLabel L6 = new JLabel("Adress: ");
        JLabel L7 = new JLabel("Sex: ");
    	JLabel L8 = new JLabel("Salary: ");
    	JLabel L9 = new JLabel("Super_ssn: ");
    	JLabel L10 = new JLabel("Dno: ");
    	
    	JTextField setFname = new JTextField(15);
    	JTextField setMLame = new JTextField(1);
    	JTextField setLname = new JTextField(15);	
    	JTextField setSsn = new JTextField(9);
    	JTextField setBdate= new JTextField(15);
    	JTextField setAdress = new JTextField(30);
    	JTextField setSalary = new JTextField(10);
    	JTextField setSuper = new JTextField(9);
    	
    	JButton Update_Button = new JButton("정보 추가하기");
    	
    	
    	panel.setLayout(new GridLayout(0,10));
    	panel.add(L1);
    	panel.add(setFname);
    	panel.add(L2);
    	panel.add(setMLame);
    	panel.add(L3);
    	panel.add(setLname);
    	panel.add(L4);
    	panel.add(setSsn);
    	panel.add(L5);
    	panel.add(setBdate);
    	panel.add(L6);
    	panel.add(setAdress);
    	panel.add(L7);
    	panel.add(sexDropdown);
    	panel.add(L8);
    	panel.add(setSalary);
    	panel.add(L9);
    	panel.add(setSuper);
    	panel.add(L10);
    	panel.add(DnumberDropdown);
    	panel.add(Update_Button);
    
    	frm.add(panel);
    	
    	 
     // 추가 버튼
     Update_Button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
        	 String fname = setFname.getText();
        	 String minit = setMLame.getText();
        	 String lname = setLname.getText();
        	 String ssn = setSsn.getText();
        	 String bdate = setBdate.getText();
        	 String address = setAdress.getText();
        	 String sex = (String)sexDropdown.getSelectedItem();
        	 double salary = Double.parseDouble(setSalary.getText());
        	 int superSsn = Integer.parseInt(setSuper.getText());
        	 int dno = (Integer)(DnumberDropdown.getSelectedItem());

        	 try (Connection conn = Test.getConnection()) {
        		    String sql = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        		    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        		        pstmt.setString(1, fname);
        		        pstmt.setString(2, minit);
        		        pstmt.setString(3, lname);
        		        pstmt.setString(4, ssn);
        		        pstmt.setString(5, bdate);
        		        pstmt.setString(6, address);
        		        pstmt.setString(7, sex);
        		        pstmt.setDouble(8, salary);
        		        pstmt.setInt(9, superSsn);
        		        pstmt.setInt(10, dno);
        		        pstmt.executeUpdate();
        		        System.out.println("데이터가 성공적으로 삽입되었습니다.");
        		        showMessageDialog(null, "데이터가 성공적으로 삽입되었습니다");
        		        setFname.setText("");
        		        setMLame.setText("");
        		        setLname.setText("");
        		        setSsn.setText("");
        		        setBdate.setText("");
        		        setAdress.setText("");
        		        setSalary.setText("");
        		        setSuper.setText("");
        		        
        		    }
        		} catch (SQLException s) {
        		    s.printStackTrace();
        		    System.err.println("데이터 삽입 중 오류가 발생했습니다.");
        		}

         }
     });
 
     // 프레임 표시
     panel.setVisible(true);
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
    
    public static String findName(Connection conn, String tName, String cName) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT Fname, Minit, Lname FROM EMPLOYEE";
            if(tName == "부서" && cName != null) {
            	sql += ", DEPARTMENT WHERE Dno = Dnumber AND Dname = '" +cName+ "'";
            }else if(tName == "성별" && cName != null) {
            	sql += " WHERE SEX = '" + cName + "'"; 
            }else if(tName == "연봉") {
            	sql += " WHERE Salary >= " + cName;
            }
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
    
    public static String findSsn(Connection conn, String tName, String cName) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Ssn FROM EMPLOYEE";
    		if(tName == "부서" && cName != null) {
    			sql += ", DEPARTMENT WHERE Dno = Dnumber AND Dname = '" + cName + "'";
    		}else if(tName == "성별" && cName != null) {
    			sql += " WHERE SEX ='" + cName +"'";
    		}else if(tName == "연봉") {
    			sql += " WHERE Salary >= " + cName;
    		}
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Ssn");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    
    public static String findBdate(Connection conn, String tName, String cName) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Bdate FROM EMPLOYEE";
    		if(tName == "부서" && cName != null) {
    			sql += ", DEPARTMENT WHERE Dno = Dnumber AND Dname = '" + cName + "'";
    		}else if(tName == "성별" && cName != null) {
    			sql += " WHERE SEX ='" + cName +"'";
    		}else if(tName == "연봉") {
    			sql += " WHERE Salary >= " + cName;
    		}
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Bdate");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    public static String findAddress(Connection conn, String tName, String cName) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Address FROM EMPLOYEE";
    		if(tName == "부서" && cName != null) {
    			sql += ", DEPARTMENT WHERE Dno = Dnumber AND Dname = '" + cName + "'";
    		}else if(tName == "성별" && cName != null) {
    			sql += " WHERE SEX ='" + cName +"'";
    		}else if(tName == "연봉") {
    			sql += " WHERE Salary >= " + cName;
    		}
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Address");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    
    public static String findSex(Connection conn, String tName, String cName) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Sex FROM EMPLOYEE";
    		if(tName == "부서" && cName != null) {
    			sql += ", DEPARTMENT WHERE Dno = Dnumber AND Dname = '" + cName + "'";
    		}else if(tName == "성별" && cName != null) {
    			sql += " WHERE SEX ='" + cName +"'";
    		}else if(tName == "연봉") {
    			sql += " WHERE Salary >= " + cName;
    		}
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Sex");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }
    
    public static String findSalary(Connection conn, String tName, String cName) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		String sql = "SELECT Salary FROM EMPLOYEE";
    		if(tName == "부서" && cName != null) {
    			sql += ", DEPARTMENT WHERE Dno = Dnumber AND Dname = '" + cName + "'";
    		}else if(tName == "성별" && cName != null) {
    			sql += " WHERE SEX ='" + cName +"'";
    		}else if(tName == "연봉") {
    			sql += " WHERE Salary >= " + cName;
    		}
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Salary");
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }

    public static String findDname2(Connection conn, String tName, String Dname) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		
    		String sql = "SELECT Dname FROM Department JOIN Employee ON Dno = Dnumber";
    		System.out.println(Dname);
    		if(tName == "부서" && Dname != null) {
    			sql += " WHERE Dname = '" + Dname + "'";
    		}else if(tName == "성별" && Dname != null) {
    			sql += " WHERE Sex = '" + Dname + "'";
    		}else if(tName == "연봉") {
    			sql += " WHERE Salary >= " + Dname;
    		}
    		
    		System.out.println(sql);
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			String ssn = resultSet.getString("Dname");
    		    
    			resultText.append(ssn).append("\n");
    		}
    		return resultText.toString();
    	}
    }

    public static String findSupervisor(Connection conn, String tName, String cName) throws SQLException{
    	try(Statement stmt = conn.createStatement()){
    		//String sql = "SELECT CASE WHEN E.Super_ssn IS NOT NULL THEN S.Fname ELSE E.Sex END AS SupervisorName FROM EMPLOYEE E LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.Ssn";
    		String sql = "SELECT Super_ssn FROM EMPLOYEE;";
    		if(tName == "부서" && cName != null) {
    			sql +=  " WHERE EXISTS(SELECT 1 FROM DEPARTMENT WHERE E.Dno = DEPARTMENT.Dnumber AND DEPARTMENT.Dname = '" + cName + "')";
    		}else if (tName == "성별" && cName != null) {
    			sql += " WHERE E.Sex = '" + cName + "'";
    		}else if (tName == "연봉") {
    			sql += " WHERE E.Salary >= " + cName;
    		}
    		
    		System.out.println(sql);
    		
    		ResultSet resultSet = stmt.executeQuery(sql);
    		
    		StringBuilder resultText = new StringBuilder();
    		while(resultSet.next()) {
    			//String ssn = resultSet.getString("SupervisorName");
    			String ssn = resultSet.getString("Super_ssn");
    		    System.out.println(ssn + " ");
    			resultText.append(ssn).append("\n");
    		}
    	
    		return resultText.toString();
    	}
    }

}
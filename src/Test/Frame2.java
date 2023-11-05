package Test;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Frame2 {
	
	private static JScrollPane scrollpane;
	static boolean check = false;
	static JLabel label2 = new JLabel("선택한 직원: ");
    static JLabel label3 = new JLabel("직원 수: ");
    static Vector<String> selected = new Vector<String>();
    static Vector<String> selectedSsn = new Vector<String>();
    static int selectedCount = 0;
	
    public static void main(String args[]) {
    	
 

        JFrame frm = new JFrame("TEST");
        
        JComboBox<String> tableDropdown = new JComboBox<>();
        JComboBox<String> columnDropdown = new JComboBox<>(); // 두 번째 드롭다운 메뉴 추가
        JTextField salaryInput = new JTextField();
        JButton searchButton = new JButton("검색");
        JButton deleteButton = new JButton("삭제");
        
        
        
        tableDropdown.setBounds(130, 30, 100, 30);
        columnDropdown.setBounds(240, 30, 100, 30); // 두 번째 드롭다운 메뉴의 가로 위치 조정
        salaryInput.setBounds(240,30,100,30);
        
        JCheckBox[] checkBoxes = new JCheckBox[8];
        String[] checkBoxNames = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
        String[] checkBoxNames2 = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Super_ssn AS Supervisor", "Dname AS Department"};
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
        label2.setBounds(30, 500,1000, 30);
        label3.setBounds(30,530,100,30);
        deleteButton.setBounds(1000, 530, 100, 50);
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
        
        
        
        deleteButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		if(selectedSsn.size() == 0) {
        			showMessageDialog(null, "ssn을 선택하셔야 합니다.");
        			return;
        		}
        		String delete = "";
        		for(int i = 0; i<selectedSsn.size(); i++) {
        			if(i != selectedSsn.size() -1) {
        				delete += "'"+selectedSsn.get(i)+"'" + ",";
        			}else {
        				delete +="'"+selectedSsn.get(i)+"'";
        			}	
        		}
        		String temp = String.format("DELETE FROM EMPLOYEE WHERE Ssn IN (%s)",delete);
        		
        		System.out.println(temp);
        		
        		try(Connection conn = Test.getConnection()){
        			delete(conn, temp);
        			frm.getContentPane().remove(scrollpane);
        			frm.revalidate();
        			frm.repaint();
        		}catch(SQLException a) {
        			
        		}
        	}
        });
        
        searchButton.addActionListener(new ActionListener() { //검색 버튼 로직
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(scrollpane != null) {
            		frm.getContentPane().remove(scrollpane);
            	}
            	
            	
            	String select = "SELECT CONCAT(Fname,' ', Minit,' ', Lname) AS Name";
                String from = " FROM Department JOIN Employee ON Dno = Dnumber";
                String where = " WHERE ";
                String tableSelect = tableDropdown.getSelectedItem().toString(); 
                String columnSelect = null;
                
            
                if(tableSelect == "부서" ) {
                	columnSelect = columnDropdown.getSelectedItem().toString();
                	where += String.format("Dname = '%s'", columnSelect);
                }else if(tableSelect == "성별") {
                	columnSelect = columnDropdown.getSelectedItem().toString();
                	where += String.format("Sex = '%s'", columnSelect);
                }
                if(tableSelect == "연봉") {
                	columnSelect = salaryInput.getText(); 
                	where += String.format("Salary >= '%s'",columnSelect);
                }
                
                if(checkedItems[0] == 1) {
                	check = true;
                	
                }else {
                	check = false;
                }
                
                if(columnSelect == null) {
                	where = "";
                }
                
                
                
                for(int i = 1; i<checkedItems.length; i++) {
                		
                	if(checkedItems[i] == 1) {
                		
                		select += " , ";
                		
                		select += checkBoxNames2[i];
                	}
                }
                
            	try(Connection conn = Test.getConnection()){
            		search(conn, select, from, where);
            	}catch(SQLException a){
            		
            	}
            	
            	
            	frm.getContentPane().add(scrollpane);
                frm.revalidate();
            }
        });
        
        
        
        
        frm.getContentPane().add(label); 
        frm.getContentPane().add(label2);
        frm.getContentPane().add(label3);
        frm.getContentPane().add(deleteButton);
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
    public static void delete(Connection conn, String del) throws SQLException {
    	try (Statement stmt = conn.createStatement()){
    		System.out.println(del);
    		int num = stmt.executeUpdate(del);
    		
    		showMessageDialog(null, num + "개의 데이터가 성공적으로 삭제되었습니다");
    		
    	}catch (Exception e) {
    		
    	}
    }
    
    public static void search(Connection conn, String sel, String fro, String whe) throws SQLException {
    	try (Statement stmt = conn.createStatement()) {
    		final int CHECKBOX_COLUMN = 0;
            String sql = sel + " " + fro + " " + whe;
            ResultSet resultSet = stmt.executeQuery(sql);
            
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            
            StringBuilder resultText = new StringBuilder();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Select");
            for (int i = 1; i <= columnCount; i++) {
            	
                columnNames.add(metaData.getColumnLabel(i));
                
            }
            
            if(check == false) {
            	columnNames.remove(1);
            }
            
            // Create a 2D vector to hold the data
            Vector<Vector<Object>> data = new Vector<>();
            
            // Populate the data from the ResultSet
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(false);
                for (int i = 1; i <= columnCount; i++) {
                	if(check == false && i==1) {
                		continue;
                	}
                    row.add(resultSet.getObject(i));
                }
                data.add(row);
            }

            // Create a DefaultTableModel with the data and column names
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames){
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == CHECKBOX_COLUMN) {
                        // 체크박스를 추가하려는 열의 클래스를 Boolean.class로 지정
                        return Boolean.class;
                    }
                    return super.getColumnClass(columnIndex);
                }
            };;
            
            // Create a JTable with the DefaultTableModel
            JTable table = new JTable(tableModel);
            
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
            
            table.getModel().addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    
                    if (column == CHECKBOX_COLUMN) {
                        Boolean checked = (Boolean) table.getValueAt(row, column);
                        if (checked) {

                            Vector<Object> rowDataForSelectedRow = data.get(row);
                            
                            Vector<String> columnNamesForSelectedRow = new Vector<>();
                            for (int i = 0; i < table.getColumnCount(); i++) {
                                columnNamesForSelectedRow.add(table.getColumnName(i));
                            }
                            
                            System.out.println(columnNamesForSelectedRow.get(1).equals("Name"));
                            
                            if(columnNamesForSelectedRow.get(1).equals("Name")) {
                            	selectedCount++;
                            	selected.add(rowDataForSelectedRow.get(1).toString());
                            }
                            int idx = columnNamesForSelectedRow.indexOf("Ssn");
                            if(idx != -1) {

                            	selectedSsn.add(rowDataForSelectedRow.get(idx).toString());
                            }


                        }else {
                        	Vector<Object> rowDataForSelectedRow = data.get(row);


                            Vector<String> columnNamesForSelectedRow = new Vector<>();
                            for (int i = 0; i < table.getColumnCount(); i++) {
                                columnNamesForSelectedRow.add(table.getColumnName(i));
                            }
                            
                            System.out.println(columnNamesForSelectedRow.get(1).equals("Name"));
                            if(columnNamesForSelectedRow.get(1).equals("Name")) {
                            	selectedCount--;
                            	selected.remove(rowDataForSelectedRow.get(1).toString());
                            }
                            int idx = columnNamesForSelectedRow.indexOf("Ssn");
                            if(idx != -1) {
                            	selectedSsn.remove(rowDataForSelectedRow.get(idx).toString());
                            }

                        }
                        String temp = "선택한 직원: ";
                        String temp2 = "직원 수: " + Integer.toString(selectedCount);
                        for(int i = 0; i<selected.size(); i++) {
                        	temp += selected.get(i) + " ";
                        }
                        
                        label2.setText(temp);
                        label3.setText(temp2);
                    }
                }
            });
            
            
            table.getColumnModel().getColumn(CHECKBOX_COLUMN).setCellRenderer(renderer);
            table.getColumnModel().getColumn(CHECKBOX_COLUMN).setCellEditor(new DefaultCellEditor(new JCheckBox()));
            
            scrollpane = new JScrollPane(table);
            scrollpane.setBounds(30,150,1300,300);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
    
    


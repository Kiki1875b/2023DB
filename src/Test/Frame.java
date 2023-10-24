package Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Frame {
    public static void main(String args[]) {

        JFrame frm = new JFrame("TEST");
        
        JComboBox<String> tableDropdown = new JComboBox<>();
        JComboBox<String> columnDropdown = new JComboBox<>(); // 두 번째 드롭다운 메뉴 추가
        tableDropdown.setBounds(130, 30, 100, 30);
        columnDropdown.setBounds(240, 30, 100, 30); // 두 번째 드롭다운 메뉴의 가로 위치 조정
        
        JLabel label = new JLabel("검색 범위:");
        label.setBounds(30, 30, 100, 30);
        frm.setSize(800, 800);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.getContentPane().setLayout(null);

        // Test 클래스의 runSQL 메서드를 호출하여 테이블 이름 가져오기
        try (Connection conn = Test.getConnection()) {
            String tableNames = Test.runSQL(conn); // 테이블 이름을 가져옴
            String[] tableNameArray = tableNames.split("\n");
            for (String tableName : tableNameArray) {
                tableDropdown.addItem(tableName.trim());
            }
            
        } catch (SQLException e) {
			
		}
        
        try (Connection conn = Test.getConnection()){
        	String temp = (String) tableDropdown.getSelectedItem();
        	String columns = findColName(conn, temp);
        	String[] columnsArray = columns.split("\n");
        	for(String colName: columnsArray) {
        		columnDropdown.addItem(colName.trim());
        	}
        }catch(SQLException e) {
        	
        }
        
        tableDropdown.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
            	columnDropdown.removeAllItems();
            	String selectedItem = (String) e.getItem();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    try(Connection conn = Test.getConnection()){
                    	String columns = findColName(conn, selectedItem);
                    	String[] columnsArray = columns.split("\n");
                    	for(String colName: columnsArray) {
                    		columnDropdown.addItem(colName.trim());
                    	}
                    }catch(SQLException a) {
                    	System.out.println("NON");
                    }

                }
            }
        });
        
        
        frm.getContentPane().add(label); 
        frm.getContentPane().add(tableDropdown);
        frm.getContentPane().add(columnDropdown);
        frm.setVisible(true);
    }
    
    public static String findColName(Connection conn, String colName) throws SQLException {
    	try (Statement stmt = conn.createStatement()) {
            String sql = "SHOW COLUMNS FROM " + colName;
            ResultSet resultSet = stmt.executeQuery(sql);
            StringBuilder resultText = new StringBuilder();
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                resultText.append(tableName).append("\n");
            }

            return resultText.toString();
        }
    }
}
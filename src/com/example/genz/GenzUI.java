package com.example.genz;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.servlet.annotation.WebServlet;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("genz")
public class GenzUI extends UI implements ClickListener{
	VerticalLayout layout;
	TextField kwh;
	TextField correlativo;
	TextField a;
	TextField b;
	TextField c;
	
	int clicks=0;
	

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = GenzUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		setContent(layout);

		kwh = new TextField("kWh");
		layout.addComponent(kwh);
		
		correlativo = new TextField("Correlativo");
		layout.addComponent(correlativo);
		
		//a = new TextField("A:");
		//layout.addComponent(a);
		
		//b = new TextField("B:");
		//layout.addComponent(b);
		
		//c = new TextField("C:");
		//layout.addComponent(c);
		
		Button button = new Button("Calcular");
		button.addClickListener(this);
		layout.addComponent(button);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		clicks++;
		//layout.addComponent(new Label("Son "+kwh.getValue()+" kWh"));
		String content = null;
		URLConnection connection = null;
		try {
		  connection =  new URL("http://www.eegsa.com/saldosyfacturas/factura/dofact.php?el=f&correlativo="+correlativo.getValue()).openConnection();
		  Scanner scanner = new Scanner(connection.getInputStream());
		  System.out.println(connection.getInputStream());
		  scanner.useDelimiter("\\Z");
		  content = scanner.next();
		  scanner.close();
		}catch ( Exception ex ) {
		    ex.printStackTrace();
		    
		}
		content = content.substring(content.indexOf("line1 = "));
		content = content.substring(0,content.indexOf(";"));
		String[] tokens = content.split(",");
		int[] valores = new int[6];
		int i = 1;
		int n =0;
		for (String string : tokens) {
			if(i%2==0){
				valores[n++]=Integer.parseInt(string.substring(0,string.indexOf("]")).trim());
			}
			i++;
		}
		
		int promedio=0;
		for (int j : valores) {
			promedio=promedio+j;
			
		}
		promedio=promedio/6;
		
		
		if (clicks>1){
			layout.removeComponent(layout.getComponent(layout.getComponentCount()-1));			
		}		
		layout.addComponent(new Label("Consumo promedio de: "+promedio+" kWh"));
		
	}

	

	

}
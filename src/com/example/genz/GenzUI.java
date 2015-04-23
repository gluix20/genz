package com.example.genz;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.servlet.annotation.WebServlet;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Image;
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
	TextField tf_nombre;
	
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
		
		// Serve the image from the theme
		Resource res = new ThemeResource("img/genztrans.png");
		// Display the image without caption
		Image image = new Image(null, res);
		image.setWidth("20%");
		layout.addComponent(image);
		//layout.setComponentAlignment(image,Alignment.MIDDLE_CENTER);
		

		kwh = new TextField("kWh:");
		
		layout.addComponent(kwh);
		kwh.setWidth("400px");
		
		correlativo = new TextField("Correlativo:");
		correlativo.setWidth("400px");
		layout.addComponent(correlativo);
		
		tf_nombre = new TextField("Nombre:");
		tf_nombre.setWidth("400px");
		layout.addComponent(tf_nombre);
		
			
		Button button = new Button("Calcular");
		button.addClickListener(this);
		layout.addComponent(button);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		clicks++;
		//layout.addComponent(new Label("Son "+kwh.getValue()+" kWh"));
		String content = null;
		String nombre = "";
		URLConnection connection = null;
		try {
		  connection =  new URL("http://www.eegsa.com/saldosyfacturas/factura/dofact.php?el=f&correlativo="+correlativo.getValue()).openConnection();
		  Scanner scanner = new Scanner(connection.getInputStream());
		  
		  scanner.useDelimiter("\\Z");
		  content = scanner.next();
		  scanner.close();
		  //System.out.println(content.toString());
		}catch ( Exception ex ) {
		    ex.printStackTrace();
		    
		}
		nombre = content.substring(content.indexOf("<tr><td class='text-small bold label'>Nombre</td><td class='text-small'>"));
		content = content.substring(content.indexOf("line1 = "));

		content = content.substring(0,content.indexOf(";"));
		nombre = nombre.substring("<tr><td class='text-small bold label'>Nombre</td><td class='text-small'>".length(),nombre.indexOf("</td></tr>"));
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
		//layout.addComponent(new Label("Nombre: "+nombre));
		tf_nombre.setValue(nombre);
		layout.addComponent(new Label("Consumo promedio de: "+promedio+" kWh"));
		
	}

	

	

}